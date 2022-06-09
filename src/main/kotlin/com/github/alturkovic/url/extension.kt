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

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URI
import java.nio.charset.StandardCharsets.UTF_8

/**
 * Returns `true` if this string is empty or consists solely of whitespace characters and is not null.
 */
fun String?.isNotNullAndIsBlank() = this?.isBlank() ?: false

/**
 * If this string does not start with the given [prefix], returns a copy of this string
 * with the prefix. Otherwise, returns this string.
 */
fun String.withPrefix(prefix: String) = if (startsWith(prefix)) this else "$prefix$this"

/**
 * Converts this string to [Url].
 *
 * @see UrlParser.parse
 */
fun String.toUrl() = UrlParser.parse(this)

/**
 * Converts this string to [UrlBuilder] for this string.
 */
fun String.buildUrl() = UrlBuilder(this.toUrl())

/**
 * Converts this string to [URI].
 *
 * @see UrlParser.parse
 * @see Url.toUri
 */
fun String.toUri() = toUrl().toUri()

/**
 * Converts [Url] to [URI].
 */
fun Url.toUri() = URI(
    protocol.toString(),
    userInfo?.asString()?.escape(),
    host.hostname,
    host.port ?: -1,
    path?.asString()?.withPrefix("/")?.escape(),
    query?.asString()?.escape(),
    fragment?.escape()
)

/**
 * Converts [Url] to a human-readable representation.
 *
 * @see URI.toString
 */
fun Url.asString(): String = toUri().toString()

/**
 * Escape this string to be URL safe.
 */
fun String.escape(): String = percentDecode(this)

// taken from: https://github.com/smola/galimatias
private fun percentDecode(input: String): String {
    return if (input.isEmpty()) {
        input
    } else try {
        val bytes = ByteArrayOutputStream()
        var idx = 0
        while (idx < input.length) {
            var isEOF = false
            var c = input.codePointAt(idx)
            while (!isEOF && c != '%'.code) {
                if (c <= 0x7F) {
                    bytes.write(c.toByte().toInt())
                    idx++
                } else {
                    bytes.write(String(Character.toChars(c)).toByteArray(UTF_8))
                    idx += Character.charCount(c)
                }
                isEOF = idx >= input.length
                c = if (isEOF) 0x00 else input.codePointAt(idx)
            }
            if (c == '%'.code && (input.length <= idx + 2 ||
                        !input[idx + 1].isHexCharacter() ||
                        !input[idx + 2].isHexCharacter())
            ) {
                if (c <= 0x7F) {
                    bytes.write(c.toByte().toInt())
                    idx++
                } else {
                    bytes.write(String(Character.toChars(c)).toByteArray(UTF_8))
                    idx += Character.charCount(c)
                }
            } else {
                while (c == '%'.code && input.length > idx + 2 &&
                    input[idx + 1].isHexCharacter() &&
                    input[idx + 2].isHexCharacter()
                ) {
                    bytes.write(hexToInt(input[idx + 1], input[idx + 2]))
                    idx += 3
                    c = if (input.length <= idx) 0x00 else input.codePointAt(idx)
                }
            }
        }
        String(bytes.toByteArray(), UTF_8)
    } catch (ex: IOException) {
        throw RuntimeException(ex)
    }
}

private fun Char.isHexCharacter(): Boolean = this in 'A'..'F' || this in 'a'..'f' || isDigit()

private fun hexToInt(c1: Char, c2: Char): Int = String(charArrayOf(c1, c2)).toInt(16)