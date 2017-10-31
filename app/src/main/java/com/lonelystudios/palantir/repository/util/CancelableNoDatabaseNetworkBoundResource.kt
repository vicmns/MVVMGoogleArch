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

package com.lonelystudios.palantir.repository.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread

import com.lonelystudios.palantir.net.utils.ApiResponse
import com.lonelystudios.palantir.net.utils.CancelableRetrofitLiveDataCall
import com.lonelystudios.palantir.utils.AppExecutors
import com.lonelystudios.palantir.vo.Resource

/**
 * A generic class that can provide a resource backed by the network.
 *
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 *
 * @param <Type>
</Type> */
abstract class CancelableNoDatabaseNetworkBoundResource<Type> @MainThread
protected constructor(appExecutors: AppExecutors) {
    private val result = MediatorLiveData<Resource<Type>>()
    private val apiResponse: CancelableRetrofitLiveDataCall<ApiResponse<Type>>?

    init {
        result.setValue(Resource.loading(null))
        apiResponse = createCall()
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)

            if (response != null) {
                when {
                    response.isSuccessful -> {
                        appExecutors.diskIO().execute { saveCallResult(processResponse(response)!!) }
                        result.setValue(Resource.success(response.body))
                    }
                    response.isCancelled -> result.setValue(Resource.cancel())
                    else -> {
                        onFetchFailed()
                        result.setValue(Resource.error(response.errorMessage, null, response.code))
                    }
                }
            }
        }
    }

    protected fun onFetchFailed() {

    }

    fun cancelServiceCall() {
        if (apiResponse == null) return

        apiResponse.cancel()
    }

    fun asLiveData(): LiveData<Resource<Type>> {
        return result
    }

    @WorkerThread
    protected fun processResponse(response: ApiResponse<Type>): Type? {
        return response.body
    }

    @WorkerThread
    protected abstract fun saveCallResult(item: Type)

    @MainThread
    protected abstract fun createCall(): CancelableRetrofitLiveDataCall<ApiResponse<Type>>
}
