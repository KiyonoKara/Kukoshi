# Kukoshi 

<div>
  <p>
    <a href="https://github.com/KiyonoKara/Kukoshi/releases"><img src="https://shields.io/github/v/release/KiyonoKara/Kukoshi" alt="Release Version"/></a>
    <a href="https://github.com/KiyonoKara/Kukoshi/actions/workflows/scala.yml"><img src="https://github.com/KiyonoKara/Kukoshi/actions/workflows/scala.yml/badge.svg" alt="Scala Workflow"></a>
    <a href="https://github.com/KiyonoKara/Kukoshi/pulls"><img src="https://shields.io/github/issues-pr/KiyonoKara/Kukoshi?color=da301b" alt="PRs" /></a>
    <a><img src="https://shields.io/github/languages/code-size/KiyonoKara/Kukoshi?color=da301b" alt="Code Size" /></a>
    <a><img src="https://img.shields.io/github/last-commit/KiyonoKara/Kukoshi?color=007ace" alt="Last Commit" /></a>
    <a href="LICENSE.md"><img src="https://img.shields.io/github/license/KiyonoKara/Kukoshi?color=007ace" alt="License" /></a>
  </p>
</div>

Kukoshi, is a Scala library made for making HTTP / HTTPS requests.

## Overview
Kukoshi makes HTTP-based interactions simpler. This client doesn't require a boilerplate set-up or a blend of imports.

## Features
- GZIP and Deflate support for compressed responses and data. 
- Supports GET, POST, DELETE, PUT, HEAD, OPTIONS, PATCH methods.
- Library supports headers in the form of a `Map` or `Seq` (works with collections that can be adapted as `Iterable[(String, String)]`).
- Can append URL parameters to a request if they are provided in the form of a `Map` or `Seq` (`Iterable[(String, String)]`).
- **Bonus Features:**
  - No external dependencies required.
  - Defaults to GET requests.
  - `head` and `options` for header-based requests.


## Installation
### Main (Preferred) Installation 
The credentials need a token with the `read:packages` permission, the username field can be an empty string.
```sbt
credentials += Credentials(
  realm = "GitHub Package Registry",
  host = "maven.pkg.github.com",
  userName = "",
  passwd = "<READ_PACKAGES_TOKEN>"
)

resolvers += "GitHub Package Registry (KiyonoKara/Kukoshi)" at "https://maven.pkg.github.com/KiyonoKara/Kukoshi"
libraryDependencies += "org.kiyo% "kiyo" % "2.0.0"
```

#### Alternate Installation
```sbt
lazy val http = RootProject(uri("git://github.com/KiyonoKara/Kukoshi.git"))
lazy val http_root = project in file(".") dependsOn http
```

## Documentation
As a preface, there is one read-only method, which is `GET`. Other methods such as `POST`, `DELETE`, `PUT`, and `PATCH` are writable methods (`DELETE` usually doesn't require a body`).

Note: The example URL will be `https://kukoshi.scala`, it is not a real website nor host.

### Importing
Importing the Request class of the library.

```scala 
import org.kiyo.Request
```

### Declaration
Primarily, the Kukoshi library is utilized through its `Request` class. There are several ways the class can be used.   
Creating a `Request` object.
```scala
// A regular Request object.
val requester: Request = new Request()
```

Optionally declaring the URL, method, and headers in the parameters. If you declare these in the constructor, you can carry out most requests using `request()` without input parameters.   

### Creating a Request
Read-only request examples.
```scala
// Defaults to a GET request if no method is provided.
// Example works if there is nothing provided in the request function, but in the constructor.
val requesterA: Request = new Request(url = "https://kukoshi.scala")
val getA: String = requester.request()

// Works the other way around.
val requesterB: Request = new Request()
val getB: String = requester.request(url = "https://kukoshi.scala")
```

### Headers
Headers are accepted in the form of any collection that can be adapted as `Iterable[(String, String)]`, this includes but is not limited to `Map` and `Seq`.
```scala
// Examples for Map and Seq
val requesterWithMapHeaders: Request = new Request(
  url = "https://kukoshi.scala", 
  headers = Map("Content-Type" -> "User-Agent" -> "*", "Accept" -> "*/*")
)

val requesterWithSeqHeaders: Request = new Request(
  url = "https://kukoshi.scala",
  headers = Seq("Content-Type" -> "User-Agent" -> "*", "Accept" -> "*/*")
)
```

### URL Parameters
When appending URL parameters to the request, URL parameters can only be added in `request()` calls. URL parameters are also accepted in the form of any collection, adaptable as `Iterable[(String, String)]`.
```scala
// Requests to "https://kukoshi.scala?parameter=value"
val requesterA: Request = new Request()
val requestA: String = requester.request(url = "https://kukoshi.scala", parameters = Map("parameter" -> "value"))

// Requests to "https://kukoshi.scala?parameter1=value1&parameter2=value2"
val requesterB: Request = new Request()
val requestB: String = requester.request(url = "https://kukoshi.scala", parameters = Map("parameter1" -> "value1", "parameter2" -> "value2"))
```

### Writable Requests
Writable requests generally require the `request()` function to be used since data can only be provided through it.
```scala
// POST request example, similar requests like this can be made for DELETE, PUT, and PATCH. 
// Be sure to include headers if the APIs you are requesting to require them.
val requester: Request = new Request()
val POST: String = requester.request(url = "https://kukoshi.scala", method = "POST", data = "{\"key\": \"value\"}")
```

## Contributing
Read about contributing to this library and its repository [here](CONTRIBUTING.md).

## License
[Apache 2.0 License](LICENSE.md)
