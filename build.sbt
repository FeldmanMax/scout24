name := "Scout24"
 
version := "1.0" 
      
lazy val `scout24` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
scalaVersion := "2.11.11"

libraryDependencies ++= Seq( jdbc , cache , ws, filters,
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.jcraft" % "jsch" % "0.1.51",
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "org.scalatestplus" %% "play" % "1.4.0-M3" % "test",
  "com.typesafe.play" %% "play-json" % "2.6.7",
  "com.google.inject" % "guice" % "4.1.0",
  "io.circe" %% "circe-core" % "0.8.0",
  "io.circe" %% "circe-generic"% "0.8.0",
  "io.circe" %% "circe-parser" % "0.8.0",
  "org.specs2" %% "specs2-core" % "4.0.1",
  "junit" % "junit" % "4.11" % Test,
  "com.h2database" % "h2" % "1.3.148" % Test,
  "com.typesafe.play" %% "anorm" % "2.5.0")

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

