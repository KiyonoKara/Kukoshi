package org.kukoshi.utility

import java.io.{InputStream, InputStreamReader, Reader}
import java.net.HttpURLConnection
import java.util.zip.{DeflaterInputStream, GZIPInputStream}
import scala.collection.mutable.{StringBuilder => StrBuilder}

object OutputReader {
  /**
   * Reads output of a connection established via the HttpURLConnection class
   *
   * @param connection  HttpURLConnection
   * @return String of the output
   */
  def read(connection: HttpURLConnection): String = {
    val connectionInputStream: InputStream = connection.getInputStream
    // Set the reader to a null value before reading the output
    var reader: Reader = new InputStreamReader(connection.getInputStream)

    // GZIP & Deflate data streaming
    // TODO: correct this by checking the headers set by the program
    /*
    if (connection.getContentEncoding != null && connection.getContentEncoding.nonEmpty) {
      connection.getContentEncoding match {
        case "gzip" => reader = new InputStreamReader(new GZIPInputStream(connectionInputStream))
        case "deflate" => reader = new InputStreamReader(new DeflaterInputStream(connectionInputStream))
        case _ => reader = new InputStreamReader(connection.getInputStream)
      }
    }*/

    // Empty char value
    var ch: Int = 0

    // String Builder to add to the final string
    // StringBuilder => StrBuilder
    val stringBuilder: StrBuilder = new StrBuilder()

    // Appending the data to a String Builder
    while (ch != -1) {
      ch = reader.read()
      if (ch == -1) {
        return stringBuilder.toString()
      }
      stringBuilder.append(ch.asInstanceOf[Char]).toString
    }
    stringBuilder.toString
  }
}
