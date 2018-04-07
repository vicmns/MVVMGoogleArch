package com.lonelystudios.palantir.ui.sources

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.lonelystudios.palantir.net.LogoService
import com.lonelystudios.palantir.net.NewsService
import com.lonelystudios.palantir.net.utils.LiveDataCallAdapterFactory
import com.lonelystudios.palantir.repository.SourcesRepository
import com.lonelystudios.palantir.repository.SourcesRepositoryFactory
import com.lonelystudios.palantir.utils.AbsentLiveData
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.sources.Source
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

/**
 * Created by vicmns on 10/28/17.
 */
class SourceItemViewModel @Inject constructor(sourcesRepositoryFactory: SourcesRepositoryFactory,
                                              okHttpClient: OkHttpClient) : ViewModel() {

    val sourcesLiveData: LiveData<Resource<Source>>
    val triggerGetLogoInfo: MutableLiveData<Source> = MutableLiveData()
    private var sourcesRepository: SourcesRepository

    init {
        /**
         * Dynamically create the NewsService and LogoService to use on repository
         */
        val newsService = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://newsapi.org/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(NewsService::class.java)
        val logoService = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://icons.better-idea.org")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(LogoService::class.java)
        sourcesRepository = sourcesRepositoryFactory.create(newsService, logoService)
        sourcesLiveData = Transformations.switchMap(triggerGetLogoInfo) {
            if (it.urlToLogo.isNullOrEmpty()) sourcesRepository.getSourceLogo(it)
            else AbsentLiveData.create()
        }
    }

    fun getLogoInfo(source: Source) {
        triggerGetLogoInfo.value = source
    }
}