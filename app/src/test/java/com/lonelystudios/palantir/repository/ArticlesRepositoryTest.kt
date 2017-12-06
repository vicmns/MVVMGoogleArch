package com.lonelystudios.palantir.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import com.lonelystudios.palantir.dao.ArticlesDao
import com.lonelystudios.palantir.db.PalantirDataBase
import com.lonelystudios.palantir.net.NewsService
import com.lonelystudios.palantir.net.utils.ApiResponse
import com.lonelystudios.palantir.net.utils.CancelableRetrofitLiveDataCall
import com.lonelystudios.palantir.util.ApiUtil.successCancelableCall
import com.lonelystudios.palantir.util.InstantAppExecutors
import com.lonelystudios.palantir.util.TestUtil
import com.lonelystudios.palantir.vo.sources.Articles
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
        val dbData: MutableLiveData<Articles> = MutableLiveData()
        whenever(dao.getArticlesBySource("foo")).thenReturn(dbData)

        val articles: Articles = TestUtil.createArticles("ok",
                Collections.singletonList(TestUtil.createArticle("2017-12-04T22:23:00Z", "John Smith",
                        "http://foo.bar/image.jpg", "Lorem Ipsum",
                        "Foo bar", "http://foo.bar/",
                        TestUtil.createArticleSource("foo", "bar"))))

        val call: CancelableRetrofitLiveDataCall<ApiResponse<Articles>> = successCancelableCall(articles)
        whenever(service.getArticlesBySource("foo")).thenReturn(call)
    }
}