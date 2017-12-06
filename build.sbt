name := "parking"

version := "0.1"

scalaVersion := "2.12.4"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "3.0.2",
  "com.h2database" % "h2" % "1.4.196",
  "org.scalatest" %% "scalatest" % "3.0.4" % Test,
  "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test,
    guice
)