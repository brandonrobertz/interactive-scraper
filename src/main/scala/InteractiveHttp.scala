package org.bxroberts.http

import java.net.URL
import collection.mutable.HashMap

import org.apache.hadoop.io.Text

import org.apache.nutch.crawl.CrawlDatum
import org.apache.nutch.net.protocols.Response
import org.apache.nutch.metadata.Metadata
import org.apache.nutch.protocol.Content
import org.apache.nutch.protocol.http.api.HttpBase
import org.apache.nutch.protocol.http.api.HttpException
import org.apache.nutch.net.protocols.Response

class InteractiveHttpResponse(url: URL, datum: CrawlDatum) extends Response {
  var code: Int = 0
  var headers: HashMap[String, String] = HashMap.empty
  var content: Array[Byte] = Array[Byte]()

  //public URL getUrl();
  def getUrl(): URL = url

  /** Returns the response code. */
  //public int getCode();
  def getCode(): Int = code

  /** Returns the value of a named header. */
  //public String getHeader(String name);
  def getHeader(name: String): String = headers(name)

  /** Returns all the headers. */
  //public Metadata getHeaders();
  def getHeaders(): Metadata = new Metadata()

  /** Returns the full content of the response. */
  //public byte[] getContent();
  def getContent(): Array[Byte] = content

}

class InteractiveHttp extends HttpBase {
  def getResponse(url: URL, datum: CrawlDatum, redirect: Boolean): Response = {
    new InteractiveHttpResponse(url, datum)
  }
}

