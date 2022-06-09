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
 * Used to manipulate built [Url] instances.
 */
class UrlBuilder(
    original: Url
) {
    private var url = original

    /**
     * Ensure built [Url] uses [protocol] as protocol.
     */
    fun withProtocol(protocol: HttpProtocol): UrlBuilder {
        url = url.copy(protocol = protocol)
        return this
    }

    /**
     * Ensure built [Url] uses [user] as user.
     */
    fun withUser(user: String): UrlBuilder {
        if (url.userInfo != null) {
            url = url.copy(userInfo = url.userInfo!!.copy(user = user))
        } else {
            url = url.copy(userInfo = UserInfo(user))
        }
        return this
    }

    /**
     * Ensure built [Url] uses [password] as password.
     *
     * @throws IllegalStateException if user is not set
     */
    fun withPassword(password: String?): UrlBuilder {
        if (url.userInfo == null) {
            throw IllegalStateException("Cannot set password without user: $url")
        }

        url = url.copy(userInfo = url.userInfo!!.copy(password = password))
        return this
    }

    /**
     * Ensure built [Url] has no password.
     */
    fun removePassword() = withPassword(null)

    /**
     * Ensure built [Url] has no userInfo.
     */
    fun removeUserInfo(): UrlBuilder {
        url = url.copy(userInfo = null)
        return this
    }

    /**
     * Ensure built [Url] uses [hostname] as hostname.
     */
    fun withHostname(hostname: String): UrlBuilder {
        url = url.copy(host = url.host.copy(hostname = hostname))
        return this
    }

    /**
     * Ensure built [Url] hostname includes 'www.'.
     */
    fun includeWww(): UrlBuilder {
        url = url.copy(host = url.host.copy(hostname = url.host.hostname.withPrefix("www.")))
        return this
    }

    /**
     * Ensure built [Url] hostname excludes 'www.'.
     */
    fun excludeWww(): UrlBuilder {
        url = url.copy(host = url.host.copy(hostname = url.host.hostname.removePrefix("www.")))
        return this
    }

    /**
     * Ensure built [Url] uses [port] as port.
     */
    fun withPort(port: Int?): UrlBuilder {
        url = url.copy(host = url.host.copy(port = port))
        return this
    }

    /**
     * Ensure built [Url] has no port.
     */
    fun removePort() = withPort(null)

    /**
     * Ensure built [Url] uses [path] as path. Ignores leading '/'.
     */
    fun withPath(path: String): UrlBuilder {
        url = url.copy(path = UrlParser.asPath(path.removePrefix("/")))
        return this
    }

    /**
     * Ensure built [Url] path has no trailing slash.
     */
    fun withoutTrailingSlash(): UrlBuilder {
        url.path?.let { path ->
            if (path.segments.isEmpty()) {
                url = url.copy(path = null)
            } else if (path.segments.last().isBlank()) {
                url = url.copy(path = path.copy(segments = path.segments.dropLast(1)))
            }
        }
        return this
    }

    /**
     * Append segment to the built [Url] path. Ignores leading '/'.
     */
    fun appendSegment(segment: String): UrlBuilder {
        val existingPath = url.path ?: Path(emptyList())
        val cleanedSegment = segment.removePrefix("/")
        return withPath("${existingPath.asString()}/$cleanedSegment")
    }

    /**
     * Ensure built [Url] has no path.
     */
    fun removePath(): UrlBuilder {
        url = url.copy(path = null)
        return this
    }

    /**
     * Ensure built [Url] uses [query] as query.
     */
    fun withQuery(query: String): UrlBuilder {
        url = url.copy(query = UrlParser.asQuery(query))
        return this
    }

    /**
     * Append query to the built [Url].
     */
    fun appendQueryParameter(name: String, value: String? = null): UrlBuilder {
        val existingQuery = url.query ?: Query(emptyList())
        url = url.copy(query = existingQuery.copy(parameters = existingQuery.parameters + QueryParameter(name, value)))
        return this
    }

    /**
     * Ensure built [Url] has no query parameter named [name].
     */
    fun removeQueryParameter(name: String): UrlBuilder {
        url.query?.let { query ->
            url = url.copy(query = query.copy(parameters = query.parameters.filterNot { it.name == name }))
        }

        return this
    }

    /**
     * Ensure built [Url] has no query.
     */
    fun removeQuery(): UrlBuilder {
        url = url.copy(query = null)
        return this
    }

    /**
     * Ensure built [Url] uses [fragment] as fragment.
     */
    fun withFragment(fragment: String?): UrlBuilder {
        url = url.copy(fragment = fragment)
        return this
    }

    /**
     * Ensure built [Url] has no fragment.
     */
    fun removeFragment() = withFragment(null)

    /**
     * Get built [Url].
     */
    fun build() = url.also { UrlValidator.validate(it) }
}