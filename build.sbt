name := "rcmndtn"

version := "1.0"

scalaVersion := "2.12.3"

mainClass := Some("Main")

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-jackson" % "3.5.1",
  "org.scalactic" %% "scalactic" % "3.0.3",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test")