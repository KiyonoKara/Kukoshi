val maintainer: String = "KiyonoKara"
val packageName: String = "Kukoshi"

name := packageName
description := "A library for making HTTP requests."
organization := "org.kiyo"
startYear := Some(2021)

version := "2.0.0"
scalaVersion := "3.2.2"
crossScalaVersions := Seq("2.13.10", scalaVersion.value)
versionScheme := Some("semver-spec")

homepage := Some(url(f"https://github.com/$maintainer/$packageName"))
licenses := Seq("Apache 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
publishMavenStyle := true
pomIncludeRepository := { _ => false }

publishTo := Some(f"GitHub $maintainer Apache Maven Packages" at f"https://maven.pkg.github.com/$maintainer/$packageName")
credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  maintainer,
  sys.env.getOrElse("GITHUB_TOKEN", "")
)