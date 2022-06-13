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
import com.github.alturkovic.url.UrlParser.parse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class UrlParserTest {

    @Test
    internal fun shouldParseFullSimpleUrl() {
        val urlString = "https://admin:password@www.example.com:8080/a/b/c?a=1&b&c=2#anchor"

        assertUrlParsing(
            url = urlString,
            expected = Url(
                protocol = HTTPS,
                userInfo = UserInfo(
                    user = "admin",
                    password = "password"
                ),
                host = Host(
                    hostname = "www.example.com",
                    port = 8080
                ),
                path = Path(
                    segments = listOf("a", "b", "c")
                ),
                query = Query(
                    parameters = listOf(
                        QueryParameter("a", "1"),
                        QueryParameter("b"),
                        QueryParameter("c", "2")
                    )
                ),
                fragment = "anchor"
            ),
            expectedUrl = urlString
        )
    }

    @Test
    internal fun shouldParseUrlWithHttpProtocolsOnly() {
        assertThat(parse("http://www.example.com").protocol).isEqualTo(HTTP)
        assertThat(parse("https://www.example.com").protocol).isEqualTo(HTTPS)
        assertThat(parse("HTTP://WWW.EXAMPLE.COM").protocol).isEqualTo(HTTP)
        assertThat(parse("HTTPS://WWW.EXAMPLE.COM").protocol).isEqualTo(HTTPS)
        assertThrows<UrlParseException> { parse("mailto:admin@example.com") }
        assertThrows<UrlParseException> { parse("redis://localhost/") }
    }

    @Test
    internal fun shouldParseHostnameWithSingleLabel() {
        assertUrlParsing(
            url = "https://localhost:8080",
            expected = Url(
                protocol = HTTPS,
                host = Host(
                    hostname = "localhost",
                    port = 8080
                ),
            ),
            expectedUrl = "https://localhost:8080"
        )
    }

    @Test
    internal fun shouldParseHostnameOnlyUrls() {
        assertUrlParsing(
            url = "example.com",
            expected = Url(
                protocol = HTTPS,
                host = Host(hostname = "example.com")
            ),
            expectedUrl = "https://example.com"
        )

        assertUrlParsing(
            url = "sub.example.com",
            expected = Url(
                protocol = HTTPS,
                host = Host(hostname = "sub.example.com")
            ),
            expectedUrl = "https://sub.example.com"
        )
    }

    @Test
    internal fun shouldParseSingleCharacterHostname() {
        assertUrlParsing(
            url = "x.com",
            expected = Url(
                protocol = HTTPS,
                host = Host(hostname = "x.com")
            ),
            expectedUrl = "https://x.com"
        )
    }

    @Test
    internal fun shouldParsePath() {
        assertUrlParsing(
            url = "example.com/a/b/c",
            expected = Url(
                protocol = HTTPS,
                host = Host(hostname = "example.com"),
                path = Path(
                    segments = listOf("a", "b", "c")
                )
            ),
            expectedUrl = "https://example.com/a/b/c"
        )
    }

    @Test
    internal fun shouldParseQuery() {
        assertUrlParsing(
            url = "example.com?a=1",
            expected = Url(
                protocol = HTTPS,
                host = Host(hostname = "example.com"),
                query = Query(
                    parameters = listOf(
                        QueryParameter("a", "1")
                    )
                )
            ),
            expectedUrl = "https://example.com?a=1"
        )
    }

    @Test
    internal fun shouldParseFragment() {
        assertUrlParsing(
            url = "example.com#anchor",
            expected = Url(
                protocol = HTTPS,
                host = Host(hostname = "example.com"),
                fragment = "anchor"
            ),
            expectedUrl = "https://example.com#anchor"
        )
    }

    @Test
    internal fun shouldParseUnescapedPath() {
        assertUrlParsing(
            url = "example.com/a b/c+d/{e}",
            expected = Url(
                protocol = HTTPS,
                host = Host(hostname = "example.com"),
                path = Path(
                    segments = listOf("a b", "c+d", "{e}")
                )
            ),
            expectedUrl = "https://example.com/a%20b/c+d/%7Be%7D"
        )
    }

    @Test
    internal fun shouldParseEscapedPath() {
        assertUrlParsing(
            url = "example.com/a%20b/c+d/%7Be%7D",
            expected = Url(
                protocol = HTTPS,
                host = Host(hostname = "example.com"),
                path = Path(
                    segments = listOf("a b", "c+d", "{e}")
                )
            ),
            expectedUrl = "https://example.com/a%20b/c+d/%7Be%7D"
        )
    }

    @Test
    internal fun shouldParseTrailingSlash() {
        assertUrlParsing(
            url = "example.com/",
            expected = Url(
                protocol = HTTPS,
                host = Host(hostname = "example.com"),
                path = Path(
                    segments = emptyList()
                )
            ),
            expectedUrl = "https://example.com/"
        )
    }

    @Test
    internal fun shouldParseQueryWithHashtagParameterValue() {
        assertUrlParsing(
            url = "http://www.example.com?a=%23#anchor",
            expected = Url(
                protocol = HTTP,
                host = Host(hostname = "www.example.com"),
                query = Query(
                    parameters = listOf(
                        QueryParameter("a", "#")
                    )
                ),
                fragment = "anchor"
            ),
            expectedUrl = "http://www.example.com?a=%23#anchor"
        )
    }

    @Test
    internal fun shouldParseEmptyQuery() {
        assertUrlParsing(
            url = "http://www.example.com?",
            expected = Url(
                protocol = HTTP,
                host = Host(hostname = "www.example.com"),
                query = Query(
                    parameters = emptyList()
                )
            ),
            expectedUrl = "http://www.example.com?"
        )
    }

    @Test
    internal fun shouldParseEmptyFragment() {
        assertUrlParsing(
            url = "http://www.example.com#",
            expected = Url(
                protocol = HTTP,
                host = Host(hostname = "www.example.com"),
                fragment = ""
            ),
            expectedUrl = "http://www.example.com#"
        )
    }

    @Test
    internal fun shouldParseEmptyQueryAndEmptyFragment() {
        assertUrlParsing(
            url = "http://www.example.com?#",
            expected = Url(
                protocol = HTTP,
                host = Host(hostname = "www.example.com"),
                query = Query(
                    parameters = emptyList()
                ),
                fragment = ""
            ),
            expectedUrl = "http://www.example.com?#"
        )
    }

    @Test
    internal fun shouldParseEmptyQueryWithFragment() {
        assertUrlParsing(
            url = "http://www.example.com?#anchor",
            expected = Url(
                protocol = HTTP,
                host = Host(hostname = "www.example.com"),
                query = Query(
                    parameters = emptyList()
                ),
                fragment = "anchor"
            ),
            expectedUrl = "http://www.example.com?#anchor"
        )
    }

    @Test
    internal fun shouldParseEmptyPathWithQuery() {
        assertUrlParsing(
            url = "http://www.example.com/?a=1",
            expected = Url(
                protocol = HTTP,
                host = Host(hostname = "www.example.com"),
                path = Path(
                    segments = emptyList()
                ),
                query = Query(
                    parameters = listOf(
                        QueryParameter("a", "1")
                    )
                )
            ),
            expectedUrl = "http://www.example.com/?a=1"
        )
    }

    @Test
    internal fun shouldParseEmptyPathWithEmptyQueryAndEmptyFragment() {
        assertUrlParsing(
            url = "http://www.example.com/?#",
            expected = Url(
                protocol = HTTP,
                host = Host(hostname = "www.example.com"),
                path = Path(
                    segments = emptyList()
                ),
                query = Query(
                    parameters = emptyList()
                ),
                fragment = ""
            ),
            expectedUrl = "http://www.example.com/?#"
        )
    }

    @Test
    internal fun shouldParsePathWithQuery() {
        assertUrlParsing(
            url = "http://www.example.com/a/b/c?a=1",
            expected = Url(
                protocol = HTTP,
                host = Host(hostname = "www.example.com"),
                path = Path(
                    segments = listOf("a", "b", "c")
                ),
                query = Query(
                    parameters = listOf(
                        QueryParameter("a", "1")
                    )
                )
            ),
            expectedUrl = "http://www.example.com/a/b/c?a=1"
        )
    }

    @Test
    internal fun shouldParsePathWithFragment() {
        assertUrlParsing(
            url = "http://www.example.com/a/b/c#anchor",
            expected = Url(
                protocol = HTTP,
                host = Host(hostname = "www.example.com"),
                path = Path(
                    segments = listOf("a", "b", "c")
                ),
                fragment = "anchor"
            ),
            expectedUrl = "http://www.example.com/a/b/c#anchor"
        )
    }

    @Test
    internal fun shouldNotParseHostnameWithPortWithoutProtocol() {
        assertThrows<UrlParseException> { parse("www.example.com:8080") }
    }

    @Test
    internal fun shouldNotAcceptBlankUrl() {
        assertThrows<UrlParseException> { parse(" ") }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            ":example",
            "://example",
            "http://example.com:",
            "http://example.com:invalid",
            "http://example.com:1:2",
            "http://@example.com",
            "http://user:password:@example.com",
            "http://:user:password@example.com",
            "http://user:password:extra@example.com",
            "http://:password@example.com",
        ]
    )
    internal fun shouldNotParseIllegallyFormattedUrl(url: String) {
        assertThrows<UrlParseException> { parse(url) }
    }

    private fun assertUrlParsing(url: String, expected: Url, expectedUrl: String) {
        parse(url).apply {
            assertThat(this).isEqualTo(expected)
            assertThat(this.asString()).isEqualTo(expectedUrl)
        }
    }
}