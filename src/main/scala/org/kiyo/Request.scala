package org.kiyo

/**
 * Created by KiyonoKara - 2021/08/19
 * File Request.scala
 */

import org.kiyo.Utility.Constants

import java.io.{ByteArrayOutputStream, DataOutputStream, InputStream, OutputStream}
import java.lang.reflect.{Field, Modifier}
import java.net.http.{HttpClient, HttpHeaders, HttpRequest, HttpResponse}
import java.net.{HttpURLConnection, URI, URL}
import java.nio.charset.StandardCharsets
import java.util
import scala.io.Source.fromInputStream
import scala.jdk.CollectionConverters._

/**
 * Main class for HTTP/HTTPS requests
 * @param url     URL string
 * @param method  Request method
 * @param headers Request headers as an iterable collection of 2-element tuples
 * @param readTimeout Max timeout for reading the request
 * @param connectTimeout Max timeout for connecting
 */
class Request(url: String = new String(), method: String = Constants.GET, headers: Iterable[(String, String)] = Map(
                "Accept-Encoding" -> "gzip, deflate",
                "Connection" -> "keep-alive"),
                readTimeout: Int = 15 * 1000,
                connectTimeout: Int = 15 * 1000) {


  /**
   * The request method for doing HTTP/HTTPS requests
   * @param url        URL string
   * @param method     HTTP request method
   * @param headers    Iterable[(String, String)], request headers
   * @param data       Data to pass into the request body
   * @param parameters URL parameters for querying
   * @param readTimeout Max timeout for reading the request
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
    val requestURL: String = Utility.createURL(url, parameters)
    val parsedURL: URL = URI.create(requestURL).toURL

    // Create the connection from the provided URL
    val connection: HttpURLConnection = parsedURL.openConnection.asInstanceOf[HttpURLConnection]

    connection.setReadTimeout(connectTimeout)
    connection.setConnectTimeout(connectTimeout)
    connection.setUseCaches(false)
    connection.setDoOutput(true)

    // Set the request method
    if (Constants.HTTPMethods.contains(methodUpperCase)) {
      connection.setRequestMethod(methodUpperCase)
    } else {
      // For methods not supported by HttpURLConnection

    }

    // Sets headers
    for ((k, v) <- headers) connection.setRequestProperty(k, v)

    // The content
    val content: StringBuilder = new StringBuilder()

    // Methods that write to the requests
    val writeableMethods: Set[String] = Set(Constants.POST, Constants.DELETE, Constants.PUT, Constants.PATCH)

    if (methodUpperCase.equals(Constants.GET)) {
      content.append(OutputReader.read(connection))
      connection.getInputStream.close()
      return content.toString
    } else if (writeableMethods.contains(methodUpperCase)) {
      content.append(OutputReader.read(connection))
      return this.writeToRequest(connection, data)
    }

    // In case everything fails
    val inputStream = connection.getInputStream
    content.clear()
    content.append(fromInputStream(inputStream).mkString)
    if (inputStream != null) inputStream.close()
    // Return content
    content.toString()
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
        content = OutputReader.read(connection)
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
    val client: HttpClient = HttpClient.newHttpClient()
    val headRequest: HttpRequest = HttpRequest.newBuilder(URI.create(url))
      .method(Constants.HEAD, HttpRequest.BodyPublishers.noBody())
      .build()

    val response: HttpResponse[Void] = client.send(headRequest, HttpResponse.BodyHandlers.discarding())
    val responseHeaders: HttpHeaders = response.headers()
    responseHeaders.map.forEach((k, v) => {
      headers.put(k, v.asScala.toList)
    })

    headers.asScala.toMap
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
           headers: Iterable[(String, String)] = Iterable.empty[(String, String)],
           version: String = HttpClient.Version.HTTP_2.toString): String = {
    val client: HttpClient = HttpClient.newBuilder()
      .version(HttpClient.Version.valueOf(version.toUpperCase))
      .build()

    val request: HttpRequest.Builder = HttpRequest.newBuilder()
      .POST(HttpRequest.BodyPublishers.ofString(data))
      .uri(URI.create(url))

    if (headers.nonEmpty) {
      headers.foreach(i => {
        request.setHeader(i._1, i._2)
      })
    }

    val response: HttpResponse[String] = client.send(request.build(), HttpResponse.BodyHandlers.ofString())
    response.body
  }

  /**
   * Makes an OPTIONS request and gets the options of a request, identifies allowed methods. May not work with some URLs that are requested due to Cross-Origin Resource Sharing
   * @param url     Provide an URL
   * @param version Provide an optional HTTP version, HTTP_2 or HTTP_1_1 are valid
   * @return Map of the response headers with the options
   */
  def options(url: String = this.url, version: String = HttpClient.Version.HTTP_2.toString): Map[String, List[String]] = {
    val client: HttpClient = HttpClient.newBuilder()
      .version(HttpClient.Version.valueOf(version.toUpperCase))
      .build()

    val request: HttpRequest.Builder = HttpRequest.newBuilder()
      .method(Constants.OPTIONS, HttpRequest.BodyPublishers.noBody())
      .uri(URI.create(url))

    val response: HttpResponse[String] = client.send(request.build(), HttpResponse.BodyHandlers.ofString())

    val responseHeaders = response.headers().map()
    responseHeaders.asScala.view.map((k, v) => (k, v.asScala.toList)).toMap
  }
}
