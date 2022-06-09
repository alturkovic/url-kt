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

data class Url(
    val protocol: HttpProtocol,
    val userInfo: UserInfo? = null,
    val host: Host,
    val path: Path? = null,
    val query: Query? = null,
    val fragment: String? = null,
)

enum class HttpProtocol(
    val defaultPort: Int
) {
    HTTP(80), HTTPS(443);

    override fun toString() = name.lowercase()
}

data class UserInfo(
    val user: String,
    val password: String? = null,
) {
    fun asString() = if (password != null) "$user:$password" else "$user:"
}

data class Host(
    val hostname: String,
    val port: Int? = null,
)

data class Path(
    val segments: List<String>
) {
    fun asString() = segments.joinToString("/")
}

data class Query(
    val parameters: List<QueryParameter>
) {
    fun asString() = parameters.joinToString("&") { it.asString() }
}

data class QueryParameter(
    val name: String,
    val value: String? = null,
) {
    fun asString() = if (value != null) "$name=$value" else name
}