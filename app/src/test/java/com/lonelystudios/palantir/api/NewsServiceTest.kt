package com.lonelystudios.palantir.api

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.lonelystudios.palantir.net.NewsService
import com.lonelystudios.palantir.net.utils.LiveDataCallAdapterFactory
import com.lonelystudios.palantir.util.LiveDataTestUtil.getValue
import com.lonelystudios.palantir.vo.sources.Articles
import com.lonelystudios.palantir.vo.sources.Source
import com.lonelystudios.palantir.vo.sources.Sources
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okio.Okio
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.nio.charset.StandardCharsets

/**
 * Created by vicmns on 12/4/17.
 */
@RunWith(JUnit4::class)
class NewsServiceTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: NewsService
    private lateinit var mockWebServer: MockWebServer

    @Before
    @Throws(IOException::class)
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(NewsService::class.java)
    }

    @After
    @Throws(IOException::class)
    fun stopService() = mockWebServer.shutdown()

    @Test
    fun getAllSource() {
        enqueueResponse("all-sources.json")
        val sources: Sources? = getValue(service.getAllSources()).body

        val request: RecordedRequest = mockWebServer.takeRequest()
        assertThat(request.path, `is`("/sources"))

        assertThat(sources, notNullValue())
        assertThat(sources?.status, `is`("ok"))
        assertThat(sources?.sources, notNullValue())
        assertThat(sources?.sources?.isNotEmpty(), `is`(true) )
        assertThat(sources?.sources?.count(), `is`(134))

    }

    @Test
    fun getSourcesByCategory() {
        enqueueResponse("technology-sources.json")
        val sources: Sources? = getValue(service.getAllSourcesByCategory(Source.TECHNOLOGY)).body

        val request: RecordedRequest = mockWebServer.takeRequest()
        assertThat(request.path, `is`("/sources?category=technology"))

        assertThat(sources, notNullValue())
        assertThat(sources?.status, `is`("ok"))
        assertThat(sources?.sources, notNullValue())
        assertThat(sources?.sources?.isNotEmpty(), `is`(true) )
        assertThat(sources?.sources?.count(), `is`(14))
    }

    @Test
    fun getArticlesBySource() {
        enqueueResponse("articles-by-source.json")
        val articles: Articles? = getValue(service.getArticlesBySource("bbc-news")).body

        val request: RecordedRequest = mockWebServer.takeRequest()
        assertThat(request.path, `is`("/top-headlines?sources=bbc-news"))

        assertThat(articles, notNullValue())
        assertThat(articles?.status, `is`("ok"))
        assertThat(articles?.articles, notNullValue())
        assertThat(articles?.articles?.isNotEmpty(), `is`(true))
        assertThat(articles?.articles?.count(), `is`(10))
    }

    @Throws(IOException::class)
    private fun enqueueResponse(fileName: String) {
        enqueueResponse(fileName, emptyMap())
    }

    @Throws(IOException::class)
    private fun enqueueResponse(fileName: String, headers: Map<String, String>) {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/" + fileName)
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(mockResponse
                .setBody(source.readString(StandardCharsets.UTF_8)))
    }
}