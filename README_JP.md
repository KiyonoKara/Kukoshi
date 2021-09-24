# クコシ

<div>
  <p>
    <a href="https://github.com/KaNguy/Kukoshi/releases"><img src="https://shields.io/github/v/release/KaNguy/Kukoshi" alt="製品版"/></a>
    <a href="https://github.com/KaNguy/Kukoshi/actions/workflows/scala.yml"><img src="https://github.com/KaNguy/Kukoshi/actions/workflows/scala.yml/badge.svg" alt="スケーラのワークフロー"></a>
    <a href="https://github.com/KaNguy/Kukoshi/pulls"><img src="https://shields.io/github/issues-pr/KaNguy/Kukoshi?color=da301b" alt="PRs" /></a>
    <a><img src="https://shields.io/github/languages/code-size/KaNguy/Kukoshi?color=da301b" /></a>
    <a><img src="https://img.shields.io/github/last-commit/KaNguy/Kukoshi?color=007ace"></a>
    <a href="LICENSE.md"><img src="https://img.shields.io/github/license/KaNguy/Kukoshi?color=007ace" alt="ライセンス" /></a>
  </p>
</div>

「クコシ」は、HTTPとHTTPSリクエストへ作りました、「Scala」ライブラリーです。

## 概要
Kukoshiは、`HttpURLConnection`や`java.net.http._`のクラス、主に`HttpClient`などの、組み込みのJavaライブラリ/モジュールをラップしています。このScalaライブラリは、`scala.io.Source`という便利なメソッドを持つ組み込みオブジェクトから、入力ストリームの支援を得ています。

## 機能 
- レスポンスのボディーについて、GZIPやDeflateに対応しています。
- GET、POST、DELETE、PUT、HEAD、OPTIONS、PATCHに対応しています。
- ライブラリーにはJSONパーサやシリアライザが内蔵されています。 