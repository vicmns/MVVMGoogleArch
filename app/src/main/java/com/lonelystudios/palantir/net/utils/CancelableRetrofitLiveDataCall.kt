package com.lonelystudios.palantir.net.utils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

import retrofit2.Call

/**
 * Created by vicmns on 8/11/17.
 */

open class CancelableRetrofitLiveDataCall<T>(private val retrofitCall: Call<*>) : MutableLiveData<T>() {

    fun cancel() {
        retrofitCall.cancel()
    }
}
