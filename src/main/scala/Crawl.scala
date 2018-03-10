package org.bxroberts.interactivescraper

import collection.mutable.HashMap
import java.lang.Thread
import java.util.List
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait

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

  def unclickedLinkIndex(links: List[WebElement]): Int = {
    for( i <- 0 to links.size) {
      var link = links.get(i)
      var url = link.getAttribute("href")
      if (!visitedLinks.contains(url)) {
        return i;
      }
    }
    return -1;
  }

  def findInteractiveFormPage() {
    println("findInteractiveFormPage")
    // check if url is a searchable interactive form
    // possibly use a ML model to decide in the future
    var links = driver.findElements(By.tagName("a"))
    println(s"Got links $links")

    var ix: Int = unclickedLinkIndex(links)
    println(s"Unclicked $ix")
    var link = links.get(ix)

    var url = link.getAttribute("href")
    var linkText = link.getText()

    println(s"Clicking Url $url Text $linkText")
    try {
      link.click()
    } catch {
      case e => {
        println(s"Error $e")
        addLink(url)
        return
      }
    }

    println("Sleeping")
    Thread.sleep(500)

    addLink(url)

    println("Recursing into page")
    findInteractiveFormPage()

    println("Going back")
    driver.navigate.back()
    Thread.sleep(500)
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
