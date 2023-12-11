package org.kukoshi.utility

import java.net.{HttpURLConnection, URI, URL, URLEncoder}

/**
 * Utility class
 */
object Utility {
  /**
   * Creates string of URL parameters
   * @param urlParameters URL parameters as an iterable collection
   * @return Formatted URL parameters
   */
  private def encodeURLParameters(urlParameters: Iterable[(String, String)]): String = {
    urlParameters.map({
      case (k, v) =>
        s"""${URLEncoder.encode(k, "UTF-8")}=${URLEncoder.encode(v, "UTF-8")}"""
    }).mkString("&")
  }

  /**
   * Creates an URL from a supplied base URL
   * then uses provided url parameters provided as an iterable collection of 2-element tuples
   * @param url           URL string
   * @param urlParameters URL parameters
   * @return Complete URL with the parameters
   */
  def createURL(url: String, urlParameters: Iterable[(String, String)]): String = {
    val newURL: URL = new URL(new URI(url).toASCIIString)
    val separator: String = if (newURL.getQuery != null) "&" else "?"
    val encodedURLParameters: String = Utility.encodeURLParameters(urlParameters)
    s"""$newURL$separator$encodedURLParameters"""
  }

  /**
   * Amends data from a Scala Map containing a String key and List[String] value into a string
   * @param map Map with the String and List of Strings
   * @return String
   */
  def amend(map: Map[String, List[String]]): String = {
    val strBuilder: StringBuilder = new StringBuilder()
    map.foreach(entry => {
      strBuilder.append("%s: %s%n".format(entry._1, entry._2.mkString(",")))
    })
    strBuilder.toString
  }

  /**
   * HTTP-related constants
   */
  object Constants {
    val GET: String = "GET"
    val POST: String = "POST"
    val DELETE: String = "DELETE"
    val PUT: String = "PUT"
    val HEAD: String = "HEAD"
    val OPTIONS: String = "OPTIONS"
    val PATCH: String = "PATCH"
    
    // Methods the library supports
    val HTTPMethods: Set[String] = Set(GET, POST, DELETE, PUT, HEAD, OPTIONS)
  }
}
