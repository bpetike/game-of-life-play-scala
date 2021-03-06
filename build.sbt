import sbt.Keys._

lazy val GatlingTest = config("gatling") extend Test

scalaVersion in ThisBuild := "2.12.7"

libraryDependencies += guice
libraryDependencies ++= Seq("org.joda" % "joda-convert" % "2.1.2",
  "net.logstash.logback" % "logstash-logback-encoder" % "5.2",
  "com.netaporter" %% "scala-uri" % "0.4.16",
  "net.codingwell" %% "scala-guice" % "4.2.1",
  "org.apache.commons" % "commons-lang3" % "3.5",
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test,
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.0.1.1" % Test,
  "io.gatling" % "gatling-test-framework" % "3.0.1.1" % Test)

// The Play project itself
lazy val root = (project in file("."))
  .enablePlugins(Common, PlayScala, GatlingPlugin)
  .configs(GatlingTest)
  .settings(inConfig(GatlingTest)(Defaults.testSettings): _*)
  .settings(
    name := """play-scala-rest-api-example""",
    scalaSource in GatlingTest := baseDirectory.value / "/gatling/simulation"
  )

// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/index.html
lazy val docs = (project in file("docs")).enablePlugins(ParadoxPlugin).
  settings(
    paradoxProperties += ("download_url" -> "https://example.lightbend.com/v1/download/play-rest-api")
  )
