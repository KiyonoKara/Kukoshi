# Kukoshi 

<div>
  <p>
    <a href="https://github.com/KaNguy/Kukoshi/pulls"><img src="https://shields.io/github/issues-pr/KaNguy/Kukoshi?color=da301b" alt="PRs" /></a>
    <a><img src="https://shields.io/github/languages/code-size/KaNguy/Kukoshi?color=da301b" /></a>
    <a><img src="https://img.shields.io/github/last-commit/KaNguy/Kukoshi?color=007ace"></a>
    <a href="LICENSE.md"><img src="https://img.shields.io/github/license/KaNguy/Kukoshi?color=007ace" alt="License" /></a>
  </p>
</div>

Kukoshi, is a Scala library made for HTTP / HTTPS requests.   
「クコシ」は、HTTPと HTTPSリクエストに作られて「Scala」ライブラリーです。

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

## Usage
(Incomplete)

## Author Notes
(Documentation is incomplete).

## Contributing
Read about contributing to this library and its repository [here](CONTRIBUTING.md).

## License
Apache 2.0 License