name := "Kukoshi"

version := "1.0.0"

scalaVersion := "2.13.6"

organization := "org.kukoshi"

versionScheme := Some("semver-spec")

homepage := Some(url("https://github.com/KiyonoKara/Kukoshi"))
licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
publishMavenStyle := true
pomIncludeRepository := { _ => false }

publishTo := Some("GitHub KiyonoKara Apache Maven Packages" at "https://maven.pkg.github.com/KiyonoKara/Kukoshi")
credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  "KiyonoKara",
  System.getenv("GITHUB_TOKEN")
)
