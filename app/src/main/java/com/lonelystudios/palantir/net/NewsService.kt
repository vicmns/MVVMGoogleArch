package com.lonelystudios.palantir.net

/**
 * Created by vicmns on 10/19/17.
 */

import com.lonelystudios.palantir.net.utils.ApiResponse
import com.lonelystudios.palantir.net.utils.CancelableRetrofitLiveDataCall
import com.lonelystudios.palantir.vo.SortBy
import com.lonelystudios.palantir.vo.sources.Articles
import com.lonelystudios.palantir.vo.sources.Sources
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {
    @GET("sources")
    fun getAllSources(): CancelableRetrofitLiveDataCall<ApiResponse<Sources>>

    @GET("sources")
    fun getAllSourcesByCategory(@Query("category") category: String): CancelableRetrofitLiveDataCall<ApiResponse<Sources>>

    @GET("sources")
    fun getAllSourcesByLanguage(@Query("language") language: String): CancelableRetrofitLiveDataCall<ApiResponse<Sources>>

    @GET("sources")
    fun getAllSourcesByCountry(@Query("country") country: String): CancelableRetrofitLiveDataCall<ApiResponse<Sources>>

    @GET("sources")
    fun getAllSourcesByAllFilters(@Query("category") category: String,
                                  @Query("language") language: String,
                                  @Query("country") country: String): CancelableRetrofitLiveDataCall<ApiResponse<Sources>>

    @GET("top-headlines")
    fun getArticlesBySource(@Query("sources") sources: String): CancelableRetrofitLiveDataCall<ApiResponse<Articles>>

    @GET("top-headlines")
    fun getArticlesBySourceSortedBy(@Query("sources") source: String,
                                    @Query("sortBy") @SortBy.Companion.Sort sortBy: String):
            CancelableRetrofitLiveDataCall<ApiResponse<Articles>>
}