val maintainer: String = "KiyonoKara"
val packageName: String = "Kukoshi"

ThisBuild / name := packageName
ThisBuild / description := "A Scala HTTP client to make HTTP requests and return responses"
ThisBuild / organization := "org.kiyo"
ThisBuild / startYear := Some(2021)

ThisBuild / version := "1.0.0"
ThisBuild / scalaVersion := "3.2.2"
ThisBuild / crossScalaVersions := Seq("2.13.10", scalaVersion.value)
ThisBuild / versionScheme := Some("semver-spec")

ThisBuild / homepage := Some(url(f"https://github.com/$maintainer/$packageName"))
ThisBuild / licenses := Seq("Apache 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / publishMavenStyle := true
ThisBuild / pomIncludeRepository := { _ => false }

ThisBuild / publishTo := Some(f"GitHub $maintainer Apache Maven Packages" at f"https://maven.pkg.github.com/$maintainer/$packageName")
ThisBuild / credentials += Credentials(
  realm = "GitHub Package Registry",
  host = "maven.pkg.github.com",
  maintainer,
  sys.env.getOrElse("GITHUB_TOKEN", "")
)

Global / lintUnusedKeysOnLoad := false