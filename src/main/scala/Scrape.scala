package org.bxroberts.interactivescraper

import collection.mutable.HashMap

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

class Scraper(baseUrl: String, conf: HashMap[String,String], driver: WebDriver) {
  var inputType: String = conf("input-type")
  var charRange = 'a' to 'z'

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

  def getPage(url: String) {
    // get page with error handing and retries
  }

  /**
   * If there are multiple forms on a page, determine which
   * one we want to use. Return the ID of the form to use
   */
  def desiredFormInPage(data: String): String {
    return true
  }

  def findInteractiveFormPage(url: String) {
    // check if url is a searchable interactive form
    // possibly use a ML model to decide in the future
  }

  def run() {
    println(s"Loading page $baseUrl")
    driver.get(baseUrl)

    // run findInteractiveFormPage, it will return the page
    // with the proper form

    // if there are multiple forms on the page, run the desiredFormInPage
    // which will determine which form to interact with

    // identify the input to generate inputs for

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

    println(s"Title $title")
  }
}
