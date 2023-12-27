package org.kiyo

/**
 * Created 2021/08/19
 * File RequestUtils.scala
 */

import java.net.{HttpURLConnection, URI, URL, URLEncoder}
import java.util
import scala.jdk.CollectionConverters.*

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
