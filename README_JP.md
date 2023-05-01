# クコシ

<div>
  <p>
    <a href="https://github.com/KiyonoKara/Kukoshi/releases"><img src="https://shields.io/github/v/release/KiyonoKara/Kukoshi" alt="製品版"/></a>
    <a href="https://github.com/KiyonoKara/Kukoshi/actions/workflows/scala.yml"><img src="https://github.com/KiyonoKara/Kukoshi/actions/workflows/scala.yml/badge.svg" alt="スケーラのワークフロー"></a>
    <a href="https://github.com/KiyonoKara/Kukoshi/pulls"><img src="https://shields.io/github/issues-pr/KiyonoKara/Kukoshi?color=da301b" alt="PRs" /></a>
    <a><img src="https://shields.io/github/languages/code-size/KiyonoKara/Kukoshi?color=da301b"  alt="サイズ"/></a>
    <a><img src="https://img.shields.io/github/last-commit/KiyonoKara/Kukoshi?color=007ace" alt="最新コミット"></a>
    <a href="LICENSE.md"><img src="https://img.shields.io/github/license/KiyonoKara/Kukoshi?color=007ace" alt="ライセンス" /></a>
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
トークンには許可の`read:packages`が必要です。ユーザー名のフィールドは空白ストリングのなるのができます。  
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
書き込み禁止メソッドは一つしかない、どちらが`GET`。`POST`、`DELETE`、`PUT`、`PATCH`は書き込まれますはなるのができます。

メモ：サンプルURLは`https://kukoshi.scala.jp`になります。リアルでもリアルホストでもない。

### インポート
ライブラリーの`Request`のクラスをインポート。
```scala
import org.kukoshi.Request
```  

### 表明
主に、`クコシ`は`Request`のクラスを介して使用されます。`Request`のオブジェクトを作り上げます。
```scala
// Requestのオブジェクト 
val requester: Request = new Request()
```  

任意で、構築子の中URL、METHOD、HEADERSを宣言すできます。

### リクエストを作り上げられる 
読み取り専用リクエストのサンプル
```scala
// METHODがない場合は、GETを使います。
// コンストラクタのURLを提供しました。
val requesterA: Request = new Request(url = "https://kukoshi.scala.jp")
val getA: String = requester.request()

// これは逆に作用します。
val requesterB: Request = new Request()
val getB: String = requester.request(url = "https://kukoshi.scala.jp")
```

### ヘッダ
`Map`や`Seq`対応しました（`Iterable[(String, String)]`も）。
```scala
// MapやSeqのサンプル
val requesterWithMapHeaders: Request = new Request(
  url = "https://kukoshi.scala.jp", 
  headers = Map("Content-Type" -> "User-Agent" -> "*", "Accept" -> "*/*")
)

val requesterWithSeqHeaders: Request = new Request(
  url = "https://kukoshi.scala.jp",
  headers = Seq("Content-Type" -> "User-Agent" -> "*", "Accept" -> "*/*")
)
```

### URLのパラメタ
URLのパラメタをリクエストに付加します場合、URLのパラメタは`request()`コールの内側にしか付加できないことを覚えておいてください。`Iterable[(String, String)]`は適応できます。
```scala
// リクエストへの『https://kukoshi.scala.jp?parameter=value』
val requesterA: Request = new Request()
val requestA: String = requester.request(url = "https://kukoshi.scala.jp", parameters = Map("parameter" -> "value"))

// リクエストへの『https://kukoshi.scala.jp?parameter1=value1&parameter2=value2』
val requesterB: Request = new Request()
val requestB: String = requester.request(url = "https://kukoshi.scala.jp", parameters = Map("parameter1" -> "value1", "parameter2" -> "value2"))
```

### 書き込み可能リクエスト
`request()`は書き込み可能リクエスト機能に使われます。
```scala
// POSTリクエストのサンプル。DELETE、PUT、PATCHは等しいです。
// ヘッダを入れることができます。
val requester: Request = new Request()
val POST: String = requester.request(url = "https://kukoshi.scala.jp", method = "POST", data = "{\"key\": \"value\"}")
```

### JSONデータ
「クコシ」には自分のJSONパーサやJSONシリアライザがあります。
  - JSONシリアライザは`Map`に対応しています、トップレベルのオブジェクトとしています。
    - `Map`、`List`、`Int`、`Boolean`、`String`に対応していますといった入れ子状のデータ。
    - トップレベルのオブジェクトとしての`List`は勧めません。

書き込み可能リクエストサンプルとJSONシリアライズド。
```scala
// RequestクラスでJSONオブジェクトをシリアライズド。
val requester: Request = new Request()
val POST: String = requester.request(
  url = "https://kukoshi.scala.jp", 
  method = "POST", 
  data = requester.JSON.encode(Map("key" -> "value"))
)
```

「クコシ」の`utility`パッケージから書き込み可能リクエストサンプルとJSONシリアライズド。
```scala
// ライブラリーのJSONオブジェクト。
import org.kukoshi.utility.JSON

val requester: Request = new Request()
val POST: String = requester.request(
  url = "https://kukoshi.scala.jp",
  method = "POST",
  data = JSON.encodeJSON(Map("key" -> "value"))
)
```

## 助けについて
リポジトリを助け合っていますについては[こちら](CONTRIBUTING.md)を読んでください。

## ライセンス
「Apache 2.0」ライセンス
