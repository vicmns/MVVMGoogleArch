package com.lonelystudios.palantir.ui.news

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.lonelystudios.palantir.repository.ArticlesRepository
import com.lonelystudios.palantir.repository.SourcesRepository
import com.lonelystudios.palantir.util.TestUtil
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.sources.Articles
import com.lonelystudios.palantir.vo.sources.Source
import com.nhaarman.mockito_kotlin.capture
import com.nhaarman.mockito_kotlin.verify
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import java.util.*
import org.mockito.Captor
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


/**
 * Created by vicmns on 12/10/17.
 */
@RunWith(MockitoJUnitRunner::class)
class AllNewsFragmentViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    lateinit var articlesRepository: ArticlesRepository
    lateinit var sourcesRepository: SourcesRepository
    lateinit var viewModel: AllNewsFragmentViewModel

    @Captor
    lateinit var sourcesCaptor: ArgumentCaptor<List<Source>>

    @Before
    fun setup() {
        articlesRepository = mock(ArticlesRepository::class.java)
        sourcesRepository = mock(SourcesRepository::class.java)
        viewModel = AllNewsFragmentViewModel(articlesRepository, sourcesRepository)
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testNull() {
        assertThat(viewModel.sourcesLiveData, notNullValue())
        assertThat(viewModel.articlesLiveData, notNullValue())
        assertThat(viewModel.userSources, notNullValue())
        verify(articlesRepository, never()).getArticlesBySource(Source())
        verify(sourcesRepository, never()).getSelectedSources()
    }

    @Test
    fun dontFetchWithoutObservers() {
        viewModel.getSelectedSources()
        verify(sourcesRepository, never()).getSelectedSources()

        val source = TestUtil.createSource("foo")
        val sources = Collections.singletonList(source)
        viewModel.getAllArticlesFromSelectedSources(sources)
        verify(articlesRepository, never()).getArticlesBySources(sources)
    }

    @Test
    fun fetchSourcesWhenObserved() {
        viewModel.getSelectedSources()
        viewModel.sourcesLiveData.observeForever(mock(Observer::class.java) as Observer<List<Source>>)
        verify(sourcesRepository, times(1)).getSelectedSources()
    }

    @Test
    fun fetchArticlesWhenObserved() {
        val source = TestUtil.createSource("foo")
        val sources = Collections.singletonList(source)
        viewModel.getAllArticlesFromSelectedSources(sources)
        viewModel.articlesLiveData.observeForever(mock(Observer::class.java) as Observer<Resource<Articles>>)
        verify(articlesRepository, times(1)).getArticlesBySources(capture(sourcesCaptor))
        assertThat(sourcesCaptor.value, `is`(sources))
    }

    @Test
    fun changeSourcesWhileObserved() {
        viewModel.sourcesLiveData.observeForever(mock(Observer::class.java) as Observer<List<Source>>)
        viewModel.getSelectedSources()
        viewModel.getSelectedSources()
        verify(sourcesRepository, times(2)).getSelectedSources()
    }

    @Test
    fun changeArticlesWhileObserved() {
        viewModel.articlesLiveData.observeForever(mock(Observer::class.java) as Observer<Resource<Articles>>)
        val source1 = TestUtil.createSource("foo")
        val sources1 = Collections.singletonList(source1)
        val source2 = TestUtil.createSource("bar")
        val sources2 = Collections.singletonList(source2)

        viewModel.getAllArticlesFromSelectedSources(sources1)
        viewModel.getAllArticlesFromSelectedSources(sources2)

        verify(articlesRepository, times(2)).getArticlesBySources(capture(sourcesCaptor))
        assertThat(sourcesCaptor.allValues, `is`(Arrays.asList(sources1, sources2)))
    }
}