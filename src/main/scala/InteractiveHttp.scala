package org.bxroberts.protocol.http

import collection.mutable.HashMap

import org.apache.hadoop.io.Text

import org.apache.nutch.crawl.CrawlDatum
import org.apache.nutch.net.protocols.Response
import org.apache.nutch.metadata.Metadata
import org.apache.nutch.protocol.Content
import org.apache.nutch.protocol.http.api.HttpBase
import org.apache.nutch.protocol.http.api.HttpException
import org.apache.nutch.net.protocols.Response

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.net.URL
import java.lang.invoke.MethodHandles



class InteractiveHttpResponse(url: URL, datum: CrawlDatum) extends Response {
  var code: Int = 0
  var headers: HashMap[String, String] = HashMap.empty
  // setHeader = headers.put(Key, Value)
  var content: String = ""

  /**
   * AS PROTOCOL
   * This follows iac links and grabs whatever lies at the end of it. The thinking
   * here is that if a clickable results in a list with a bunch of plain html hrefs
   * that normal plugins can parse that and we can also constrct more iac links if
   * we need to from it.
   * - if url not iac://, bail
   * - load webdriver
   * - follow iac path
   * - return content from data at end of iac path
   */

  // JAVA INTERFACE
  /** Get original URL **/
  def getUrl(): URL = url

  /** Returns the response code. */
  def getCode(): Int = code

  /** Returns the value of a named header. */
  def getHeader(name: String): String = headers(name)

  /** Returns all the headers. */
  def getHeaders(): Metadata = new Metadata()

  /** Returns the full content of the response. */
  def getContent(): Array[Byte] = content.map(_.toByte).toArray
}

class InteractiveHttp extends HttpBase {
  def getResponse(url: URL, datum: CrawlDatum, redirect: Boolean): Response = {
    log.info("HEEEEEEEEEEEELLLLLLLLLLLLLLLLLLLLLLOOOOO InteractiveHttp")
    new InteractiveHttpResponse(url, datum)
  }

  // logging
  val log: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
}

