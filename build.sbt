name := "TODO-AKKA"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.1",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.1",
  "com.typesafe.akka" % "akka-http-experimental_2.11" % "2.0-M2",
  "com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % "1.0",
  "com.typesafe.akka" %% "akka-http-xml-experimental" % "1.0",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.1",
  "io.spray" %%  "spray-json" % "1.3.2",
  "org.scalikejdbc" %% "scalikejdbc"       % "2.3.2",
  "org.postgresql" % "postgresql" % "9.4-1200-jdbc41",
  "ch.qos.logback"  %  "logback-classic"   % "1.1.3"
)