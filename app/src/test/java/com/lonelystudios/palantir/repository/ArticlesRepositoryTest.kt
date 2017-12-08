package com.lonelystudios.palantir.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.lonelystudios.palantir.dao.ArticlesDao
import com.lonelystudios.palantir.db.PalantirDataBase
import com.lonelystudios.palantir.net.NewsService
import com.lonelystudios.palantir.net.utils.ApiResponse
import com.lonelystudios.palantir.net.utils.CancelableRetrofitLiveDataCall
import com.lonelystudios.palantir.util.ApiUtil.successCancelableCall
import com.lonelystudios.palantir.util.InstantAppExecutors
import com.lonelystudios.palantir.util.TestUtil
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.sources.Article
import com.lonelystudios.palantir.vo.sources.Articles
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import java.util.*

/**
 * Created by vicmns on 12/5/17.
 */
@RunWith(JUnit4::class)
class ArticlesRepositoryTest {
    private lateinit var repository: ArticlesRepository
    private lateinit var dao: ArticlesDao
    private lateinit var service: NewsService
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        dao = mock(ArticlesDao::class.java)
        service = mock(NewsService::class.java)
        val db = mock(PalantirDataBase::class.java)
        whenever(db.articlesDao()).thenReturn(dao)
        repository = ArticlesRepository(dao, service, InstantAppExecutors())
    }

    @Test
    fun loadArticlesBySource() {
        val dbData: MutableLiveData<List<Article>> = MutableLiveData()
        whenever(dao.getArticlesBySource("foo")).thenReturn(dbData)

        val articleList: List<Article> = Collections.singletonList(TestUtil.createArticle("2017-12-04T22:23:00Z", "John Smith",
                "http://foo.bar/image.jpg", "Lorem Ipsum",
                "Foo bar", "http://foo.bar/",
                TestUtil.createArticleSource("foo", "bar")))

        val articles: Articles = TestUtil.createArticles("ok", articleList)

        val call: CancelableRetrofitLiveDataCall<ApiResponse<Articles>> = successCancelableCall(articles)
        whenever(service.getArticlesBySource("foo")).thenReturn(call)

        val data: LiveData<Resource<List<Article>>> = repository.getArticlesBySource(TestUtil.createSource("foo"))
        verify(dao).getArticlesBySource("foo")
        verifyNoMoreInteractions(service)

        val observer: Observer<*>? = mock(Observer::class.java)
        data.observeForever(observer as Observer<Resource<List<Article>>>)
        verifyNoMoreInteractions(service)
        verify(observer).onChanged(Resource.loading(null))
        val updateDbData = MutableLiveData<List<Article>>()
        whenever(dao.getArticlesBySource("foo")).thenReturn(updateDbData)

        dbData.postValue(null)
        verify(service).getArticlesBySource("foo")
        verify(dao).update(*articleList.toTypedArray())

        updateDbData.postValue(articleList)
        verify(observer).onChanged(Resource.success(articleList))
    }
}