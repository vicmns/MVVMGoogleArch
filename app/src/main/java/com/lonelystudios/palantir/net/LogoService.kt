package com.lonelystudios.palantir.net

import com.lonelystudios.palantir.net.utils.ApiResponse
import com.lonelystudios.palantir.net.utils.CancelableRetrofitLiveDataCall
import com.lonelystudios.palantir.vo.logo.SourceLogoInfo
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by vicmns on 10/25/17.
 */
interface LogoService {
    @GET("allicons.json")
    fun getSourceLogo(@Query("url") url: String): CancelableRetrofitLiveDataCall<ApiResponse<SourceLogoInfo>>
}