
name := "volskaya-kafka-server"

version := "0.1"

scalaVersion := "2.12.6"

val kafkaVersion = "0.10.1.1"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % kafkaVersion,
  "org.slf4j" % "slf4j-log4j12" % "1.7.5" % Test
)

enablePlugins(JavaAppPackaging)