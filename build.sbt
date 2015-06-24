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
    name := "Kafka Consumer",
    libraryDependencies ++= Seq (
      "org.apache.spark" %% "spark-core" % "1.4.0" % "provided",

      "org.apache.spark" %% "spark-streaming" % "1.4.0",
      "org.apache.spark" %% "spark-streaming-kafka" % "1.4.0",

      "org.apache.kafka" % "kafka-clients" % "0.8.2.1",

      "io.spray" %%  "spray-json" % "1.3.2"
    )
  ).
  settings(
    runMain in Compile <<= Defaults.runMainTask(fullClasspath in Compile, runner in (Compile, run)),
    run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))
  )
