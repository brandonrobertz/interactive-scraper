package org.bxroberts.interactivescraper

import org.bxroberts.interactivescraper.Crawl
import collection.mutable.HashMap
import java.util.concurrent.TimeUnit
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.io.TemporaryFilesystem

import org.json4s.native.JsonParser

object InteractiveCrawler {
  // Interactive Searchable
  // TODO: make these commandline arguments
  var scrapeInfo: HashMap[String,HashMap[String,String]] = HashMap(
    "https://bxroberts.org/" -> HashMap(
      "input-type" -> "character-2",
      "wildcard-type" -> "%"
    ),
    "http://registrar.utexas.edu/students/degrees/verify" -> HashMap(
      "input-type" -> "character-1",
    )
  )

  def loadData(): HashMap[String,HashMap[String,String]] = {
    val filename = "conf/datasources.json"
    val data = scala.io.Source.fromFile(filename).mkString
    var parsed = JsonParser.parse(data)
    println(parsed)
    return scrapeInfo
  }

  def getDriver(): WebDriver = {
    return new ChromeDriver()
  }

  def closeDriver(driver: WebDriver) {
    driver close()
    driver quit()
    TemporaryFilesystem.getDefaultTmpFS().deleteTemporaryFiles()
  }

  def scrapeSite(url: String, conf: HashMap[String,String]) {
    println(s"Base URL $url Config: $conf")
    var driver: WebDriver = getDriver()
    var scraper = new Crawl(url, conf, driver)
    scraper run()
    closeDriver(driver)
  }

  def main(args: Array[String]) {
    scrapeInfo foreach {
      case (url, conf) => scrapeSite(url, conf)
    }
  }
}

