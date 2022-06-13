/*
 * MIT License
 *
 * Copyright (c) 2022 Alen Turkovic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.alturkovic.url

import com.github.alturkovic.url.HttpProtocol.HTTP
import com.github.alturkovic.url.HttpProtocol.HTTPS
import com.github.alturkovic.url.UrlParser.Parser.Stage.*
import com.github.alturkovic.url.UrlValidator.validate

/**
 * Parse raw strings to [Url] instances.
 */
object UrlParser {

    /**
     * Parse [url] into a valid [Url].
     */
    fun parse(url: String, defaultProtocol: HttpProtocol = HTTPS): Url {
        if (url.isBlank()) throw UrlParseException("Cannot parse blank url")

        val parser = Parser(url)

        val parsedUrl = Url(
            protocol = parser.extractHttpProtocol() ?: defaultProtocol,
            userInfo = parser.extractUserInfo(),
            host = parser.extractHost(),
            path = parser.extractPath(),
            query = parser.extractQuery(),
            fragment = parser.extractFragment(),
        )

        if (parser.nextStage != DONE) throw UrlParseException("Parsed $url and reached ${parser.nextStage}")

        return parsedUrl.also { validate(it) }
    }

    private class Parser(val url: String) {
        var remainingUrl = url
        var nextStage = PROTOCOL

        enum class Stage {
            PROTOCOL, USER_INFO, HOST, PATH, QUERY, FRAGMENT, DONE
        }

        fun extractHttpProtocol(): HttpProtocol? {
            if (nextStage != PROTOCOL) throw UrlParseException("Reached $PROTOCOL @ $nextStage")

            val protocol = extractDelimited(":")
            if (protocol.isNotNullAndIsBlank()) throw UrlParseException("Missing protocol before ':' in $url")
            if (remainingUrl.startsWith("//")) cutNextCharacters(2)

            return protocol?.let { asProtocol(it) }.also { nextStage = USER_INFO }
        }

        fun extractUserInfo(): UserInfo? {
            if (nextStage != USER_INFO) return null

            val userInfo = extractDelimited("@")
            if (userInfo.isNotNullAndIsBlank()) throw UrlParseException("Missing user info before '@' in $url")

            return userInfo?.let { asUserInfo(it) }.also { nextStage = HOST }
        }

        fun extractHost(): Host {
            if (nextStage != HOST) throw UrlParseException("Reached $HOST @ $nextStage")

            val host = extractDelimited("/")?.also { nextStage = PATH }
                ?: extractDelimited("?")?.also { nextStage = QUERY }
                ?: extractDelimited("#")?.also { nextStage = FRAGMENT }
                ?: extractRestOfUrl().ifBlank { throw UrlParseException("Missing host in $url") }

            return asHost(host)
        }

        fun extractPath(): Path? {
            if (nextStage != PATH) return null

            val path = extractDelimited("?").also { nextStage = QUERY }
                ?: extractDelimited("#").also { nextStage = FRAGMENT }
                ?: extractRestOfUrl()

            return asPath(path)
        }

        fun extractQuery(): Query? {
            if (nextStage != QUERY) return null

            // in case of blank path but existing query/fragment
            if (remainingUrl.startsWith("?")) cutNextCharacters(1)

            val query = extractDelimited("#").also { nextStage = FRAGMENT }
                ?: extractRestOfUrl()

            return asQuery(query)
        }

        fun extractFragment(): String? {
            if (nextStage != FRAGMENT) return null

            // in case of blank query but existing fragment
            if (remainingUrl.startsWith("#")) cutNextCharacters(1)

            return extractRestOfUrl().escape()
        }

        private fun extractDelimited(delimiter: String): String? {
            val endIndex = remainingUrl.indexOf(delimiter)
            if (endIndex == -1) return null

            val value = remainingUrl.substring(0, endIndex)
            if (value.isNotBlank()) remainingUrl = remainingUrl.substring(endIndex + delimiter.length)
            return value
        }

        private fun cutNextCharacters(charactersToRemove: Int) {
            remainingUrl = remainingUrl.substring(charactersToRemove)
        }

        private fun extractRestOfUrl(): String {
            val result = remainingUrl
            remainingUrl = ""
            nextStage = DONE
            return result
        }
    }

    /**
     * Convert [protocol] to [HttpProtocol].
     *
     * @throws UrlParseException if protocol is not supported
     */
    fun asProtocol(protocol: String): HttpProtocol {
        if (protocol.equals("http", true)) return HTTP
        if (protocol.equals("https", true)) return HTTPS

        throw UrlParseException("$protocol protocol is not supported")
    }

    /**
     * Convert [userInfo] to [UserInfo].
     *
     * @throws UrlParseException if [userInfo] is malformed or user is missing
     */
    fun asUserInfo(userInfo: String): UserInfo {
        val parts = userInfo.split(":")
        if (parts.size > 2) throw UrlParseException("Malformed user info ($userInfo)")
        if (parts[0].isBlank()) throw UrlParseException("Missing user in user info ($userInfo)")

        return if (parts.size == 1) UserInfo(user = parts[0].escape())
        else UserInfo(
            user = parts[0].escape(),
            password = parts[1].escape()
        )
    }

    /**
     * Convert [host] to [Host].
     *
     * @throws UrlParseException if [host] is malformed or missing
     * @throws UrlParseException if [host] contains invalid port
     */
    fun asHost(host: String): Host {
        val parts = host.split(":")
        if (parts.size > 2) throw UrlParseException("Malformed host ($host)")
        if (parts[0].isBlank()) throw UrlParseException("Missing hostname in host ($host)")

        if (parts.size == 1) return Host(hostname = parts[0])

        return Host(
            hostname = parts[0],
            port = asPort(parts[1])
        )
    }

    /**
     * Convert [port] to [Int].
     *
     * @throws UrlParseException if [port] is invalid
     */
    fun asPort(port: String): Int = try {
        port.toInt()
    } catch (e: Exception) {
        throw UrlParseException("Malformed port number ($port)")
    }

    /**
     * Convert [path] to [Path].
     */
    fun asPath(path: String): Path {
        if (path.isBlank()) return Path(emptyList())

        return Path(path.split("/").map { it.escape() })
    }

    /**
     * Convert [query] to [Query].
     */
    fun asQuery(query: String): Query {
        if (query.isBlank()) return Query(emptyList())

        return Query(query.split("&").map {
            if (it.contains("=")) {
                val parts = it.split("=")
                QueryParameter(name = parts[0].escape(), value = parts[1].escape())
            } else QueryParameter(name = it.escape())
        })
    }
}

class UrlParseException(msg: String) : RuntimeException(msg)