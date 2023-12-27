package org.kiyo

/**
 * Created 2021/08/19
 * File OutputReader.scala
 */

import java.io.InputStream
import java.net.HttpURLConnection
import java.util.zip.{GZIPInputStream, InflaterInputStream}
import scala.collection.mutable
import scala.jdk.CollectionConverters._

protected object OutputReader {
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
   * @param inputStream The input stream
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
}
