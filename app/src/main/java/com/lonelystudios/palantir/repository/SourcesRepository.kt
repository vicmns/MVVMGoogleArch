package com.lonelystudios.palantir.repository

import android.arch.lifecycle.LiveData
import com.lonelystudios.palantir.dao.SourcesDao
import com.lonelystudios.palantir.net.LogoService
import com.lonelystudios.palantir.net.NewsService
import com.lonelystudios.palantir.net.utils.ApiResponse
import com.lonelystudios.palantir.net.utils.CancelableRetrofitLiveDataCall
import com.lonelystudios.palantir.repository.util.CancelableNetworkBoundResource
import com.lonelystudios.palantir.utils.AppExecutors
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.logo.SourceLogoInfo
import com.lonelystudios.palantir.vo.sources.Source
import com.lonelystudios.palantir.vo.sources.Sources
import javax.inject.Inject

/**
 * Created by vicmns on 10/27/17.
 */
class SourcesRepository @Inject constructor(private val sourcesDao: SourcesDao,
                                            private val newsService: NewsService,
                                            private val logoService: LogoService,
                                            private val appExecutors: AppExecutors) {

    lateinit var sourcesResource: CancelableNetworkBoundResource<List<Source>, Sources>
    lateinit var sourceLogo: CancelableNetworkBoundResource<Source, SourceLogoInfo>

    fun getAllRepositories(): LiveData<Resource<List<Source>>> {
        //if (sourcesResource != null) sourcesResource.cancelServiceCall()
        sourcesResource = object : CancelableNetworkBoundResource<List<Source>, Sources>(appExecutors) {
            override fun saveCallResult(item: Sources) {
                val sourceList = item.sources
                sourcesDao.insert(*sourceList.toTypedArray())
            }

            override fun shouldFetch(data: List<Source>?): Boolean {
                return data?.isEmpty() ?: false
            }

            override fun loadFromDb(): LiveData<List<Source>> {
                return sourcesDao.getSources()
            }

            override fun createCall(): CancelableRetrofitLiveDataCall<ApiResponse<Sources>> {
                return newsService.getAllSources()
            }
        }

        return sourcesResource.asLiveData()
    }

    fun getSourceLogo(source: Source): LiveData<Resource<Source>> {
        sourceLogo = object : CancelableNetworkBoundResource<Source, SourceLogoInfo>(appExecutors) {
            override fun saveCallResult(item: SourceLogoInfo) {
                val iconItem = item.icons?.maxBy { iconsItem -> iconsItem.bytes }
                source.urlToLogo = iconItem?.url
                if(iconItem == null) source.isUrlLogoAvailable = false
                sourcesDao.update(source)
            }

            override fun shouldFetch(data: Source?): Boolean {
                return source.urlToLogo.isNullOrEmpty()
            }

            override fun loadFromDb(): LiveData<Source> {
                return sourcesDao.getSourcesById(source.id)
            }

            override fun createCall(): CancelableRetrofitLiveDataCall<ApiResponse<SourceLogoInfo>> {
                return logoService.getSourceLogo(source.url.toString())
            }

        }

        return sourceLogo.asLiveData()
    }

    fun updateSource(source: Source){
        appExecutors.diskIO().execute {
            sourcesDao.update(source)
        }
    }
}