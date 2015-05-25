name := "git-tweeter"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.12" % "test",
  "org.mockito" % "mockito-core" % "1.10.19",
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "org.twitter4j" % "twitter4j-stream" % "4.0.3",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.1"
)