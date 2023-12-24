package org.kiyo

/**
 * Created 2021/08/19
 * File RequestUtils.scala
 */

import java.net.{HttpURLConnection, URI, URL, URLEncoder}

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
    val separator: String = if (newURL.getQuery != null) "&" else "?"
    val encodedURLParameters: String = urlParameters.map({
      case (k, v) =>
        s"""${URLEncoder.encode(k, "UTF-8")}=${URLEncoder.encode(v, "UTF-8")}"""
    }).mkString("&")
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
      strBuilder.append("%s: %s%n".format(entry._1, entry._2.mkString(",")))
    )
    strBuilder.toString
  }
}
