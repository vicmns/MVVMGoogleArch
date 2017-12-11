package com.lonelystudios.palantir.ui.news

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.lonelystudios.palantir.repository.SourcesRepository
import com.lonelystudios.palantir.vo.sources.Source
import com.nhaarman.mockito_kotlin.verify
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by vicmns on 12/10/17.
 */
@RunWith(MockitoJUnitRunner::class)
class NewsDashboardFragmentViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    lateinit var sourcesRepository: SourcesRepository
    lateinit var viewModel: NewsDashboardFragmentViewModel

    @Before
    fun setup() {
        sourcesRepository = Mockito.mock(SourcesRepository::class.java)
        viewModel = NewsDashboardFragmentViewModel(sourcesRepository)
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testNull() {
        MatcherAssert.assertThat(viewModel.sourcesLiveData, CoreMatchers.notNullValue())
        verify(sourcesRepository, Mockito.never()).getSelectedSources()
    }

    @Test
    fun dontFetchWithoutObservers() {
        viewModel.getSelectedSources()
        verify(sourcesRepository, Mockito.never()).getSelectedSources()
    }

    @Test
    fun fetchWhenObserved() {
        viewModel.getSelectedSources()
        viewModel.sourcesLiveData.observeForever(Mockito.mock(Observer::class.java) as Observer<List<Source>>)
        verify(sourcesRepository, Mockito.times(1)).getSelectedSources()
    }

    @Test
    fun changeWhileObserved() {
        viewModel.sourcesLiveData.observeForever(Mockito.mock(Observer::class.java) as Observer<List<Source>>)
        viewModel.getSelectedSources()
        viewModel.getSelectedSources()
        verify(sourcesRepository, Mockito.times(2)).getSelectedSources()
    }
}