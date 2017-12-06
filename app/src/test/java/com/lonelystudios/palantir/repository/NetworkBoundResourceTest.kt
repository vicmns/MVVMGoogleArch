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

package com.lonelystudios.palantir.repository


import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.lonelystudios.palantir.net.utils.ApiResponse
import com.lonelystudios.palantir.repository.util.NetworkBoundResource
import com.lonelystudios.palantir.util.ApiUtil
import com.lonelystudios.palantir.util.CountingAppExecutors
import com.lonelystudios.palantir.util.InstantAppExecutors
import com.lonelystudios.palantir.vo.Resource
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mockito
import org.mockito.Mockito.*
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Function

@RunWith(Parameterized::class)
class NetworkBoundResourceTest(private val useRealExecutors: Boolean) {
    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    private var saveCallResult: Function<Foo, Void?>? = null

    private lateinit var shouldFetch: Function<Foo?, Boolean>

    private var createCall: Function<Void?, LiveData<ApiResponse<Foo>>>? = null

    private val dbData = MutableLiveData<Foo>()

    private var networkBoundResource: NetworkBoundResource<Foo, Foo>? = null

    private val fetchedOnce = AtomicBoolean(false)
    private var countingAppExecutors: CountingAppExecutors? = null

    init {
        if (useRealExecutors) {
            countingAppExecutors = CountingAppExecutors()
        }
    }

    @Before
    fun init() {
        val appExecutors = if (useRealExecutors)
            countingAppExecutors!!.appExecutors
        else
            InstantAppExecutors()
        networkBoundResource = object : NetworkBoundResource<Foo, Foo>(appExecutors) {
            override fun saveCallResult(item: Foo) {
                saveCallResult!!.apply(item)
            }

            override fun shouldFetch(data: Foo?): Boolean {
                // since test methods don't handle repetitive fetching, call it only once
                return shouldFetch.apply(data) && fetchedOnce.compareAndSet(false, true)
            }

            override fun loadFromDb(): LiveData<Foo> {
                return dbData
            }

            override fun createCall(): LiveData<ApiResponse<Foo>> {
                return createCall!!.apply(null)
            }
        }
    }

    private fun drain() {
        if (!useRealExecutors) {
            return
        }
        try {
            countingAppExecutors!!.drainTasks(1, TimeUnit.SECONDS)
        } catch (t: Throwable) {
            throw AssertionError(t)
        }

    }

    @Test
    fun basicFromNetwork() {
        val saved = AtomicReference<Foo>()
        shouldFetch = Function { Objects.isNull(it) }
        val fetchedDbValue = Foo(1)
        saveCallResult = Function {
            saved.set(it)
            dbData.value = fetchedDbValue
            null
        }
        val networkResult = Foo(1)
        createCall = Function { ApiUtil.createCall(Response.success(networkResult)) }

        val observer = Mockito.mock(Observer::class.java)
        networkBoundResource!!.asLiveData().observeForever(observer as Observer<Resource<Foo>>)
        drain()
        verify(observer).onChanged(Resource.loading(null))
        reset<Observer<Resource<Foo>>>(observer)
        dbData.value = null
        drain()
        assertThat(saved.get(), `is`(networkResult))
        verify<Observer<Resource<Foo>>>(observer).onChanged(Resource.success(fetchedDbValue))
    }

    @Test
    fun failureFromNetwork() {
        val saved = AtomicBoolean(false)
        shouldFetch = Function { Objects.isNull(it) }
        saveCallResult = Function {
            saved.set(true)
            null
        }
        val body = ResponseBody.create(MediaType.parse("text/html"), "error")
        createCall = Function { ApiUtil.createCall(Response.error<Foo>(500, body)) }

        val observer = Mockito.mock(Observer::class.java)
        networkBoundResource!!.asLiveData().observeForever(observer as Observer<Resource<Foo>>)
        drain()
        verify<Observer<Resource<Foo>>>(observer).onChanged(Resource.loading(null))
        reset<Observer<Resource<Foo>>>(observer)
        dbData.value = null
        drain()
        assertThat(saved.get(), `is`(false))
        verify<Observer<Resource<Foo>>>(observer).onChanged(Resource.error("error", null))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun dbSuccessWithoutNetwork() {
        val saved = AtomicBoolean(false)
        shouldFetch = Function { Objects.isNull(it) }
        saveCallResult = Function {
            saved.set(true)
            null
        }

        val observer = Mockito.mock(Observer::class.java)
        networkBoundResource!!.asLiveData().observeForever(observer as Observer<Resource<Foo>>)
        drain()
        verify<Observer<Resource<Foo>>>(observer).onChanged(Resource.loading(null))
        reset<Observer<Resource<Foo>>>(observer)
        val dbFoo = Foo(1)
        dbData.value = dbFoo
        drain()
        verify<Observer<Resource<Foo>>>(observer).onChanged(Resource.success(dbFoo))
        assertThat(saved.get(), `is`(false))
        val dbFoo2 = Foo(2)
        dbData.value = dbFoo2
        drain()
        verify<Observer<Resource<Foo>>>(observer).onChanged(Resource.success(dbFoo2))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun dbSuccessWithFetchFailure() {
        val dbValue = Foo(1)
        val saved = AtomicBoolean(false)
        shouldFetch = Function { foo -> foo === dbValue }
        saveCallResult = Function { foo ->
            saved.set(true)
            null
        }
        val body = ResponseBody.create(MediaType.parse("text/html"), "error")
        val apiResponseLiveData: MutableLiveData<ApiResponse<Foo>> = MutableLiveData()
        createCall = Function{ apiResponseLiveData }

        val observer = Mockito.mock(Observer::class.java)
        networkBoundResource!!.asLiveData().observeForever(observer as Observer<Resource<Foo>>)
        drain()
        verify<Observer<Resource<Foo>>>(observer).onChanged(Resource.loading(null))
        reset<Observer<Resource<Foo>>>(observer)

        dbData.value = dbValue
        drain()
        verify<Observer<Resource<Foo>>>(observer).onChanged(Resource.loading(dbValue))

        apiResponseLiveData.value = ApiResponse(Response.error<Foo>(400, body))
        drain()
        assertThat(saved.get(), `is`(false))
        verify<Observer<Resource<Foo>>>(observer).onChanged(Resource.error("error", dbValue))

        val dbValue2 = Foo(2)
        dbData.value = dbValue2
        drain()
        verify<Observer<Resource<Foo>>>(observer).onChanged(Resource.error("error", dbValue2))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun dbSuccessWithReFetchSuccess() {
        val dbValue = Foo(1)
        val dbValue2 = Foo(2)
        val saved = AtomicReference<Foo>()
        shouldFetch = Function { foo -> foo === dbValue }
        saveCallResult = Function { foo ->
            saved.set(foo)
            dbData.value = dbValue2
            null
        }
        val apiResponseLiveData: MutableLiveData<ApiResponse<Foo>> = MutableLiveData()
        createCall = Function { apiResponseLiveData }

        val observer = Mockito.mock(Observer::class.java)
        networkBoundResource!!.asLiveData().observeForever(observer as Observer<Resource<Foo>>)
        drain()
        verify<Observer<Resource<Foo>>>(observer).onChanged(Resource.loading(null))
        reset<Observer<Resource<Foo>>>(observer)

        dbData.value = dbValue
        drain()
        val networkResult = Foo(1)
        verify<Observer<Resource<Foo>>>(observer).onChanged(Resource.loading(dbValue))
        apiResponseLiveData.value = ApiResponse(Response.success(networkResult))
        drain()
        assertThat(saved.get(), `is`(networkResult))
        verify<Observer<Resource<Foo>>>(observer).onChanged(Resource.success(dbValue2))
        verifyNoMoreInteractions(observer)
    }

    internal class Foo(var value: Int)

    companion object {

        @Parameterized.Parameters
        @JvmStatic
        fun param(): List<Boolean> {
            return Arrays.asList(true, false)
        }
    }
}