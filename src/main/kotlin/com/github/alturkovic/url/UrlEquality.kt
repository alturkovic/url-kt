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

/**
 * Check if 2 [Url] instances are effectively the same.
 *
 * Ignores trailing slashes, default ports and escaped characters.
 */
object UrlEquality {
    fun check(url: Url, other: Url) : Boolean {
        if (url === other) return true
        if (url == other) return true

        if (url.protocol != other.protocol) return false
        if (url.host.hostname != other.host.hostname) return false
        if (url.resolvePort() != other.resolvePort()) return false
        if (url.effectivePath() != other.effectivePath()) return false
        if (url.escapedQuery() != other.escapedQuery()) return false
        if (url.escapedFragment() != other.escapedFragment()) return false

        return true
    }

    private fun Url.resolvePort() = host.port?: protocol.defaultPort
    private fun Url.effectivePath() = UrlBuilder(this).withoutTrailingSlash().build().path?.asString()?.escape()
    private fun Url.escapedQuery() = query?.asString()?.escape()
    private fun Url.escapedFragment() = fragment?.escape()
}