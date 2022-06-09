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

internal class UrlBuilderTest {

    @Test
    internal fun shouldChangeProtocol() {
        val url = "https://example.com".buildUrl()
            .withProtocol(HttpProtocol.HTTP)
            .build()

        assertThat(url.asString()).isEqualTo("http://example.com")
    }

    @Test
    internal fun shouldAddUser() {
        val url = "https://example.com".buildUrl()
            .withUser("admin")
            .build()

        assertThat(url.asString()).isEqualTo("https://admin:@example.com")
    }

    @Test
    internal fun shouldChangeUser() {
        val url = "https://user:password@example.com".buildUrl()
            .withUser("admin")
            .build()

        assertThat(url.asString()).isEqualTo("https://admin:password@example.com")
    }

    @Test
    internal fun shouldAddUserEscaped() {
        val url = "https://example.com".buildUrl()
            .withUser("#admin")
            .build()

        assertThat(url.asString()).isEqualTo("https://%23admin:@example.com")
    }

    @Test
    internal fun shouldAddPassword() {
        val url = "https://user:@example.com".buildUrl()
            .withPassword("password")
            .build()

        assertThat(url.asString()).isEqualTo("https://user:password@example.com")
    }

    @Test
    internal fun shouldChangePassword() {
        val url = "https://user:secret@example.com".buildUrl()
            .withPassword("password")
            .build()

        assertThat(url.asString()).isEqualTo("https://user:password@example.com")
    }

    @Test
    internal fun shouldRemovePassword() {
        val url = "https://user:password@example.com".buildUrl()
            .removePassword()
            .build()

        assertThat(url.asString()).isEqualTo("https://user:@example.com")
    }

    @Test
    internal fun shouldAddPasswordEscaped() {
        val url = "https://user:@example.com".buildUrl()
            .withPassword("#password")
            .build()

        assertThat(url.asString()).isEqualTo("https://user:%23password@example.com")
    }

    @Test
    internal fun shouldRemoveUserInfo() {
        val url = "https://user:password@example.com".buildUrl()
            .removeUserInfo()
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com")
    }

    @Test
    internal fun shouldChangeHostname() {
        val url = "https://example.com".buildUrl()
            .withHostname("another.com")
            .build()

        assertThat(url.asString()).isEqualTo("https://another.com")
    }

    @Test
    internal fun shouldIncludeWwwWhenMissing() {
        val url = "https://example.com".buildUrl()
            .includeWww()
            .build()

        assertThat(url.asString()).isEqualTo("https://www.example.com")
    }

    @Test
    internal fun shouldIncludeWwwWhenExists() {
        val url = "https://www.example.com".buildUrl()
            .includeWww()
            .build()

        assertThat(url.asString()).isEqualTo("https://www.example.com")
    }

    @Test
    internal fun shouldExcludeWwwWhenMissing() {
        val url = "https://example.com".buildUrl()
            .excludeWww()
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com")
    }

    @Test
    internal fun shouldExcludeWwwWhenExists() {
        val url = "https://www.example.com".buildUrl()
            .excludeWww()
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com")
    }

    @Test
    internal fun shouldAddPort() {
        val url = "https://example.com".buildUrl()
            .withPort(8080)
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com:8080")
    }

    @Test
    internal fun shouldChangePort() {
        val url = "https://example.com:80".buildUrl()
            .withPort(8080)
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com:8080")
    }

    @Test
    internal fun shouldRemovePort() {
        val url = "https://example.com:80".buildUrl()
            .removePort()
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com")
    }

    @Test
    internal fun shouldAddPath() {
        val url = "https://example.com".buildUrl()
            .withPath("a/b/c")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com/a/b/c")
    }

    @Test
    internal fun shouldChangePath() {
        val url = "https://example.com/d/e/f".buildUrl()
            .withPath("a/b/c")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com/a/b/c")
    }

    @Test
    internal fun shouldRemovePath() {
        val url = "https://example.com/d/e/f".buildUrl()
            .removePath()
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com")
    }

    @Test
    internal fun shouldAddPathEscaped() {
        val url = "https://example.com".buildUrl()
            .withPath("a b c")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com/a%20b%20c")
    }

    @Test
    internal fun shouldChangePathWithLeadingSlash() {
        val url = "https://example.com".buildUrl()
            .withPath("/a/b/c")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com/a/b/c")
    }

    @Test
    internal fun shouldDropTrailingSlash() {
        val url = "https://example.com/".buildUrl()
            .withoutTrailingSlash()
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com")
    }

    @Test
    internal fun shouldDropTrailingSlashWithPath() {
        val url = "https://example.com/a/".buildUrl()
            .withoutTrailingSlash()
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com/a")
    }

    @Test
    internal fun shouldDropTrailingSlashWhenMissing() {
        val url = "https://example.com".buildUrl()
            .withoutTrailingSlash()
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com")
    }

    @Test
    internal fun shouldDropTrailingSlashWhenMissingWithPath() {
        val url = "https://example.com/a".buildUrl()
            .withoutTrailingSlash()
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com/a")
    }

    @Test
    internal fun shouldAppendSegment() {
        val url = "https://example.com/a".buildUrl()
            .appendSegment("b")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com/a/b")
    }

    @Test
    internal fun shouldAppendSegmentWithLeadingSlash() {
        val url = "https://example.com/a".buildUrl()
            .appendSegment("/b")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com/a/b")
    }

    @Test
    internal fun shouldAppendSegmentWhenPathIsMissing() {
        val url = "https://example.com".buildUrl()
            .appendSegment("a")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com/a")
    }

    @Test
    internal fun shouldAppendSegmentEscaped() {
        val url = "https://example.com/a".buildUrl()
            .appendSegment("b c")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com/a/b%20c")
    }

    @Test
    internal fun shouldAddQuery() {
        val url = "https://example.com".buildUrl()
            .withQuery("a=1&b&c=2")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com?a=1&b&c=2")
    }

    @Test
    internal fun shouldChangeQuery() {
        val url = "https://example.com?d=3".buildUrl()
            .withQuery("a=1&b&c=2")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com?a=1&b&c=2")
    }

    @Test
    internal fun shouldRemoveQuery() {
        val url = "https://example.com?a=1&b&c=2".buildUrl()
            .removeQuery()
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com")
    }

    @Test
    internal fun shouldAddQueryEscaped() {
        val url = "https://example.com".buildUrl()
            .withQuery("a=#")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com?a=%23")
    }

    @Test
    internal fun shouldAppendQueryParameterWhenQueryIsMissing() {
        val url = "https://example.com".buildUrl()
            .appendQueryParameter("a", "1")
            .appendQueryParameter("b")
            .appendQueryParameter("c", "2")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com?a=1&b&c=2")
    }

    @Test
    internal fun shouldAppendQueryParameterWithQuery() {
        val url = "https://example.com?a=1".buildUrl()
            .appendQueryParameter("b")
            .appendQueryParameter("c", "2")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com?a=1&b&c=2")
    }

    @Test
    internal fun shouldRemoveQueryParameter() {
        val url = "https://example.com?a=1&b&c=2&d=3".buildUrl()
            .removeQueryParameter("d")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com?a=1&b&c=2")
    }

    @Test
    internal fun shouldAppendQueryParameterEscaped() {
        val url = "https://example.com".buildUrl()
            .appendQueryParameter("a", "#")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com?a=%23")
    }

    @Test
    internal fun shouldAddFragment() {
        val url = "https://example.com".buildUrl()
            .withFragment("anchor")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com#anchor")
    }

    @Test
    internal fun shouldChangeFragment() {
        val url = "https://example.com#foo".buildUrl()
            .withFragment("anchor")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com#anchor")
    }

    @Test
    internal fun shouldRemoveFragment() {
        val url = "https://example.com#anchor".buildUrl()
            .removeFragment()
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com")
    }

    @Test
    internal fun shouldAddFragmentEscaped() {
        val url = "https://example.com".buildUrl()
            .withFragment("#anchor")
            .build()

        assertThat(url.asString()).isEqualTo("https://example.com#%23anchor")
    }
}
