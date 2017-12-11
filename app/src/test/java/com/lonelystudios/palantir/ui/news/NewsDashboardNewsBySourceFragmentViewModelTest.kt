package com.lonelystudios.palantir.ui.news

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.lonelystudios.palantir.repository.ArticlesRepository
import com.lonelystudios.palantir.util.TestUtil
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.sources.Articles
import com.lonelystudios.palantir.vo.sources.Source
import com.nhaarman.mockito_kotlin.capture
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

/**
 * Created by vicmns on 12/10/17.
 */
@RunWith(MockitoJUnitRunner::class)
class NewsDashboardNewsBySourceFragmentViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    lateinit var articlesRepository: ArticlesRepository
    lateinit var viewModel: NewsDashboardNewsBySourceFragmentViewModel

    @Captor
    lateinit var sourceCaptor: ArgumentCaptor<List<Source>>

    @Before
    fun setup() {
        articlesRepository = Mockito.mock(ArticlesRepository::class.java)
        viewModel = NewsDashboardNewsBySourceFragmentViewModel(articlesRepository)
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testNull() {
        MatcherAssert.assertThat(viewModel.articlesLiveData, CoreMatchers.notNullValue())
        verify(articlesRepository, Mockito.never()).getArticlesBySource(Source())
    }

    @Test
    fun dontFetchWithoutObservers() {
        val source = TestUtil.createSource("foo")
        viewModel.getAllArticlesFromSelectedSources(source)
        verify(articlesRepository, Mockito.never()).getArticlesBySource(source)
    }

    @Test
    fun fetchWhenObserved() {
        val source = TestUtil.createSource("foo")
        viewModel.getAllArticlesFromSelectedSources(source)
        val sources = Collections.singletonList(source)
        viewModel.articlesLiveData.observeForever(Mockito.mock(Observer::class.java) as Observer<Resource<Articles>>)
        verify(articlesRepository, Mockito.times(1)).getArticlesBySources(capture(sourceCaptor))
        MatcherAssert.assertThat(sourceCaptor.value, CoreMatchers.`is`(sources))
    }

    @Test
    fun changeWhileObserved() {
        viewModel.articlesLiveData.observeForever(Mockito.mock(Observer::class.java) as Observer<Resource<Articles>>)
        val source1 = TestUtil.createSource("foo")
        val sources1 = Collections.singletonList(source1)
        val source2 = TestUtil.createSource("bar")
        val sources2 = Collections.singletonList(source2)

        viewModel.getAllArticlesFromSelectedSources(source1)
        viewModel.getAllArticlesFromSelectedSources(source2)

        verify(articlesRepository, Mockito.times(2)).getArticlesBySources(capture(sourceCaptor))
        MatcherAssert.assertThat(sourceCaptor.allValues, CoreMatchers.`is`(Arrays.asList(sources1, sources2)))
    }

    @Test
    fun articles() {
        val source = TestUtil.createSource("foo")
        val observer: Observer<Resource<Articles>> = mock(Observer::class.java) as Observer<Resource<Articles>>
        viewModel.articlesLiveData.observeForever(observer)
        verifyNoMoreInteractions(observer)
        verifyNoMoreInteractions(articlesRepository)
        viewModel.getAllArticlesFromSelectedSources(source)
        verify(articlesRepository).getArticlesBySources(Collections.singletonList(source))
    }
}