
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

package org.bxroberts.parse {
  class InteractiveParser extends Parser {
    def getParse(content: Content): ParseResult = {
      log.info(s"URL: ${content.getUrl}")
      log.info(s"BASE: ${content.getBaseUrl}")
      //log.info(s"CONTENT ${content.toString}")
      log.info(content.toString)

      val status = new ParseStatus(ParseStatus.SUCCESS)

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
}
