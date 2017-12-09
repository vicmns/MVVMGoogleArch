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

    fun getAllSources(): LiveData<Resource<List<Source>>> {
        //if (sourcesResource != null) sourcesResource.cancelServiceCall()
        sourcesResource = object : CancelableNetworkBoundResource<List<Source>, Sources>(appExecutors) {
            var isUpdate = false

            override fun saveCallResult(item: Sources) {
                val sourceList = item.sources
                if(isUpdate) {
                    sourcesDao.update(*sourceList.toTypedArray())
                } else {
                    sourcesDao.insert(*sourceList.toTypedArray())
                }
            }

            override fun shouldFetch(data: List<Source>?): Boolean {
                isUpdate = data?.isNotEmpty() ?: false
                return data?.isEmpty() ?: true
            }

            override fun loadFromDb(): LiveData<List<Source>> = sourcesDao.getSources()

            override fun createCall(): CancelableRetrofitLiveDataCall<ApiResponse<Sources>> =
                    newsService.getAllSources()
        }

        return sourcesResource.asLiveData()
    }

    fun getSelectedSources(): LiveData<List<Source>> = sourcesDao.getSelectedSources()

    fun getSourceLogo(source: Source): LiveData<Resource<Source>> {
        sourceLogo = object : CancelableNetworkBoundResource<Source, SourceLogoInfo>(appExecutors) {
            override fun saveCallResult(item: SourceLogoInfo) {
                val iconItem = item.icons?.maxBy { iconsItem -> iconsItem.bytes }
                source.urlToLogo = iconItem?.url
                if(iconItem == null) source.isUrlLogoAvailable = false
                sourcesDao.update(source)
            }

            override fun shouldFetch(data: Source?): Boolean = source.urlToLogo.isNullOrEmpty()

            override fun loadFromDb(): LiveData<Source> = sourcesDao.getSourcesById(source.id)

            override fun createCall(): CancelableRetrofitLiveDataCall<ApiResponse<SourceLogoInfo>> =
                    logoService.getSourceLogo(source.url.toString())

        }

        return sourceLogo.asLiveData()
    }

    fun updateSource(source: Source){
        appExecutors.diskIO().execute {
            sourcesDao.update(source)
        }
    }
}