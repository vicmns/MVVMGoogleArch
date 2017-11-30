package com.lonelystudios.palantir.di

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lonelystudios.palantir.BuildConfig
import com.lonelystudios.palantir.di.scope.PerApp
import com.lonelystudios.palantir.net.LogoService
import com.lonelystudios.palantir.net.NewsService
import com.lonelystudios.palantir.net.utils.Constants
import com.lonelystudios.palantir.net.utils.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



/**
 * Created by vicmns on 8/3/17.
 */

@Module
class NetModule {
    @Provides
    @PerApp
    fun provideOkHttpCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    @PerApp
    fun providesGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }

    @Provides
    @PerApp
    fun provideOkHttpClient(cache: Cache): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.cache(cache)
        client.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " + BuildConfig.NEWS_API_API_KEY)
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        client.addInterceptor(interceptor)
        return client.build()
    }

    @Provides
    @PerApp
    fun providesNewsService(okHttpClient: OkHttpClient): NewsService {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://newsapi.org/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(NewsService::class.java)
    }

    @Provides
    @PerApp
    fun providesLogoService(okHttpClient: OkHttpClient): LogoService {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://icons.better-idea.org")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(LogoService::class.java)
    }

}
