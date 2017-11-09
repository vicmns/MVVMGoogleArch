package com.lonelystudios.palantir.ui.sources

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.lonelystudios.palantir.net.LogoService
import com.lonelystudios.palantir.net.NewsService
import com.lonelystudios.palantir.repository.SourcesRepository
import com.lonelystudios.palantir.utils.AbsentLiveData
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.sources.Source
import javax.inject.Inject

/**
 * Created by vicmns on 10/25/17.
 */
class SourcesViewModel @Inject constructor(private val sourcesRepository: SourcesRepository) : ViewModel() {

    val sourcesLiveData: LiveData<Resource<List<Source>>>
    private val triggerGetSources: MutableLiveData<Boolean> = MutableLiveData()

    init {
        sourcesLiveData = Transformations.switchMap(triggerGetSources) {
            if(it) sourcesRepository.getAllRepositories()
            else AbsentLiveData.create()
        }
    }

    fun getAllSources() {
        triggerGetSources.value = true
    }

    fun updateSelectedSource(source: Source) {
        sourcesRepository.updateSource(source)
    }
}