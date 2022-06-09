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

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.random.Random

internal class UrlValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = [
        "http://example",
        "http://www.example..com",
        "http://www.sub.-example.com",
        "http://www.exam!ple.com",
        "http://www.exam ple.com",
        "http://www.sub.example-.com",
        "http://www.example.com:-1",
        "http://www.example.com:0",
        "http://www.example.com:65536"
    ])
    internal fun shouldNotValidateIllegalUrl(url: String) {
        assertThrows<UrlValidationException> { url.toUrl() }
            .also { Assertions.assertThat(it.errors.size).isEqualTo(1) }
    }

    @Test
    internal fun shouldNotValidateTooLongHostname() {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        fun tooLongHostname(length: Int) = (1..length)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")

        shouldNotValidateIllegalUrl("www." + tooLongHostname(248) + ".com")
    }

    @Test
    internal fun shouldCollectMultipleValidationErrors() {
        assertThrows<UrlValidationException> { "http://www.exam!ple.com:-1".toUrl() }
            .also { Assertions.assertThat(it.errors.size).isEqualTo(2) }
    }
}