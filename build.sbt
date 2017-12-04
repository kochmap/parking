name := "parking"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "3.0.2",
  "org.scalatest" %% "scalatest" % "3.0.4" % Test,
  "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test
)