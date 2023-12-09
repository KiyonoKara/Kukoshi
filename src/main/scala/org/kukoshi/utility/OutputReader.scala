package org.kukoshi.utility

import java.io.{InputStream, InputStreamReader, Reader}
import java.net.HttpURLConnection
import java.util.zip.{GZIPInputStream, InflaterInputStream}

object OutputReader {
  /**
   * Reads output of a connection established from HttpURLConnection
   * @param connection HttpURLConnection
   * @return Data as a String
   */
  def read(connection: HttpURLConnection): String = {
    val connectionInputStream: InputStream = connection.getInputStream
    // Set the reader to a null value before reading the output
    var reader: Reader = new InputStreamReader(connection.getInputStream)

    // GZIP & Deflate data streaming
    if (connection.getHeaderFields.containsKey("Content-Encoding")) {
      connection.getContentEncoding match {
        case "gzip" => reader = new InputStreamReader(new GZIPInputStream(connectionInputStream))
        case "deflate" => reader = new InputStreamReader(new InflaterInputStream(connectionInputStream))
      }
    }

    // Empty char value
    var ch: Int = 0
    val stringBuilder: StringBuilder = new StringBuilder()

    // Appends each character from the data to the StringBuilder
    while (ch != -1) {
      ch = reader.read()
      if (ch == -1) {
        return stringBuilder.toString()
      }
      stringBuilder.append(ch.toChar).toString
    }
    stringBuilder.toString
  }
}
