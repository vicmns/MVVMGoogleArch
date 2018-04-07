package com.lonelystudios.palantir.ui.news

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.lonelystudios.palantir.net.LogoService
import com.lonelystudios.palantir.net.NewsService
import com.lonelystudios.palantir.net.utils.LiveDataCallAdapterFactory
import com.lonelystudios.palantir.repository.ArticlesRepository
import com.lonelystudios.palantir.repository.ArticlesRepositoryFactory
import com.lonelystudios.palantir.repository.SourcesRepository
import com.lonelystudios.palantir.repository.SourcesRepositoryFactory
import com.lonelystudios.palantir.utils.AbsentLiveData
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.sources.Articles
import com.lonelystudios.palantir.vo.sources.Source
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

/**
 * Created by vicmns on 11/20/17.
 */
class AllNewsFragmentViewModel @Inject constructor(articlesRepositoryFactory: ArticlesRepositoryFactory,
                                                   okHttpClient: OkHttpClient,
                                                   sourcesRepositoryFactory: SourcesRepositoryFactory) : ViewModel() {

    val articlesLiveData: LiveData<Resource<Articles>>
    val sourcesLiveData: LiveData<List<Source>>
    var userSources: List<Source> = ArrayList()
    private val triggerGetArticles: MutableLiveData<List<Source>> = MutableLiveData()
    private val triggerGetSources: MutableLiveData<Boolean> = MutableLiveData()
    private var articlesRepository: ArticlesRepository
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
        articlesRepository = articlesRepositoryFactory.create(newsService)
        sourcesRepository = sourcesRepositoryFactory.create(newsService, logoService)
        articlesLiveData = Transformations.switchMap(triggerGetArticles) {
            if (it != null && it.isNotEmpty()) articlesRepository.getArticlesBySources(it)
            else AbsentLiveData.create()
        }

        sourcesLiveData = Transformations.switchMap(triggerGetSources) {
            if(it) sourcesRepository.getSelectedSources()
            else AbsentLiveData.create()
        }
    }

    fun getAllArticlesFromSelectedSources(sources: List<Source>) {
        triggerGetArticles.value = sources
    }

    fun getSelectedSources() {
        triggerGetSources.value = true
    }
}