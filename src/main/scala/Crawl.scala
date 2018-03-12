package org.bxroberts.interactivescraper

import collection.mutable.HashMap
import collection.mutable.ListBuffer

import java.lang.Thread
import java.net.URL

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait

import org.jsoup.Jsoup


class Crawl(baseUrl: String, conf: HashMap[String,String], driver: WebDriver) {
  var inputType: String = conf("input-type")
  var charRange = 'a' to 'z'
  var visitedLinks: Set[String] = Set()
  val MAX_DEPTH = 20

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
  def scrapableForms(): ListBuffer[WebElement] = {
    var forms: ListBuffer[WebElement] = ListBuffer()
    var fElements = driver.findElements(By.tagName("form"))
    println(s"fElements $fElements")
    fElements.forEach(form => {
      if (form.isEnabled && form.isDisplayed) {
        println(s"Evaluating form $form")
        val inputs = form.findElements(By.tagName("input"))
        val firstIName = inputs.get(0).getAttribute("name")

        println(s"Inputs $inputs")
        println(s"firstIName $firstIName")

        if (inputs.size == 1 && inputs.get(0).isDisplayed) {
          println(s"Adding form $form")
          forms.append(form)
        } else {
          inputs.forEach(inp => {
            val iName = inp.getAttribute("name")
            println(s"Checking input $iName")

            val vis = inp.isDisplayed && inp.isEnabled
            if (vis && iName.contains("name") && !forms.contains(form)) {
              println(s"Adding form $form")
              forms.append(form)
            }
          })
        }
      }
    })
    return forms
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

  def goodLink(href: String, link: WebElement): Boolean = {
    val rawHref = link.getAttribute("href")
    //println(s"checking for good link $href raw $rawHref")
    // only use visible links
    if (!link.isEnabled || !link.isDisplayed)
      return false

    // ignore strange protocols
    if (href.contains("mailto:") || href.startsWith("tel:"))
      return false

    // TODO: make this a setting. selenium doesn't handle tabs
    // at all, so this has potential to fuck things up. one
    // recommendation to fix is to mutate the target before clicking
    // but you can't do that in scala/java AFAIK
    val tar = link.getAttribute("target")
    if (tar == "_new" || tar == "_blank")
      return false

    // make sure we're not leaving the host
    val proto = new URL(driver.getCurrentUrl()).getProtocol
    val baseHost = new URL(baseUrl).getHost
    if (rawHref == null || rawHref.isEmpty) {
      println("skipping empty href")
      return false
    }
    var thisHost = new URL(rawHref).getHost
    val sameHost = thisHost == baseHost
    if (!sameHost)
      println(s"Not same host this: $thisHost base: $baseHost")

    return sameHost
  }

  /**
   * Extract all the by links from our page so we can look them up at a later
   * time given that the driver instance will not be the same one after
   * recursion.
   *
   * NOTE: selenium is interpreting href attributes in a
   * manner which doesn't allow us to find them. I.e., if anchor
   * href is "//bxroberts.org" the result of getAttribute("href") will
   * return https://bxroberts.org/
   */
  def extractPageLinks(): List[By] = {
    var selectors: List[By] = List()
    val links = driver.findElements(By.tagName("a"))
    links.forEach( link => {
      val href = getHref(link)
      if (goodLink(href, link)) {
        val selector = By.cssSelector(s"""a[href="${href}"]""")
        selectors = selectors ++ List(selector)
      }
    })
    return selectors
  }

  def clickLink(link: WebElement): Boolean = {
    try {
      link.click()
      Thread.sleep(2000)
      return true
    } catch {
      case e: Throwable => println(s"Error clicking $link: $e")
    }
    return false
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

  def crawlUntilCondition(stopCond: () => Boolean, depth: Int = 0) {
   var title = driver.getTitle
    println(s"findInteractiveFormPage Title: $title Depth: $depth")
    addLink(driver.getCurrentUrl)

    // check if url is a searchable interactive form
    // possibly use a ML model to decide in the future
    if (stopCond()) {
      return
    }

    //driver.navigate.refresh()
    val byLinks: List[By] = extractPageLinks()

    for (by <- byLinks) {
      var link = findLink(by)

      if (link != null) {
        var url = getHref(link)
        println(s"Extracted url $url")

        if (!visitedLinks.contains(url)) {
          addLink(url)

         if(depth < MAX_DEPTH && clickLink(link)) {
           println(s"***** Clicked link $url")
           crawlUntilCondition(stopCond, depth + 1)
           println("Going back...")
           driver.navigate.back()
          }
        }
      }
    }

    //driver.navigate.back()
  }

  def goodFormsFound(): Boolean = {
    var forms = scrapableForms()
    return forms.size > 0
  }

  def run() {
    println(s"Loading page $baseUrl")
    driver.get(baseUrl)

    // run findInteractiveFormPage, it will navigate to the page
    // with the proper form
    crawlUntilCondition(goodFormsFound)

    // var forms = scrapableForms()
    // for (form <- forms) {
    //   println(s"Form $form")
    //   for (i <- inputs) {
    //     val inp = form.findElement(By.tagName("input"))
    //     println(s"Input $inp")
    //     inp.sendKeys(i)
    //     inp.submit()
    //     Thread.sleep(1500)
    //     driver.navigate.back()
    //   }
    // }


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

    // var tag: By = By.tagName("body")
    // var element: WebElement = driver.findElement(tag)
    // var data: String = element.getAttribute("innerHTML")
    // var title: String = driver.getTitle

    // var forms = scrapableForms()
    // print(s"Scrape forms $forms")

  }
}
