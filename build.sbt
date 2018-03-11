import Dependencies._

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "org.bxroberts",
      scalaVersion := "2.12.3",
      version      := "0.0.2"
    )),
    name := "InteractiveCrawler",
    libraryDependencies ++= Seq(
      "org.apache.nutch" % "nutch" % "1.13",
      "org.seleniumhq.selenium" % "selenium-java" % "3.6.0",
      "org.scala-lang.modules" % "scala-xml_2.12" % "1.0.6",
      "org.jsoup" % "jsoup" % "1.8.3",
      "org.json4s" %% "json4s-native" % "3.6.0-M2",
    ),
    scalacOptions += "-feature"
  )
