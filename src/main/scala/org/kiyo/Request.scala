package org.kiyo

/**
 * Created 2021/08/19
 * File Request.scala
 */

import org.kiyo.Constants

import java.io.{ByteArrayOutputStream, DataOutputStream, InputStream, OutputStream}
import java.lang.reflect.{Field, Modifier}
import java.net.http.HttpRequest.BodyPublisher
import java.net.http.{HttpClient, HttpHeaders, HttpRequest, HttpResponse}
import java.net.{HttpURLConnection, URI, URL}
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util
import scala.io.Source.fromInputStream
import scala.jdk.CollectionConverters._

/**
 * Main class for HTTP/HTTPS requests
 * @param url            URL string
 * @param method         Request method
 * @param headers        Request headers as an iterable collection of 2-element tuples
 * @param readTimeout    Max timeout for reading the request
 * @param connectTimeout Max timeout for connecting
 */
class Request(url: String = new String(),
              method: String = Constants.GET,
              headers: Iterable[(String, String)] = Iterable.empty[(String, String)],
              readTimeout: Int = 15 * 1000,
              connectTimeout: Int = 15 * 1000) {


  /**
   * The request method for doing HTTP/HTTPS requests
   * @param url            URL string
   * @param method         HTTP request method
   * @param headers        Iterable[(String, String)], request headers
   * @param data           Data to pass into the request body
   * @param parameters     URL parameters for querying
   * @param readTimeout    Max timeout for reading the request
   * @param connectTimeout Max timeout for connecting
   * @return Request output as a string
   */
  def request(url: String = this.url,
              method: String = this.method,
              headers: Iterable[(String, String)] = this.headers,
              data: String = new String(),
              parameters: Iterable[(String, String)] = Iterable.empty[(String, String)],
              readTimeout: Int = this.readTimeout,
              connectTimeout: Int = this.connectTimeout): String = {
    // Set method to uppercase
    val methodUpperCase = method.toUpperCase()

    // Parse the URL along with the parameters
    val requestURL: String = RequestUtils.createURL(url, parameters)
    val parsedURL: URL = URI.create(requestURL).toURL

    // Create the connection from the provided URL
    val connection: HttpURLConnection = parsedURL.openConnection.asInstanceOf[HttpURLConnection]

    // Set the request method
    if (Constants.HTTPMethods.contains(methodUpperCase)) {
      connection.setRequestMethod(methodUpperCase)
    } else if (Constants.otherHTTPMethods.contains(methodUpperCase)) {
      // For methods not supported by HttpURLConnection
      val requestContext: RequestContext = RequestContext(
        url = url,
        method = method,
        data = data,
        headers = headers,
        readTimeout = readTimeout,
        connectTimeout = connectTimeout)
      return this.modifierDataRequest(requestContext)
    }

    connection.setReadTimeout(connectTimeout)
    connection.setConnectTimeout(connectTimeout)
    connection.setUseCaches(false)
    connection.setDoOutput(true)

    // Sets headers
    for ((k, v) <- headers) connection.setRequestProperty(k, v)

    // The content
    val content: StringBuilder = new StringBuilder()

    // Methods that write to the requests
    val writeableMethods: Set[String] = Set(Constants.POST, Constants.DELETE, Constants.PUT)

    if (writeableMethods.contains(methodUpperCase)) {
      return this.writeToRequest(connection, data)
    } else {
      content.append(OutputReader.readConnectionData(connection))
      connection.getInputStream.close()
    }

    content.toString()
  }

  /**
   * Intended for requests that modify resources (and aren't supported by HttpURLConnection) and submit data
   * @param ctx Request context
   * @return Response body
   */
  private def modifierDataRequest(ctx: RequestContext): String = {
    val (client_, request_): (HttpClient.Builder, HttpRequest.Builder) = this.httpBase(ctx)
    val client: HttpClient = client_.build()

    val response: HttpResponse[String] = client.send(request_.build(), HttpResponse.BodyHandlers.ofString())
    response.body
  }

  /**
   * Provides a base HttpClient and HttpRequest builder for abstraction
   * @param ctx Request context
   * @return HttpClient and HttpRequest builders
   */
  private def httpBase(implicit ctx: RequestContext): (HttpClient.Builder, HttpRequest.Builder) = {
    // Base HttpClient builder
    val client_ : HttpClient.Builder = HttpClient.newBuilder()
      .connectTimeout(Duration.ofMillis(ctx.connectTimeout))

    // Choose between body publishers
    val bodyPublisher: HttpRequest.BodyPublisher =
      if (ctx.hasBody)
        HttpRequest.BodyPublishers.ofString(ctx.data)
      else HttpRequest.BodyPublishers.noBody()

    // Base HttpRequest builder
    val request_ : HttpRequest.Builder = HttpRequest.newBuilder(URI.create(ctx.url))
      .method(ctx.method, bodyPublisher)
      .timeout(Duration.ofMillis(ctx.readTimeout))

    // Set headers
    for ((k, v) <- ctx.headers) request_.setHeader(k, v)

    (client_, request_)
  }

  /**
   * Writes to a request
   * @param connection HttpURLConnection, existing connection
   * @param data       Data or body to write to the request
   * @return Request output
   */
  private def writeToRequest(connection: HttpURLConnection, data: String): String = {
    // Check data
    if (data.isEmpty) {
      val inputStream: InputStream = connection.getInputStream
      val content: String = fromInputStream(inputStream).mkString
      inputStream.close()
      return content
    }

    // Put byte into output stream
    val byteArrayOutputStream: ByteArrayOutputStream = new ByteArrayOutputStream()
    val dataBytes: Array[Byte] = data.getBytes(StandardCharsets.UTF_8)
    byteArrayOutputStream.write(dataBytes, 0, dataBytes.length)

    // Turn byte array output stream into a byte array
    val byteArray: Array[Byte] = byteArrayOutputStream.toByteArray
    connection.setFixedLengthStreamingMode(byteArray.length)

    var content: String = new String()
    try {
      // Write to the request
      val outputStream: DataOutputStream = new DataOutputStream(connection.getOutputStream)
      outputStream.write(byteArray, 0, byteArray.length)
      outputStream.flush()
      outputStream.close()

      // Get output of request
      if (connection.getResponseCode == HttpURLConnection.HTTP_OK) {
        content = OutputReader.readConnectionData(connection)
      } else {
        val inputStream: InputStream = connection.getInputStream
        content = fromInputStream(inputStream).mkString
        inputStream.close()
      }
    } catch {
      case error: Error =>
        error.printStackTrace()
        error.toString
    } finally {
      connection.disconnect()
    }
    content
  }

  /**
   * Creates a HEAD request, there is no written body from HEAD requests
   * @param url URL string
   * @return Map with all response headers
   */
  def head(url: String = this.url): Map[String, List[String]] = {
    val headers: util.HashMap[String, List[String]] = new util.HashMap[String, List[String]]
    val (client_, request_): (HttpClient.Builder, HttpRequest.Builder) = this.httpBase(RequestContext(
      url = url,
      method = Constants.HEAD,
      hasBody = false,
      readTimeout = this.readTimeout,
      connectTimeout = this.connectTimeout
    ))

    val client: HttpClient = client_.build()

    val response: HttpResponse[Void] = client.send(request_.build(), HttpResponse.BodyHandlers.discarding())
    val responseHeaders: HttpHeaders = response.headers()

    responseHeaders.map.asScala.map((k, v_list) => {
      k -> v_list.asScala.toList
    }).toMap
  }

  /**
   * Makes a simple and fast POST request using Java's HTTP Client.
   * @param url     Target URL for POST request.
   * @param data    The data / body used to send.
   * @param headers Headers as an iterable collection with 2-element tuples
   * @return Written output (if any) from the POST request
   */
  def post(url: String = this.url,
           data: String = new String(),
           headers: Iterable[(String, String)] = Iterable.empty[(String, String)]): String = {
    this.modifierDataRequest(RequestContext(
      url = url,
      method = Constants.POST,
      data = data,
      headers = headers,
      readTimeout = this.readTimeout,
      connectTimeout = this.connectTimeout)
    )
  }

  /**
   * Makes an OPTIONS request and gets the options of a request, identifies allowed methods. May not work with some URLs that are requested due to Cross-Origin Resource Sharing
   * @param url     Provide an URL
   * @return Map of the response headers with the options
   */
  def options(url: String = this.url): Map[String, List[String]] = {
    val (client_, request_): (HttpClient.Builder, HttpRequest.Builder) = this.httpBase(RequestContext(
      url = url,
      method = Constants.OPTIONS,
      hasBody = false,
      readTimeout = this.readTimeout,
      connectTimeout = this.connectTimeout
    ))

    val client: HttpClient = client_.
      version(HttpClient.Version.HTTP_2)
      .build()

    val response: HttpResponse[String] = client.send(request_.build(), HttpResponse.BodyHandlers.ofString())

    val responseHeaders = response.headers().map()
    responseHeaders.asScala.map((k, v) => (k, v.asScala.toList)).toMap
  }
}
