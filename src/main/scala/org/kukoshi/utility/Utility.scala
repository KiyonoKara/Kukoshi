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

  def setHeaders(connection: HttpURLConnection, headers: Iterable[(String, String)] = Nil): Unit = {
    if (headers.nonEmpty) {
      headers foreach {
        case (key, value) =>
          try {
            connection.setRequestProperty(key, value)
          } catch {
            case _: Any => ()
          }
      }
    }
  }

  /**
   * HTTP-related constants
   * Import example
   * {{{
   *   import org.kukoshi.Utility.Constants
   *   val GET: String = Constants.GET
   * }}}
   */
  object Constants {
    /**
     * Main HTTP/HTTPS methods
     */
    val GET: String = "GET"
    val POST: String = "POST"
    val DELETE: String = "DELETE"
    val PUT: String = "PUT"
    val HEAD: String = "HEAD"
    val OPTIONS: String = "OPTIONS"
    val PATCH: String = "PATCH"

    /**
     * Officially supported methods
     */
    val HTTPMethods: Set[String] = Set(GET, POST, DELETE, PUT, HEAD, OPTIONS)
  }
}
