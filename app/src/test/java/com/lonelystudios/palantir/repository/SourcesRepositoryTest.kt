package com.lonelystudios.palantir.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.lonelystudios.palantir.dao.SourcesDao
import com.lonelystudios.palantir.db.PalantirDataBase
import com.lonelystudios.palantir.net.LogoService
import com.lonelystudios.palantir.net.NewsService
import com.lonelystudios.palantir.net.utils.ApiResponse
import com.lonelystudios.palantir.net.utils.CancelableRetrofitLiveDataCall
import com.lonelystudios.palantir.util.ApiUtil.successCancelableCall
import com.lonelystudios.palantir.util.InstantAppExecutors
import com.lonelystudios.palantir.util.TestUtil
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.logo.SourceLogoInfo
import com.lonelystudios.palantir.vo.sources.Source
import com.lonelystudios.palantir.vo.sources.Sources
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.*

/**
 * Created by vicmns on 12/8/17.
 */
@RunWith(JUnit4::class)
class SourcesRepositoryTest {
    private lateinit var repository: SourcesRepository
    private lateinit var dao: SourcesDao
    private lateinit var service: NewsService
    private lateinit var logoService: LogoService

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        dao = mock(SourcesDao::class.java)
        service = mock(NewsService::class.java)
        logoService = mock(LogoService::class.java)
        val db = mock(PalantirDataBase::class.java)
        whenever(db.sourcesDao()).thenReturn(dao)
        repository = SourcesRepository(dao, service, logoService, InstantAppExecutors())
    }

    @Test
    fun getAllSources() {
        val dbData: MutableLiveData<List<Source>> = MutableLiveData()
        whenever(dao.getSources()).thenReturn(dbData)

        val source = TestUtil.createSource("foo", "us", "http://foo.bar/logo", "foo",
                "foo bar news", "en-US", "who", "http://foo.bar/", true)
        val sources = Sources(Collections.singletonList(source), "ok")

        val call: CancelableRetrofitLiveDataCall<ApiResponse<Sources>> = successCancelableCall(sources)
        whenever(service.getAllSources()).thenReturn(call)

        val data: LiveData<Resource<List<Source>>> = repository.getAllSources()
        verify(dao).getSources()
        verifyNoMoreInteractions(service)

        val observer: Observer<*> = mock(Observer::class.java)
        data.observeForever(observer as Observer<Resource<List<Source>>>)
        verifyNoMoreInteractions(service)
        verify(observer).onChanged(Resource.loading(null))
        val updateDbData = MutableLiveData<List<Source>>()
        whenever(dao.getSources()).thenReturn(updateDbData)

        dbData.postValue(null)
        verify(service).getAllSources()
        verify(dao).insert(*sources.sources.toTypedArray())

        updateDbData.postValue(sources.sources)
        verify(observer).onChanged(Resource.success(sources.sources))
    }

    @Test
    fun getSelectedSources() {
        val source = TestUtil.createSource("foo", "us", "http://foo.bar/logo", "foo",
                "foo bar news", "en-US", "who", "http://foo.bar/", true)
        val sources = Collections.singletonList(source)
        val dbData: MutableLiveData<List<Source>> = MutableLiveData()
        dbData.value = sources
        whenever(dao.getSelectedSources()).thenReturn(dbData)

        val data: LiveData<List<Source>> = repository.getSelectedSources()
        val observer: Observer<*> = mock(Observer::class.java)
        data.observeForever(observer as Observer<List<Source>>)
        verify(dao).getSelectedSources()
        verify(observer).onChanged(sources)
    }

    @Test
    fun getSourcesLogo() {
        val dbData: MutableLiveData<Source> = MutableLiveData()
        whenever(dao.getSourcesById("foo")).thenReturn(dbData)

        val source = TestUtil.createSource("foo", "us", "",  "foo",
                "foo bar news", "en-US", "who", "http://foo.bar/", true)
        val sourceLogoInfo = TestUtil.createSourceLogoInfo(Collections.singletonList(
                TestUtil.createIconsItem(100, 100, 100)),
                "http://foo.bar/")

        val call: CancelableRetrofitLiveDataCall<ApiResponse<SourceLogoInfo>> = successCancelableCall(sourceLogoInfo)
        whenever(logoService.getSourceLogo("http://foo.bar/")).thenReturn(call)

        val data: LiveData<Resource<Source>> = repository.getSourceLogo(source)
        verify(dao).getSourcesById("foo")
        verifyNoMoreInteractions(logoService)

        val observer: Observer<*> = mock(Observer::class.java)
        data.observeForever(observer as Observer<Resource<Source>>)
        verifyNoMoreInteractions(service)
        verify(observer).onChanged(Resource.loading(null))
        val updateDbData = MutableLiveData<Source>()
        whenever(dao.getSourcesById("foo")).thenReturn(updateDbData)

        dbData.postValue(null)
        verify(logoService).getSourceLogo("http://foo.bar/")
        verify(dao).update(source)

        updateDbData.postValue(source)
        verify(observer).onChanged(Resource.success(source))
    }
}