import sbt.Keys._
import sbt._
import sbtassembly.AssemblyKeys._
import sbtassembly.{ MergeStrategy, PathList }

object BuildHsgl extends Build {
  lazy val id = "hsgl"
  // dvergar
  lazy val projVersion = "0.0.1"
  lazy val projOrganization = "com.argcv"
  lazy val projScalaVersion = "2.11.8"

  lazy val commonSettings = Seq(
    name := id,
    version := projVersion,
    organization := projOrganization,
    scalaVersion := projScalaVersion,
    licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
    homepage := Some(url("https://github.com/yuikns/hsgl"))
  )

  lazy val publishSettings = Seq(
    isSnapshot := false,
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false }
  )

  lazy val dependenciesSettings = Seq(
    resolvers ++= Seq(
      "Atlassian Releases" at "https://maven.atlassian.com/public/",
      "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
      Resolver.sonatypeRepo("snapshots"),
      Classpaths.typesafeReleases,
      Classpaths.typesafeSnapshots
    ),
    libraryDependencies ++= Seq(
      //"com.nativelibs4java" %% "scalacl" % "0.3-SNAPSHOT",
      "com.nativelibs4java" % "javacl-core" % "1.0.0-RC4",
      "commons-pool" % "commons-pool" % "1.6", // pool for SockPool
      "net.liftweb" % "lift-webkit_2.11" % "3.0-M6", // a light weight framework for web
      //"com.google.guava" % "guava" % "18.0", // string process etc. (snake case for example)
      "org.scalatest" % "scalatest_2.11" % "2.2.5" % "test"
    ),
    dependencyOverrides ++= Set(
      "org.scala-lang" % "scala-reflect" % "2.11.7",
      "org.scala-lang" % "scala-compiler" % "2.11.7",
      "org.scala-lang" % "scala-library" % "2.11.7",
      "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.4",
      "org.scalatest" % "scalatest_2.11" % "2.2.5" % "test"
    )
  )

  lazy val assemblySettings = Seq(
    assemblyJarName in assembly := s"$id-$projVersion-$projScalaVersion.jar",
    assemblyMergeStrategy in assembly := {
      case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
      case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
      case PathList("org", "apache", xs @ _*) => MergeStrategy.last
      case PathList("com", "google", xs @ _*) => MergeStrategy.last
      case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
      case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
      case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
      case "about.html" => MergeStrategy.rename
      case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
      case "META-INF/mailcap" => MergeStrategy.last
      case "META-INF/mimetypes.default" => MergeStrategy.last
      case "META-INF/MANIFEST.MF" => MergeStrategy.discard
      case "plugin.properties" => MergeStrategy.last
      case "log4j.properties" => MergeStrategy.last
      case x =>
        // p1
        //val oldStrategy = (assemblyMergeStrategy in assembly).value
        //oldStrategy(x)
        // p2
        MergeStrategy.last
    }
  )

  lazy val root = Project(id = id, base = file("."))
    .settings(commonSettings: _*)
    .settings(publishSettings: _*)
    .settings(dependenciesSettings: _*)
    .settings(assemblySettings: _*)
    .dependsOn(valhalla)
    .aggregate(valhalla)

  lazy val valhalla = ProjectRef(file("modules/valhalla"), "valhalla")

}

