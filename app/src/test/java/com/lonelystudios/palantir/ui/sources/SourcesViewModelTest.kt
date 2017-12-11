package com.lonelystudios.palantir.ui.sources

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.lonelystudios.palantir.repository.SourcesRepository
import com.lonelystudios.palantir.util.TestUtil
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.sources.Source
import com.nhaarman.mockito_kotlin.capture
import com.nhaarman.mockito_kotlin.verify
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by vicmns on 12/11/17.
 */
@RunWith(MockitoJUnitRunner::class)
class SourcesViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    lateinit var repository: SourcesRepository
    lateinit var viewModel: SourcesViewModel

    @Captor
    lateinit var sourcesCaptor: ArgumentCaptor<Source>

    @Before
    fun setup() {
        repository = mock(SourcesRepository::class.java)
        viewModel = SourcesViewModel(repository)
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testNull() {
        MatcherAssert.assertThat(viewModel.sourcesLiveData, CoreMatchers.notNullValue())
        verify(repository, never()).getAllSources()
    }

    @Test
    fun dontFetchWithoutObservers() {
        viewModel.getAllSources()
        verify(repository, never()).getAllSources()
    }

    @Test
    fun fetchWhenObserved() {
        viewModel.getAllSources()
        viewModel.sourcesLiveData.observeForever(mock(Observer::class.java) as Observer<Resource<List<Source>>>)
        verify(repository, times(1)).getAllSources()
    }

    @Test
    fun update() {
        val source = TestUtil.createSource("foo")
        viewModel.updateSelectedSource(source)
        verify(repository, times(1)).updateSource(capture(sourcesCaptor))
        assertThat(sourcesCaptor.value, `is`(source))
    }

}