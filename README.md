![Java](https://img.shields.io/badge/Java-8%2B-ED8B00?style=for-the-badge&labelColor=ED8B00&logo=java&color=808080) ![JitPack](https://img.shields.io/jitpack/v/github/alturkovic/url-kt?style=for-the-badge&labelColor=007ec5&color=808080&logo=Git&logoColor=white) ![License](https://img.shields.io/github/license/alturkovic/url-kt?style=for-the-badge&color=808080&logo=Open%20Source%20Initiative&logoColor=white)

`url-kt` helps you operate with http(s) URLs in Kotlin JVM.

## Why?

- `java.net.URL.equals()` [does not work correctly](https://stackoverflow.com/questions/3771081/proper-way-to-check-for-url-equality/3771123#3771123).

- `java.net.URL` and `java.net.URI` are a pain to parse. For an example, if you try to parse `www.example.com` as `java.net.URI` using `URI.create(url)` and call `uri.getHost()`, you will get `null` since it will put `www.example.com` as path.

- `java.net.URL` and `java.net.URI` are hard to manipulate.

- `java.net.URI` will only parse `RFC 2396` URI syntax and fail with any non-compliant URI. A lot of URIs used on the internet do not follow this (old) syntax.

## How?

### Parse String to Url

```kotlin
val url : Url = "www.example.com".toUrl()
```

### Convert Url to String

```kotlin
val url : String = "www.example.com".asString()
```

### Convert Url to URI

```kotlin
val uri : URI = "www.example.com".toUrl().toUri()
```

### Convert String to URI

```kotlin
val uri : URI = "www.example.com".toUri()
```

### Compare Urls

```kotlin
UrlEquality.check(
    "www.example.com".toUrl(), 
    "https://www.example.com:443".toUrl()
) // true

UrlEquality.check(
    "http://www.example.com:80/".toUrl(), 
    "http://www.example.com/".toUrl()
) // true
```

### Manipulate Urls

```kotlin
"https://example.com".buildUrl()
    .withUser("admin")
    .withPassword("password")
    .build().asString() // https://admin:password@example.com

"https://example.com".buildUrl()
    .includeWww()
    .build().asString() // https://www.example.com

"https://example.com:80".buildUrl()
    .removePort()
    .build().asString() // https://example.com

"https://example.com/a".buildUrl()
    .appendSegment("b")
    .build().asString() // https://example.com/a/b
```

## Gotchas

When defining a custom port, you **must** specify the protocol because otherwise there is no distinction if the part before ':' defines the protocol or the host.

Default protocol used for parsing is HTTPS, but can be switched to HTTPS by calling `UrlParser.parse(url, HTTP)`

## Importing into your project using Maven

Add the JitPack repository to your `pom.xml`.

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the following under your `<dependencies>`:

```xml
<dependency>
    <groupId>com.github.alturkovic</groupId>
    <artifactId>url-kt</artifactId>
    <version>1.0.1</version>
</dependency>
```
