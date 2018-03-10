package org.bxroberts.interactivescraper

import collection.mutable.HashMap

import java.lang.Thread
import java.net.URL

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait

import scala.xml.XML
import org.jsoup.Jsoup


class Crawl(baseUrl: String, conf: HashMap[String,String], driver: WebDriver) {
  var inputType: String = conf("input-type")
  var charRange = 'a' to 'z'
  var visitedLinks: Set[String] = Set()

  /**
   * Extract chars from a character-based input type.
   * Default is 1 if no match found.
   */
  def numChars(): Int = {
    val pattern = "character-([0-9]+)".r
    return inputType match {
      case pattern(c) => c.toInt
      case _ => 1
    }
  }

  /**
   * Returns an iterator of inputs to search based
   * on the numChars specified in input-type and
   * whether or not to use a wildcard (wildcard-type)
   */
  def inputs(): Iterator[String] = {
    return ('a' to 'z').combinations(numChars)
      .map(_ mkString(""))
      .map((x) => {
        if (conf isDefinedAt "wildcard-type")
          (x + conf("wildcard-type")) else x
      })
  }

  /**
   * If there are multiple forms on a page, determine which
   * one we want to use. Return the ID of the form to use
   */
  def desiredFormInPage(): String = {
    try {
      var e: WebElement = driver.findElement(By.name("form"))
      println("WebElement", e);
      return e.getAttribute("id")
    } catch {
      case e: NoSuchElementException => return ""
    }
  }

  def getPage(f: () => Unit) {
    Thread.sleep(3000)
    println("running")
    f()
    /*
    val myDynamicElement = new WebDriverWait(driver, 10).until(
      new ExpectedCondition[WebElement] {
        override def apply(d: WebDriver) = d.findElement(By.tagName("body"))
      }
    )
    */
  }

  def addLink(url: String) {
    println(s"Adding link to visited $url")
    visitedLinks += url
  }

  def getHref(link: WebElement): String = {
    val source = link.getAttribute("outerHTML")
    return Jsoup
      .parse(source)
      .getElementsByTag("a")
      .attr("href")
  }

  def goodLink(href: String): Boolean = {
    val thisHost = new URL(href).getHost
    val baseHost = new URL(baseUrl).gethost
    return !href.contains("mailto:") && thisHost == baseHost
  }

  /**
   * Extract all the by links from our page so
   * we can look them up at a later time given
   * that the driver instance will not be the same
   * one after recursion.
   *
   * TODO: take raw page HTML and extract real
   * links, selenium is interpreting them in a
   * manner which doesn't allow us to re-search
   * and find them. I.e., if anchor href is
   * "//bxroberts.org" the result of getAttribute("href")
   * will return https://bxroberts.org/
   */
  def extractPageLinks(): List[By] = {
    var selectors: List[By] = List()
    val links = driver.findElements(By.tagName("a"))
    links.forEach( link => {
      println(s"Link $link")
      val href = getHref(link)
      if (goodLink(href)) {
        val selector = By.cssSelector(s"""a[href="${href}"]""")
        selectors = selectors ++ List(selector)
      }
    })
    return selectors
  }

  def clickLink(link: WebElement) {
    try {
      link.click()
    } catch {
      case e: Throwable => println(s"Error clicking $link: $e")
    }
  }

  def findLink(by: By): WebElement = {
    var link: WebElement = null
    try {
      link = driver.findElement(by)
    } catch {
      case e: Throwable => println(s"Error finding link by $by: $e")
    }
    return link
  }

  def findInteractiveFormPage() {
   var title = driver.getTitle
    println(s"findInteractiveFormPage Title: $title")
    addLink(driver.getCurrentUrl)

    // check if url is a searchable interactive form
    // possibly use a ML model to decide in the future

    //driver.navigate.refresh()
    val byLinks: List[By] = extractPageLinks()
    println(s"List[By] = $byLinks")

    for (by <- byLinks) {
      var link = findLink(by)

      if (link != null) {
        var url = getHref(link)
        println(s"Extracted url $url")
        var pUrl = link.getAttribute("href")
        print(s"Extracted url $url parsed $pUrl")

        if (!visitedLinks.contains(url)) {
          addLink(url)

          println(s"Clicking link $url")
          clickLink(link)
          findInteractiveFormPage()

          println(s"Link $link")
          driver.navigate.back()
        }
      }
    }

    println("Going back...")
    //driver.navigate.back()
  }

  def run() {
    println(s"Loading page $baseUrl")
    driver.get(baseUrl)

    // run findInteractiveFormPage, it will navigate to the page
    // with the proper form
    findInteractiveFormPage()
    println("Found searchable form")

    // if there are multiple forms on the page, run the desiredFormInPage
    // which will determine which form to interact with

    // identify the input(s) within form to generate inputs for

    // for each input, enter into form input and sumbit
    //   a. save the page and follow any links
    //      NOTE: we may want to only follow "new" links, so this would
    //      prevent us from clicking back on headers, boilerplate, etc
    //   b. if there's a "next" page, follow it to the end before
    //      continuing to the next input. NOTE: this may not be
    //      necessary if we're just following (clicking) all links
    //      inside the page. but for now it's guaranteed to be faster
    //      and less noisy

    var tag: By = By.tagName("body")
    var element: WebElement = driver.findElement(tag)
    var data: String = element.getAttribute("innerHTML")
    var title: String = driver.getTitle

    for (i <- inputs)
      println(s"Input $i")

    var form: String = desiredFormInPage()
    println("form", form)

    println(s"Title $title")
  }
}
