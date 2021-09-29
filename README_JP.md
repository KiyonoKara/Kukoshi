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
  - JSONシリアライザはScalaのMapを用いています。だからそれはScalaのマップをJSONの有効な文字列を解析します。
  - JSONパーサはJSONの有効な文字列をScalaのマップを解析します。  
- このライブラリーは`Map`や`Seq`対応します（`Iterable[(String, String)]`も）。
- このライブラリーはURLのパラメーターを加えてます（`Map`または`Seq`または`Iterable[(String, String)]`付き）。
- `head()`や`options()`より出力データをフォーマット、`amend()`を事によって。
- **ボーナス機能：**
  - 対外デペンデンシーは必須ではありません。
  - GETリクエストはディフォールト。
  - ライブラリーにはエクストラユーティリティがいます。
  
## インストール 
####  メインインストール 
トークンには許可の`read:packages`が必要です。ユーザー名のフィールドは空白ストリングになることができます。  
`OWNER`はリポジトリ所有者に置き換えます。
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

#### 代替インストール
```sbt
// OWNERはリポジトリ所有者に置き換えます。
lazy val http = RootProject(uri("git://github.com/<OWNER>/Kukoshi.git"))
lazy val http_root = project in file(".") dependsOn http
```

## 文献集
書き込み禁止メソッドは一つしかない、どちらが`GET`。`POST`、`DELETE`、`PUT`、`PATCH`は書き込まれますはなることができます。

サイドノート：サンプルURLは`https://kukoshi.scala.jp`になります。リアルでもリアルホストでもない。

### インポート
ライブラリーの`Request`のクラスをインポート。
```scala
import org.kukoshi.Request
```  

### 表明
`Request`のオブジェクトを作り上げます。
```scala
// Requestのオブジェクト 
val requester: Request = new Request()
```  

任意で、構築子の中URL、METHOD、HEADERSを宣言すできます。