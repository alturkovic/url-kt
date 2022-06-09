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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class UrlEqualityTest {

    @Test
    internal fun shouldEqualByInstance() {
        val url = "https://www.example.com".toUrl()
        assertThat(UrlEquality.check(url, url)).isTrue
    }

    @Test
    internal fun shouldEqualByContent() {
        assertThat(UrlEquality.check(
            "https://www.example.com".toUrl(),
            "https://www.example.com".toUrl())
        ).isTrue
    }

    @Test
    internal fun shouldEqualWithSpecifiedDefaultHttpsPort() {
        assertThat(UrlEquality.check(
            "https://www.example.com:443".toUrl(),
            "www.example.com".toUrl())
        ).isTrue
    }

    @Test
    internal fun shouldEqualWithSpecifiedDefaultHttpPort() {
        assertThat(UrlEquality.check(
            "http://www.example.com:80".toUrl(),
            "http://www.example.com".toUrl())
        ).isTrue
    }

    @Test
    internal fun shouldEqualWithSamePort() {
        assertThat(UrlEquality.check(
            "http://www.example.com:8080".toUrl(),
            "http://www.example.com:8080".toUrl())
        ).isTrue
    }

    @Test
    internal fun shouldEqualWithTrailingSlash() {
        assertThat(UrlEquality.check(
            "https://www.example.com/".toUrl(),
            "https://www.example.com".toUrl())
        ).isTrue
    }

    @Test
    internal fun shouldEqualWithTrailingSlashWithPath() {
        assertThat(UrlEquality.check(
            "https://www.example.com/a/b/c/".toUrl(),
            "https://www.example.com/a/b/c".toUrl())
        ).isTrue
    }

    @Test
    internal fun shouldEqualWithEscapedPath() {
        assertThat(UrlEquality.check(
            "https://www.example.com/a b".toUrl(),
            "https://www.example.com/a%20b".toUrl())
        ).isTrue
    }

    @Test
    internal fun shouldEqualWithEscapedQuery() {
        assertThat(UrlEquality.check(
            "https://www.example.com?a= &b".toUrl(),
            "https://www.example.com?a=%20&b".toUrl())
        ).isTrue
    }

    @Test
    internal fun shouldEqualWithEscapedFragment() {
        assertThat(UrlEquality.check(
            "https://www.example.com#anchor#".toUrl(),
            "https://www.example.com#anchor%23".toUrl())
        ).isTrue
    }
}