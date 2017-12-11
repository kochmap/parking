name := "parking"

version := "0.1"

scalaVersion := "2.12.4"

javaOptions in Test += "-Dlogger.file=conf/logback.test.xml"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.h2database" % "h2" % "1.4.196",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test,
  guice,
  jdbc % Test
)
