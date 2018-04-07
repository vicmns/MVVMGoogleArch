package com.lonelystudios.palantir.ui.news

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.lonelystudios.palantir.net.NewsService
import com.lonelystudios.palantir.net.utils.LiveDataCallAdapterFactory
import com.lonelystudios.palantir.repository.ArticlesRepository
import com.lonelystudios.palantir.repository.ArticlesRepositoryFactory
import com.lonelystudios.palantir.utils.AbsentLiveData
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.sources.Articles
import com.lonelystudios.palantir.vo.sources.Source
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Inject

/**
 * Created by vicmns on 12/1/17.
 */
class NewsDashboardNewsBySourceFragmentViewModel @Inject constructor(articlesRepositoryFactory: ArticlesRepositoryFactory,
                                                                     okHttpClient: OkHttpClient): ViewModel() {
    val articlesLiveData: LiveData<Resource<Articles>>
    private val triggerGetArticles: MutableLiveData<List<Source>> = MutableLiveData()
    private var articlesRepository: ArticlesRepository

    init {
        /**
         * Dynamically create the NewsService to use on repository
         */
        val newsService = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://newsapi.org/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(NewsService::class.java)
        articlesRepository = articlesRepositoryFactory.create(newsService)
        articlesLiveData = Transformations.switchMap(triggerGetArticles) {
            if (it != null && it.isNotEmpty()) articlesRepository.getArticlesBySources(it)
            else AbsentLiveData.create()
        }
    }

    fun getAllArticlesFromSelectedSources(source: Source) {
        triggerGetArticles.value = Collections.singletonList(source)
    }
}