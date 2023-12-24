package org.kiyo

/**
 * HTTP-related constants
 */
protected object Constants {
  val GET: String = "GET"
  val POST: String = "POST"
  val DELETE: String = "DELETE"
  val PUT: String = "PUT"
  val HEAD: String = "HEAD"
  val OPTIONS: String = "OPTIONS"
  val PATCH: String = "PATCH"

  // Methods HttpURLConnection supports
  val HTTPMethods: Set[String] = Set(GET, POST, DELETE, PUT, HEAD, OPTIONS)
}
