lazy val commons = Seq(
  organization := "it.dtk",
  version := "0.1.0",
  scalaVersion := "2.11.6"
)

lazy val testLibrary = Seq (
  "org.scalactic"  %% "scalactic"  % "2.2.5",
  "org.scalatest"  %% "scalatest"  % "2.2.5" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.3" % "test"
)

lazy val commonDependencies = testLibrary ++ Seq(
"io.spray" %%  "spray-json" % "1.3.2"
)

/***********************/
/* projects definition */
/***********************/

lazy val root = (project in file(".")).aggregate(konsumer).
  settings(commons: _*).
  settings(
    name := "WhereToLive Backend"
  )

lazy val entities = (project in file("./entities")).
  settings(commons: _*).
  settings(
    name := "Entities",
    libraryDependencies ++= commonDependencies
  )

lazy val konsumer = (project in file("./konsumer")).
  settings(commons: _*).
  settings(
    name := "Kafka Consumer",
    libraryDependencies ++= commonDependencies ++ Seq (
      "org.apache.spark" %% "spark-core" % "1.4.0" % "provided",

      "org.apache.spark" %% "spark-streaming" % "1.4.0",
      "org.apache.spark" %% "spark-streaming-kafka" % "1.4.0",

      "org.apache.kafka" % "kafka-clients" % "0.8.2.1"
    )
  ).
  settings(
    runMain in Compile <<= Defaults.runMainTask(fullClasspath in Compile, runner in (Compile, run)),
    run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))
  ).
  dependsOn(entities)
