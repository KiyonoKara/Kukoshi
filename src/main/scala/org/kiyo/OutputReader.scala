package org.kiyo

/**
 * Created by KiyonoKara - 2021/08/19
 * File OutputReader.scala
 */

import java.io.InputStream
import java.net.HttpURLConnection
import java.util.zip.{GZIPInputStream, InflaterInputStream}
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

protected object OutputReader {
  /**
   * Reads output of a connection established from HttpURLConnection
   * @param connection HttpURLConnection
   * @return Data as a String
   */
  def read(connection: HttpURLConnection): String = {
    val connectionInputStream: InputStream = connection.getInputStream
    var reader: InputStream = connectionInputStream

    // Get all headers and set all to lowercase
    val headerFields: mutable.Map[String, List[String]] = connection.getHeaderFields.asScala
      .filter(_._1 != null)
      .map((k, v) => (k.toLowerCase, v.asScala.toList))

    // GZIP & Deflate data streaming
    // Deprecated: connection.getHeaderFields.containsKey("Content-Encoding")
    if (headerFields.getOrElse("content-encoding", List.empty).nonEmpty) {
      val co_en = headerFields.get("content-encoding").toSeq.flatten
      co_en match {
        case gzip if gzip.contains("gzip") => reader = new GZIPInputStream(connectionInputStream)
        case deflate if deflate.contains("deflate") => reader = new InflaterInputStream(connectionInputStream)
        case _ => ()
      }
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
