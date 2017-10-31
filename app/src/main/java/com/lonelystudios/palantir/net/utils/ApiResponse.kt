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

package com.lonelystudios.palantir.net.utils

import java.io.IOException

import retrofit2.Response
import timber.log.Timber

/**
 * Common class used by API responses.
 * @param <T>
</T> */
class ApiResponse<T> {
    val code: Int
    val body: T?
    val errorMessage: String

    val isCancelled: Boolean

    val isSuccessful: Boolean
        get() = code in 200..299

    constructor() {
        code = -1
        body = null
        errorMessage = ""
        isCancelled = false
    }

    constructor(errorMessage: String) {
        code = 500
        body = null
        this.errorMessage = errorMessage
        isCancelled = false
    }

    constructor(error: Throwable) {
        code = 500
        body = null
        errorMessage = error.message.toString()
        isCancelled = false
    }

    constructor(isCancelled: Boolean) {
        code = -1
        body = null
        errorMessage = "Cancelled response"
        this.isCancelled = isCancelled
    }

    constructor(response: Response<T>) {
        code = response.code()
        isCancelled = false
        if (response.isSuccessful) {
            body = response.body()
            errorMessage = ""
        } else {
            var message = ""
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody().string()
                } catch (ignored: IOException) {
                    Timber.e(ignored, "error while parsing response")
                }

            }
            if (message.trim { it <= ' ' }.isEmpty()) {
                message = response.message()
            }
            errorMessage = message
            body = null
        }
    }
}
