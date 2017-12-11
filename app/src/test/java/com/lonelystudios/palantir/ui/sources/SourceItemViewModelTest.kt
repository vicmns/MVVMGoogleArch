package com.lonelystudios.palantir.ui.sources

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.lonelystudios.palantir.repository.SourcesRepository
import com.lonelystudios.palantir.util.TestUtil
import com.lonelystudios.palantir.vo.Resource
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
import org.mockito.Mockito.never
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

/**
 * Created by vicmns on 12/11/17.
 */
@RunWith(MockitoJUnitRunner::class)
class SourceItemViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    lateinit var repository: SourcesRepository
    lateinit var viewModel: SourceItemViewModel

    @Captor
    lateinit var sourceCaptor: ArgumentCaptor<Source>

    @Before
    fun setup() {
        repository = Mockito.mock(SourcesRepository::class.java)
        viewModel = SourceItemViewModel(repository)
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testNull() {
        MatcherAssert.assertThat(viewModel.sourcesLiveData, CoreMatchers.notNullValue())
        verify(repository, never()).getAllSources()
    }

    @Test
    fun dontFetchWithoutObservers() {
        val source = TestUtil.createSource("foo")
        viewModel.getLogoInfo(source)
        verify(repository, never()).getSourceLogo(source)
    }

    @Test
    fun fetchWhenObserved() {
        val source = TestUtil.createSource("foo")
        viewModel.getLogoInfo(source)
        viewModel.sourcesLiveData.observeForever(Mockito.mock(Observer::class.java) as Observer<Resource<Source>>)
        verify(repository, Mockito.times(1)).getSourceLogo(source)
    }

    @Test
    fun changeWhileObserved() {
        viewModel.sourcesLiveData.observeForever(Mockito.mock(Observer::class.java) as Observer<Resource<Source>>)
        val source1 = TestUtil.createSource("foo")
        val source2 = TestUtil.createSource("bar")

        viewModel.getLogoInfo(source1)
        viewModel.getLogoInfo(source2)

        verify(repository, Mockito.times(2)).getSourceLogo(capture(sourceCaptor))
        MatcherAssert.assertThat(sourceCaptor.allValues, CoreMatchers.`is`(Arrays.asList(source1, source2)))
    }

    @Test
    fun logoInfo() {
        val source = TestUtil.createSource("foo")
        val observer: Observer<Resource<Source>> = (Mockito.mock(Observer::class.java) as Observer<Resource<Source>>)
        viewModel.sourcesLiveData.observeForever(observer)
        verifyNoMoreInteractions(observer)
        verifyNoMoreInteractions(repository)
        viewModel.getLogoInfo(source)
        repository.updateSource(source)
        verify(repository).updateSource(source)
    }

}