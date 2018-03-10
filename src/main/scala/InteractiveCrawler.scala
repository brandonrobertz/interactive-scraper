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

object InteractiveCrawler {
  // Interactive Searchable
  var scrapeInfo: HashMap[String,HashMap[String,String]] = HashMap(
    "https://bxroberts.org/" -> HashMap(
      "input-type" -> "character-2",
      "wildcard-type" -> "%"))
/*
    "http://registrar.utexas.edu/students/degrees/verify" -> HashMap(
      "input-type" -> "character-1"),
    "http://reg.tmb.state.tx.us/OnLineVerif/Phys_NoticeVerif.asp" -> HashMap(
      "input-type" -> "character-1",
      "wildcard-type" -> "*"),

    "http://traviscountyclerk.org/apex/f?p=137:10:0::NO" -> HashMap(
      "input-type" -> "character-1",
      "wildcard-type" -> "%"),

    "http://www.texasbar.com" -> HashMap(
      "input-type" -> "character-1"),

    "http://www.traviscad.org/tcad_search.php?mode=name&kind=real" -> HashMap(
      "input-type" -> "character-1"),

    "http://www.traviscountyclerk.org/eclerk/Category.do?code=R.17" -> HashMap(
      "input-type" -> "character-1"),

    "http://www.traviscountytax.org" -> HashMap(
      "input-type" -> "character-2"),

    "http://www.traviscountyclerk.org" -> HashMap(
      "input-type" -> "character-1",
      "wildcard-type" -> "*"))
*/

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

