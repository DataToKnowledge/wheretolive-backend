lazy val commons = Seq(
  organization := "it.dtk",
  version := "0.1.0",
  scalaVersion := "2.11.6"
)

lazy val root = (project in file(".")).aggregate(konsumer).
  settings(commons: _*).
  settings(
    name := "WhereToLive Backend"
  )

lazy val konsumer = (project in file("./konsumer")).
  settings(commons: _*).
  settings(
    name := "Kafka Consumer"
  )
