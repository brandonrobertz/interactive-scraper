package org.bxroberts.parse

import org.apache.nutch.parse.Outlink
import org.apache.nutch.parse.Parser
import org.apache.nutch.parse.ParseResult
import org.apache.nutch.parse.ParseStatus
import org.apache.nutch.protocol.Content
import org.apache.nutch.util.NutchConfiguration

import org.apache.hadoop.conf.Configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.invoke.MethodHandles

class InteractiveParser extends Parser {
  def getParse(content: Content): ParseResult = {
    log.info(s"URL: ${content.getUrl()}")
    log.info(s"BASE: ${content.getBaseUrl()}")
    log.info(s"CONTENT ${content.toString().length}")
    log.info(content.toString())

    val status = new ParseStatus(ParseStatus.SUCCESS)

    /**
     * AS PARSER
     * This extracts http links and also constructs iac links to other clickables):
     * It is assumed that the normal http parser will be able to extract any plain
     * anchor tags, so this one simply loads up the page in webdriver and constructs
     * more iac links. Or it can do both.
     * - unpack iac:// to normal http:// with crawl instructions
     * - load webdriver
     * - navigate to path
     * - save rendered HTML at end of path (main parseResult content)
     * - check to see all resources loaded at end of path
     * - if we have links to documents, add http links to parseResult
     * - grab all clickable links, create iac:// outlinks for those
     */

    /**
     * AS PROTOCOL
     * This follows iac links and grabs whatever lies at the end of it. The thinking
     * here is that if a clickable results in a list with a bunch of plain html hrefs
     * that normal plugins can parse that and we can also constrct more iac links if
     * we need to from it.
     * Implementing: ProtocolOutput getProtocolOutput(Text url, CrawlDatum datum) ...
     * - if url not iac://, bail
     * - load webdriver
     * - follow iac path
     * - return content from data at end of iac path
     */

    // var outlink = new Outlink(
    //   "intact://http://lol.com$$CLICK['btnsearch']", "Search"
    // )
    // val outlinks: Array[Outlink] = Array(outlink)
    return status.getEmptyParseResult(content.getUrl(), getConf())
  }

  // getters setters interface >:|
  var conf: Configuration = NutchConfiguration.create()
  def setConf(new_conf: Configuration ): Unit = (conf = new_conf)
  def getConf(): Configuration = conf

  // logging
  val log: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
}
