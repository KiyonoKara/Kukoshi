# Kukoshi 

<div>
  <p>
    <a href="https://github.com/KaNguy/Kukoshi/releases"><img src="https://shields.io/github/v/release/KaNguy/Kukoshi" alt="Release Version"/></a>
    <a href="https://github.com/KaNguy/Kukoshi/actions/workflows/scala.yml"><img src="https://github.com/KaNguy/Kukoshi/actions/workflows/scala.yml/badge.svg" alt="Scala Workflow"></a>
    <a href="https://github.com/KaNguy/Kukoshi/pulls"><img src="https://shields.io/github/issues-pr/KaNguy/Kukoshi?color=da301b" alt="PRs" /></a>
    <a><img src="https://shields.io/github/languages/code-size/KaNguy/Kukoshi?color=da301b" /></a>
    <a><img src="https://img.shields.io/github/last-commit/KaNguy/Kukoshi?color=007ace"></a>
    <a href="LICENSE.md"><img src="https://img.shields.io/github/license/KaNguy/Kukoshi?color=007ace" alt="License" /></a>
  </p>
</div>

Kukoshi, is a Scala library made for HTTP / HTTPS requests.

## Overview
Kukoshi wraps built-in Java libraries / modules such as `HttpURLConnection` and classes from `java.net.http._`, primarily `HttpClient`. This Scala library gets assistance for input streams from `scala.io.Source`, a built-in object with convenience methods.

## Features
- GZIP and Deflate support for incoming response bodies. 
- Supports GET, POST, DELETE, PUT, HEAD, OPTIONS, PATCH methods.
- Has a built-in JSON serializer and parser.
  - The JSON serializer primarily takes immutable (default) Scala maps to parse a collection into a valid JSON string.
  - The JSON parser parsers a valid JSON string into an immutable Scala map.  
- Library supports headers in the form of a `Map` or `Seq` (works with collections that can be adapted as `Iterable[(String, String)]`).
- Can append URL parameters to a request if they are provided in the form of a `Map` or `Seq` (`Iterable[(String, String)]`). 
- Additional function, `amend()`, which neatly formats results from `head()` and `options()` functions.
- **Bonus Features:**
  - No external dependencies required.
  - Defaults to GET requests.
  - Extra utilities.

## Installation
#### Main Installation 
The credentials need a token with the `read:packages` permission, the username field can be an empty string.    
Replace `OWNER` with the respective repository owner.
```sbt 
credentials += Credentials(
  realm = "GitHub Package Registry",
  host = "maven.pkg.github.com",
  userName = "",
  passwd = "<READ_PACKAGES_TOKEN>"
)

resolvers += "GitHub Package Registry (<OWNER>/Kukoshi)" at "https://maven.pkg.github.com/<OWNER>/Kukoshi"
libraryDependencies += "org.kukoshi" %% "kukoshi" % "1.0.0"
```

#### Alternate Installation
```sbt
// Replace OWNER with the repository owner's username.
lazy val http = RootProject(uri("git://github.com/<OWNER>/Kukoshi.git"))
lazy val http_root = project in file(".") dependsOn http
```

## Documentation
As a preface, there is one read-only method, which is `GET`. Other methods such as `POST`, `DELETE`, `PUT`, and `PATCH` are writable methods (`DELETE` usually doesn't require a body`).

Note: The example URL will be `https://kukoshi.scala`, it is not a real website nor host.

### Importing
Importing the Request class of the library.
```scala 
import org.kukoshi.Request
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
Adding headers. Headers are accepted in the form of any collection that can be adapted as `Iterable[(String, String)]`, this includes but is not limited to `Map` and `Seq`.
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

### JSON Data
While it is convenient to make HTTP / HTTPS requests with this library, serializing and parsing JSON data is difficult. To solve this issue, Kukoshi has its own JSON serializer and parser.  
- The JSON serializer supports `Map` collections to make a top-level object. `List` collections are also supported but not recommended.
  - Nested data such as `Map`, `List`, `Int`, `Boolean`, and `String` is supported.

Writable request example with JSON serialization.
```scala
// Serializing with the JSON object from the Request class.
val requester: Request = new Request()
val POST: String = requester.request(
  url = "https://kukoshi.scala", 
  method = "POST", 
  data = requester.JSON.encode(Map("key" -> "value"))
)
```

Writable request example with JSON serialization from the `utility` package of Kukoshi.
```scala
// With the library's JSON object
import org.kukoshi.utility.JSON

val requester: Request = new Request()
val POST: String = requester.request(
  url = "https://kukoshi.scala",
  method = "POST",
  data = JSON.encodeJSON(Map("key" -> "value"))
)
```

## Contributing
Read about contributing to this library and its repository [here](CONTRIBUTING.md).

## License
Apache 2.0 License