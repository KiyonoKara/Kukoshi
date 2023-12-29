package org.kiyo

/**
 * Created 2021/08/19
 * File Request.scala
 */

import org.kiyo.{Constants, RequestUtils}
import org.kiyo.RequestUtils.ScalaHeaders

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, DataOutputStream, InputStream, OutputStream}
import java.lang.reflect.{Field, Modifier}
import java.net.http.HttpRequest.BodyPublisher
import java.net.http.{HttpClient, HttpHeaders, HttpRequest, HttpResponse}
import java.net.{HttpURLConnection, URI, URL}
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util
import java.util.zip.{DeflaterInputStream, GZIPInputStream}
import scala.io.Source.{fromBytes, fromInputStream}
import scala.jdk.CollectionConverters.MapHasAsScala


/**
 * Main class for HTTP/HTTPS requests
 * @param url            URL string
 * @param method         Request method
 * @param headers        Request headers as an iterable collection of 2-element tuples
 * @param readTimeout    Max timeout for reading the request
 * @param connectTimeout Max timeout for connecting
 */
case class Request(url: String = new String(),
              method: String = Constants.GET,
              headers: Iterable[(String, String)] = Iterable.empty,
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
              parameters: Iterable[(String, String)] = Iterable.empty,
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
      return this.decodeResponseData(this.httpBase(requestContext))
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
      content.append(RequestUtils.readConnectionData(connection))
      connection.getInputStream.close()
    }

    content.toString()
  }

  /**
   * Writes to an existing request using an existing HttpURLConnection
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

    val content: StringBuilder = new StringBuilder()
    try {
      // Write to the request
      val outputStream: DataOutputStream = new DataOutputStream(connection.getOutputStream)
      outputStream.write(byteArray, 0, byteArray.length)
      outputStream.flush()
      outputStream.close()

      // Get output of request
      if (connection.getResponseCode == HttpURLConnection.HTTP_OK) {
        content.append(RequestUtils.readConnectionData(connection))
      } else {
        val inputStream: InputStream = connection.getInputStream
        content.append(fromInputStream(inputStream).mkString)
        inputStream.close()
      }
    } finally {
      connection.disconnect()
    }
    content.toString
  }

  /**
   * Builds and sends a request based on the request context
   * Client may also specify if there is a payload to send and/or receive
   * @param ctx The request context
   * @return HttpResponse with an Array[Byte] or Void
   */
  private def httpBase[T](ctx: RequestContext): HttpResponse[T] = {
    // Base HttpClient builder
    val client_ : HttpClient.Builder = HttpClient.newBuilder()
      .connectTimeout(Duration.ofMillis(ctx.connectTimeout))

    // Choose between body publishers
    val bodyPublisher: HttpRequest.BodyPublisher =
      if (ctx.`sendsPayload?`)
        HttpRequest.BodyPublishers.ofString(ctx.data)
      else HttpRequest.BodyPublishers.noBody()

    // Base HttpRequest builder
    val request_ : HttpRequest.Builder = HttpRequest.newBuilder(URI.create(ctx.url))
      .method(ctx.method, bodyPublisher)
      .timeout(Duration.ofMillis(ctx.readTimeout))

    // Set headers
    for ((k, v) <- ctx.headers) request_.setHeader(k, v)

    val client: HttpClient = client_.build()

    val bodyHandler: HttpResponse.BodyHandler[T] =
      if (ctx.`receivesPayload?`) HttpResponse.BodyHandlers.ofByteArray().asInstanceOf[HttpResponse.BodyHandler[T]]
      else HttpResponse.BodyHandlers.discarding().asInstanceOf[HttpResponse.BodyHandler[T]]

    val response: HttpResponse[T] = client.send(request_.build(), HttpResponse.BodyHandlers.ofByteArray()).asInstanceOf[HttpResponse[T]]
    response
  }

  /**
   * Quickly searches for any content encoding header values then reads the data
   * @param response The response from the HttpResponse
   * @return The data (decoded if it was encoded via gzip or deflate)
   */
  private def decodeResponseData(response: HttpResponse[Array[Byte]]): String = {
    // Pass response body into an input stream
    val byteArrayIS: ByteArrayInputStream = new ByteArrayInputStream(response.body())
    // Search for content encoding
    val contentEncoding: String = response.headers().firstValue("Content-Encoding").orElse("")
    // Pass into the reading function and determine how to decode (if there was any encoding)
    RequestUtils.decodeAndRead(byteArrayIS, contentEncoding)
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
           headers: Iterable[(String, String)] = Iterable.empty): String = {
    val response: HttpResponse[Array[Byte]] = this.httpBase(RequestContext(
      url = url,
      method = Constants.POST,
      data = data,
      `sendsPayload?` = true,
      headers = headers,
      readTimeout = this.readTimeout,
      connectTimeout = this.connectTimeout)
    )

    this.decodeResponseData(response)
  }

  /**
   * Creates a HEAD request, there is no written body from HEAD requests
   * @param url URL string
   * @return Map with all response headers
   */
  def head(url: String = this.url, headers: Iterable[(String, String)] = this.headers): Map[String, List[String]] = {
    // Use the request context then map the headers
    this.httpBase[Void](
        RequestContext(
          url = url,
          method = Constants.HEAD,
          `receivesPayload?` = false,
          headers = headers,
          readTimeout = this.readTimeout,
          connectTimeout = this.connectTimeout
        )
      )
      .headers()
      .map()
      .asScalaHeaderMap
  }

  /**
   * Makes an OPTIONS request and gets the options of a request, identifies allowed methods. May not work with some URLs that are requested due to Cross-Origin Resource Sharing
   * @param url Provide an URL
   * @return Map of the response headers with the options
   */
  def options(url: String = this.url, headers: Iterable[(String, String)] = this.headers): Map[String, List[String]] = {
    // Use the request context then map the headers
    this.httpBase[Void](
        RequestContext(
          url = url,
          method = Constants.OPTIONS,
          `receivesPayload?` = false,
          headers = headers,
          readTimeout = this.readTimeout,
          connectTimeout = this.connectTimeout
        ))
      .headers()
      .map()
      .asScalaHeaderMap
  }
}
