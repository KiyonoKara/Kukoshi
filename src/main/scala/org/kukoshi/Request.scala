package org.kukoshi

/**
 * Created by KiyonoKara - 08/19/2021
 * File Request.scala
 */

// Networking and web

import java.net.{HttpURLConnection, URI, URL}

// Java HTTP
import java.net.http.{HttpClient, HttpHeaders, HttpRequest, HttpResponse}

// IO & NIO
import java.io.{InputStream, OutputStream}
import java.nio.charset.StandardCharsets

// Scala IO Source
import scala.io.Source.fromInputStream

// Local utilities
import org.kukoshi.utility.Utility.Constants
import org.kukoshi.utility.{OutputReader, Utility}

// Other
import java.lang.reflect.Field
import java.time.Duration
import java.util
import scala.jdk.CollectionConverters._

/**
 * Main class for making HTTP/HTTPS requests
 *
 * @param url     String; Provide an URL with its path (if you are requesting with the path)
 * @param method  String; Request method, refer to the Constants file for supported methods
 * @param headers Iterable[(String, String)]; Headers in the form of a Map collection is primarily valid
 */
class Request(var url: String = "",
              var method: String = Constants.GET,
              headers: Iterable[(String, String)] = Map(
                "Accept-Encoding" -> "gzip, deflate",
                "Connection" -> "keep-alive")
             ) {
  private lazy val methodField: Field = {
    val method = classOf[HttpURLConnection].getDeclaredField("method")
    method.setAccessible(true)
    method
  }


  /**
   * The request method for the class that completes the HTTP/HTTPS requests
   *
   * @param url        String; Provide an URL
   * @param method     String; Request method, defaults to the class' default method
   * @param headers    Iterable[(String, String)]; Headers for requesting
   * @param data       String; Preferably JSON data that is in the form of a string
   * @param parameters Iterable[(String, String)]; URL parameters that can be used for querying
   * @return Output as a string
   */
  def request(url: String = this.url, method: String = this.method, headers: Iterable[(String, String)] = this.headers, data: String = null, parameters: Iterable[(String, String)] = Nil): String = {
    // Parse the URL along with the parameters
    val requestURL: String = Utility.createURL(url, parameters)
    val parsedURL: URL = new URL(requestURL)

    // Create the connection from the provided URL
    val connection: HttpURLConnection = parsedURL.openConnection.asInstanceOf[HttpURLConnection]

    // Set the request method
    if (Constants.HTTPMethods.contains(method.toUpperCase)) {
      connection.setRequestMethod(method.toUpperCase)
    } else {
      /** For PATCH requests, the method will default to POST.
       * PATCH requests can still be done with X-HTTP-Method-Override header that changes the request method.
       *
       * Example for adding the PATCH override:
       * {{{
       *   val PATCH: String = new Request().request("http://localhost:8080/echo",
       *                                         "PATCH", Map("Accept" -> "*",
       *                                         "User-Agent" -> "*",
       *                                         "X-HTTP-Method-Override" -> "PATCH"),
       *                                         data = "{\"message\": \"PATCH message\"}")
       * }}}
       *
       */
      connection match {
        case httpURLConnection: HttpURLConnection =>
          httpURLConnection.getClass.getDeclaredFields.find(_.getName == "delegate") foreach { i =>
            i.setAccessible(true)
            this.methodField.set(i.get(httpURLConnection), method.toUpperCase)
          }
      }
    }

    // Timeouts
    connection.setConnectTimeout(Constants.DEFAULT_TIMEOUT)
    connection.setReadTimeout(Constants.DEFAULT_TIMEOUT)

    // Sets headers
    if (headers.nonEmpty) {
      Utility.setHeaders(connection, headers)
    }

    if (method.toUpperCase.equals(Constants.GET)) {
      return OutputReader.read(connection)
    }

    if (method.toUpperCase.equals(Constants.POST) || method.toUpperCase.equals(Constants.DELETE) || method.toUpperCase.equals(Constants.PUT) || method.toUpperCase.equals(Constants.PATCH)) {
      return this.writeToRequest(connection, method, data)
    }

    // Input stream for data with a GET request if all of the requests fail
    val inputStream = connection.getInputStream
    val content = fromInputStream(inputStream).mkString
    if (inputStream != null) inputStream.close()
    // Return the content or data, read-only
    content
  }


  /**
   * Writes to a request
   *
   * @param connection HttpURLConnection; The connection established will be used so it can be written to.
   * @param method     A method is always required, cannot default to a common request method
   * @param data       Preferably JSON data in the form of a string.
   * @return output Generally returns the output of the Output Reader
   */
  private def writeToRequest(connection: HttpURLConnection, method: String, data: String): String = {
    val theMethod: String = method.toUpperCase
    if (theMethod.equals(Constants.POST) || theMethod.equals(Constants.PUT) || theMethod.equals(Constants.PATCH)) connection.setDoOutput(true)

    // Processing the data
    if (data.isEmpty) {
      val inputStream: InputStream = connection.getInputStream
      val content: String = fromInputStream(inputStream).mkString
      inputStream.close()
      return content
    }
    val byte: Array[Byte] = data.getBytes(StandardCharsets.UTF_8)
    val length: Int = byte.length
    connection.setFixedLengthStreamingMode(length)

    try {
      // Write to the request
      val outputStream: OutputStream = connection.getOutputStream
      outputStream.write(byte, 0, byte.length)
      if (theMethod.equals(Constants.POST)) {
        outputStream.flush()
        outputStream.close()
      }
      // Get output of request
      val inputStream: InputStream = connection.getInputStream
      if (connection.getContentEncoding != null && connection.getContentEncoding.nonEmpty) {
        val content: String = OutputReader.read(connection)
        content
      } else {
        val content: String = fromInputStream(inputStream).mkString
        inputStream.close()
        content
      }
    } catch {
      case error: Error =>
        error.printStackTrace()
        error.toString
    }
  }

  /**
   * Creates a HEAD request that gets the headers of the response, there is no body from HEAD requests nor its responses.
   *
   * @param url Provide an URL for making the HEAD request
   * @return A Map with all the response headers, this is not the body of the request
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
   *
   * @param url     URL for making the POST request.
   * @param data    The data / body used for the POST request.
   * @param headers Headers for indicating content type or etc.
   * @return Written output from the POST request, most POST requests will have some type of output.
   */
  def post(url: String = this.url, data: String = new String(), headers: Iterable[(String, String)] = Nil, version: String = HttpClient.Version.HTTP_2.toString): String = {
    val client: HttpClient = HttpClient.newBuilder()
      .version(HttpClient.Version.valueOf(version.toUpperCase))
      .build()

    val request: HttpRequest.Builder = HttpRequest.newBuilder()
      .POST(HttpRequest.BodyPublishers.ofString(if (data == null) return new StringBuilder().toString() else data))
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
   *
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
    val optionHeaders: Map[String, List[String]] = Map("Allow" -> responseHeaders.get("Allow").asScala.toList)
    optionHeaders
  }

  /**
   * Amends headers from a Map containing a String key and List[String] value into a neat and organized string
   *
   * @param map Map with the String and List with Strings
   * @return String
   */
  def amend(map: Map[String, List[String]]): String = {
    var str: String = new String()
    map.foreach(entry => {
      str += "%s: %s%n".format(entry._1, entry._2.mkString(","))
    })
    str
  }
}
