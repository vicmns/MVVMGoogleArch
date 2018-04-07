package com.lonelystudios.palantir.repository

import android.arch.lifecycle.LiveData
import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.lonelystudios.palantir.dao.ArticlesDao
import com.lonelystudios.palantir.net.NewsService
import com.lonelystudios.palantir.net.utils.ApiResponse
import com.lonelystudios.palantir.net.utils.CancelableRetrofitLiveDataCall
import com.lonelystudios.palantir.repository.util.CancelableNetworkBoundResource
import com.lonelystudios.palantir.repository.util.CancelableNoDatabaseNetworkBoundResource
import com.lonelystudios.palantir.utils.AppExecutors
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.sources.Article
import com.lonelystudios.palantir.vo.sources.Articles
import com.lonelystudios.palantir.vo.sources.Source
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Created by vicmns on 11/20/17.
 */
@AutoFactory
class ArticlesRepository constructor(private val newsService: NewsService,
                                             @Provided private val articlesDao: ArticlesDao,
                                             @Provided private val appExecutors: AppExecutors) {

    lateinit var articlesBySourceResource: CancelableNetworkBoundResource<List<Article>, Articles>
    lateinit var allArticlesResource: CancelableNoDatabaseNetworkBoundResource<Articles>

    fun getArticlesBySource(source: Source): LiveData<Resource<List<Article>>> {
        articlesBySourceResource = object : CancelableNetworkBoundResource<List<Article>, Articles>(appExecutors) {
            override fun saveCallResult(item: Articles) {
                item.articles?.let { articlesDao.update(*it.toTypedArray()) }
            }

            override fun shouldFetch(data: List<Article>?): Boolean = true

            override fun loadFromDb(): LiveData<List<Article>> =
                    articlesDao.getArticlesBySource(source.id)

            override fun createCall(): CancelableRetrofitLiveDataCall<ApiResponse<Articles>> =
                    newsService.getArticlesBySource(source.id)
        }

        return articlesBySourceResource.asLiveData()
    }

    fun getArticlesBySources(sources: List<Source>): LiveData<Resource<Articles>> {
        allArticlesResource = object : CancelableNoDatabaseNetworkBoundResource<Articles>(appExecutors) {
            override fun saveCallResult(item: Articles) {
                item.articles?.let { articlesDao.insert(*it.toTypedArray()) }
            }

            override fun createCall(): CancelableRetrofitLiveDataCall<ApiResponse<Articles>> {
                val sourcesQueryString = StringBuilder()
                for (source in sources) {
                    sourcesQueryString.append(source.id)
                    sourcesQueryString.append(",")
                }
                sourcesQueryString.deleteCharAt(sourcesQueryString.lastIndexOf(","))
                return newsService.getArticlesBySource(sourcesQueryString.toString())
            }

        }

        return allArticlesResource.asLiveData()
    }

}