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

「Kukoshi」は、HTTPとHTTPSのリクエストを作るScalaライブラリーです。
## 纏め
KukoshiはHTTPリクエストの利用を簡単になります。このクライアントはボイラプレートのセッティングや色々なインポートが不必要です。

## 機能
- 圧縮されたデータは、GZIPとDeflateがサポートされている。
- GET、POST、DELETE、PUT、HEAD、OPTIONS、PATCHソッド可能です。
- Map` か `Seq` 形式のHEADERをサポートしています（`Iterable[(String, String)]` として適応できるCollectionで動作する）
- `Iterable[(String, String)]` (`Map`、`Seq`)のURLパラメータもできます.
- **ボーナス機能:**
    - 外部Library不要
    - デフォルト：GET.
    - Headerに基づくRequest`head`か`options`
    - `amend()`は`head()`のデータから`Map[String, List[String]]`型のデータをきれいに整形される。
    - `Utility`オブジェクトの追加ユーティリティ有り。

## Installation 
#### MAIN方法 
TOKENには許可の`read:packages`が必要です。ユーザー名のフィールドは空白ストリングのなるのができます。
```sbt 
credentials += Credentials(
  realm = "GitHub Package Registry",
  host = "maven.pkg.github.com",
  userName = "",
  passwd = "<READ_PACKAGES_TOKEN>"
)

resolvers += "GitHub Package Registry (KiyonoKara/Kukoshi)" at "https://maven.pkg.github.com/KiyonoKara/Kukoshi"
libraryDependencies += "org.kukoshi" %% "kukoshi" % "2.0.0"
```

#### 代替のインストール
```sbt
lazy val http = RootProject(uri("git://github.com/KiyonoKara/Kukoshi.git"))
lazy val http_root = project in file(".") dependsOn http
```

## 文献集
書き込み禁止メソッドは一つしかない、どちらが`GET`。`POST`、`DELETE`、`PUT`、`PATCH`は書き込まれますはなるのができます。

※ サンプルURLは`https://kukoshi.scala.jp`になります。URLは存在されません。

### インポート
ライブラリーの`Request`のクラスをインポートして。
```scala
import org.kukoshi.Request
```  

### 表明
主に、`Request`のクラスを介して使用されます。`Request`のオブジェクトを作り上げます。
```scala
// Requestのオブジェクト 
val requester: Request = new Request()
```  

任意で、構築子の中URL、METHOD、HEADERSを宣言すできます。

### リクエストの作り方
読み取り専用のリクエストのサンプル
```scala
// METHODなければ、GETを使います。
// コンストラクタのURLを提供しました。
val requesterA: Request = new Request(url = "https://kukoshi.scala.jp")
val getA: String = requester.request()

// 逆に作用できます。
val requesterB: Request = new Request()
val getB: String = requester.request(url = "https://kukoshi.scala.jp")
```

### Headers
`Map`、`Seq`対応しました（`Iterable[(String, String)]`も）。
```scala
// Map、Seqサンプル
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
URLパラメタをリクエストに付加する場合、URLパラメタは`request()`にコールの内側にしか付加できないことが可能です。`Iterable[(String, String)]`もできます。
```scala
// リクエストへの『https://kukoshi.scala.jp?parameter=value』
val requesterA: Request = new Request()
val requestA: String = requester.request(url = "https://kukoshi.scala.jp", parameters = Map("parameter" -> "value"))

// リクエストへの『https://kukoshi.scala.jp?parameter1=value1&parameter2=value2』
val requesterB: Request = new Request()
val requestB: String = requester.request(url = "https://kukoshi.scala.jp", parameters = Map("parameter1" -> "value1", "parameter2" -> "value2"))
```

### 書き込み可能なリクエスト
`request()`は書き込み可能リクエスト関数に使われます。
```scala
// POSTリクエストのサンプル。DELETE、PUT、PATCHは等しいです。
// ヘッダを入れることができます。
val requester: Request = new Request()
val POST: String = requester.request(url = "https://kukoshi.scala.jp", method = "POST", data = "{\"key\": \"value\"}")
```

## コントリビュートについて
リポジトリをコントリビュートするについては[こちら](CONTRIBUTING.md)です。読んでください。

## ライセンス
[Apache 2.0ライセンス](LICENSE.md)
