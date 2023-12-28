package org.kiyo

/**
 * Created 2021/08/19
 * File RequestUtils.scala
 */

import java.io.InputStream
import java.net.{HttpURLConnection, URI, URL, URLEncoder}
import java.util
import java.util.zip.{GZIPInputStream, InflaterInputStream}
import scala.collection.mutable
import scala.jdk.CollectionConverters.{ListHasAsScala, MapHasAsScala}

/**
 * Utility object
 */
object RequestUtils {
  /**
   * Creates an URL from a supplied base URL
   * then uses provided url parameters provided as an iterable collection of 2-element tuples
   * @param url           URL string
   * @param urlParameters URL parameters
   * @return Complete URL with the parameters
   */
  def createURL(url: String, urlParameters: Iterable[(String, String)]): String = {
    val newURL: URL = new URI(url).toURL
    // Determine the separator
    val separator: String = if (newURL.getQuery != null) "&" else "?"
    // Create URL parameters
    val encodedURLParameters: String = urlParameters.map({
      case (k, v) =>
        s"""${URLEncoder.encode(k, "UTF-8")}=${URLEncoder.encode(v, "UTF-8")}"""
    }).mkString("&")
    // Combine url, separator, and url parameters
    s"""$newURL$separator$encodedURLParameters"""
  }

  /**
   * Amends data from a Scala Map containing a String key and List[String] value into a string
   * @param map Map with the String and List of Strings
   * @return String
   */
  def amend(map: Map[String, List[String]]): String = {
    val strBuilder: StringBuilder = new StringBuilder()
    map.foreach(entry =>
      // The header key formatted with the list of headers separated by comma
      strBuilder.append("%s: %s%n".format(entry._1, entry._2.mkString(",")))
    )
    strBuilder.toString
  }

  /**
   * Reads output of a connection established from HttpURLConnection
   * @param connection HttpURLConnection
   * @return Data as a String
   */
  def readConnectionData(connection: HttpURLConnection): String = {
    val connectionInputStream: InputStream = connection.getInputStream

    // Get all headers and set all to lowercase
    val headerFields: mutable.Map[String, List[String]] = connection.getHeaderFields.asScala
      .filter(_._1 != null)
      .map((k, v) => (k.toLowerCase, v.asScala.toList))

    // GZIP & Deflate data streaming
    var compression: String = new String()
    if (headerFields.getOrElse("content-encoding", List.empty).nonEmpty) {
      val co_encoding: Seq[String] = headerFields.get("content-encoding").toSeq.flatten
      compression = co_encoding.head
    }

    this.decodeAndRead(connectionInputStream, compression)
  }

  /**
   * Uses a supplied input stream and specified content encoding and decodes accordingly
   * If the content encoding type isn't support, it will default to the supplied input stream
   * @param inputStream     The input stream
   * @param contentEncoding The type of content encoding (gzip or deflate)
   * @return The (decoded) content
   */
  def decodeAndRead(inputStream: InputStream, contentEncoding: String): String = {
    var reader: InputStream = inputStream

    // Determine whether content encoding is gzip or deflate
    contentEncoding match {
      case "gzip" => reader = new GZIPInputStream(inputStream)
      case "deflate" => reader = new InflaterInputStream(inputStream)
      case _ => ()
    }

    // Start with empty char value
    var ch: Int = 0
    val stringBuilder: StringBuilder = new StringBuilder()

    // Appends each character from the data to the StringBuilder
    while (ch != -1) {
      ch = reader.read()
      stringBuilder.append(ch.toChar)
    }
    stringBuilder.toString
  }

  /**
   * Implicit class to convert header maps into Scala maps
   * @param headerMap Java header map
   */
   implicit class ScalaHeaders(private val headerMap: util.Map[String, util.List[String]]) {
    /**
     * Converts Java header map into a Scala map
     * @return Scala version of the map
     */
    def asScalaHeaderMap: Map[String, List[String]] = {
      // Map into a Scala Map
      this.headerMap.asScala.map((k, v_list) => {
        k -> v_list.asScala.toList
      }).toMap
    }
  }
}
