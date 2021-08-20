name := "Kukoshi"

version := "0.1"

scalaVersion := "2.13.6"

organization := "org.kukoshi"

homepage := Some(url("https://github.com/KaNguy/Kukoshi"))
licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
publishMavenStyle := true
pomIncludeRepository := { _ => false }

publishTo := Some("GitHub KaNguy Apache Maven Packages" at "https://maven.pkg.github.com/KaNguy/Kukoshi")
credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  "KaNguy",
  System.getenv("GITHUB_TOKEN")
)