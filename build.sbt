lazy val commons = Seq(
  organization := "it.dtk",
  version := "0.1.0",
  scalaVersion := "2.10.5",
  scalacOptions += "-target:jvm-1.7"
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

      "org.apache.spark" %% "spark-streaming" % "1.4.0" % "provided",
      "org.apache.spark" %% "spark-streaming-kafka" % "1.4.0"
        exclude("commons-beanutils", "commons-beanutils")
        exclude("commons-collections", "commons-collections")
        exclude("com.esotericsoftware.minlog", "minlog"),

      "com.typesafe" % "config" % "1.2.1",
      "org.apache.kafka" % "kafka-clients" % "0.8.2.1",
      "org.elasticsearch" %% "elasticsearch-spark" % "2.1.0"
    )
  ).
  settings(
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
    runMain in Compile <<= Defaults.runMainTask(fullClasspath in Compile, runner in (Compile, run)),
    run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))
  ).
  settings(
    assemblyMergeStrategy in assembly := {
      case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
      case m if m.startsWith("META-INF") => MergeStrategy.discard
      case "about.html"  => MergeStrategy.rename
      case "reference.conf" => MergeStrategy.concat
      case _ => MergeStrategy.first
    }
  ).
  dependsOn(entities)
