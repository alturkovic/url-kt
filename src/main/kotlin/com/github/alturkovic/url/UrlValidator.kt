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
 * Validate parsed [Url] is valid.
 */
object UrlValidator {

    /**
     * Validate [url] instance is not malformed.
     *
     * @throws UrlValidationException containing all errors found
     */
    fun validate(url: Url) {
        val validationErrors = mutableListOf<String>()

        url.host.hostname.let { validateHostname(validationErrors, it) }
        url.host.port?.let { validatePort(validationErrors, it) }

        if (validationErrors.isNotEmpty()) {
            throw UrlValidationException(url, validationErrors)
        }
    }

    private fun validateHostname(validationErrors: MutableList<String>, hostname: String) {
        if (hostname.isBlank()) {
            validationErrors += "Blank hostname"
            return
        }

        if (hostname.length > 255) validationErrors += "Hostname is too long (${hostname.length} > 255)"
        if (!hostname.contains('.')) validationErrors += "Hostname without dot"
        hostname.split('.').forEach { label ->
            if (label.isBlank()) {
                validationErrors += "Blank hostname label"
                return
            }
            if (label.startsWith("-")) validationErrors += "Label ($hostname) starts with -"
            if (label.endsWith("-")) validationErrors += "Label ($hostname) ends with -"
            label.forEach { char ->
                if (!char.isHostnameCharacter()) validationErrors += "Illegal label ($label) character ($char)"
            }
        }
    }

    private fun validatePort(validationErrors: MutableList<String>, port: Int) {
        if (port !in 1..65535) validationErrors += "Invalid port: $port"
    }

    private fun Char.isHostnameCharacter() = isLetterOrDigit() || this == '-'
}

class UrlValidationException(
    val url: Url,
    val errors: List<String>
) : RuntimeException(errors.joinToString(", ") + " in $url")