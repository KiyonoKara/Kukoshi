val maintainer: String = "KiyonoKara"
val packageName: String = "Kukoshi"

name := packageName

version := "2.0.0"

scalaVersion := "3.2.2"

organization := "org.kukoshi"

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
  System.getenv("GITHUB_TOKEN")
)
