package com.lonelystudios.palantir.ui.news

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.lonelystudios.palantir.repository.SourcesRepository
import com.lonelystudios.palantir.utils.AbsentLiveData
import com.lonelystudios.palantir.vo.sources.Source
import javax.inject.Inject

/**
 * Created by vicmns on 12/1/17.
 */
class NewsDashboardFragmentViewModel @Inject constructor(sourcesRepository: SourcesRepository) : ViewModel() {
    val sourcesLiveData: LiveData<List<Source>>
    var sources: List<Source> = ArrayList()
    private val triggerGetSources: MutableLiveData<Boolean> = MutableLiveData()

    init {
        sourcesLiveData = Transformations.switchMap(triggerGetSources) {
            if(it) sourcesRepository.getSelectedSources()
            else AbsentLiveData.create()
        }
    }

    fun getSelectedSources() {
        triggerGetSources.value = true
    }
}