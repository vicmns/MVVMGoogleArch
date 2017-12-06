/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lonelystudios.palantir.api

import com.lonelystudios.palantir.net.utils.ApiResponse

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat

@RunWith(JUnit4::class)
class ApiResponseTest {
    @Test
    fun exception() {
        val exception = Exception("foo")
        val apiResponse = ApiResponse<String>(exception)
        assertThat<String>(apiResponse.body, nullValue())
        assertThat(apiResponse.code, `is`(500))
        assertThat(apiResponse.errorMessage, `is`("foo"))
    }

    @Test
    fun success() {
        val apiResponse = ApiResponse(Response.success("foo"))
        assertThat(apiResponse.errorMessage, nullValue())
        assertThat(apiResponse.code, `is`(200))
        assertThat<String>(apiResponse.body, `is`("foo"))
    }

    @Test
    fun error() {
        val response = ApiResponse(Response.error<String>(400,
                ResponseBody.create(MediaType.parse("application/txt"), "blah")))
        assertThat(response.code, `is`(400))
        assertThat(response.errorMessage, `is`("blah"))
    }
}