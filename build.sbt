name := "volskaya-kafka-server"
version := "0.1"
scalaVersion := "2.12.6"

resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")
scalacOptions ++= Seq("-Ypartial-unification")
val zioVersion = "1.0.0-RC15"
val zioKafkaVersion = "0.3.2"
val pureConfigVersion = "0.12.1"

val Specs2Version = "4.7.0"
val PureScriptVersion = "0.12.1"

val kafkaVersion = "0.10.1.1"

val pureConfigDependencies = Seq(
  "com.github.pureconfig" %% "pureconfig" % pureConfigVersion
)
val zioDependencies = Seq(
  "dev.zio" %% "zio-streams" % zioVersion,
  "dev.zio" %% "zio-kafka" % zioKafkaVersion,
  "dev.zio" %% "zio-interop-cats" % "2.0.0.0-RC6"
)

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % kafkaVersion,
  "org.slf4j" % "slf4j-log4j12" % "1.7.5" % Test
) ++ pureConfigDependencies ++ zioDependencies

enablePlugins(JavaAppPackaging)
addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("chk", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")
