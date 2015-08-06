name := """broken-links"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
 "org.apache.spark" %% "spark-core" % "1.2.0",
 "com.typesafe.akka" %% "akka-actor" % "2.2.3",
 "com.typesafe.akka" %% "akka-slf4j" % "2.2.3"
)
