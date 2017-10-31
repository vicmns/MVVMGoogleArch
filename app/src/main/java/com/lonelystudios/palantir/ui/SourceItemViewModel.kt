package com.lonelystudios.palantir.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.lonelystudios.palantir.repository.SourcesRepository
import com.lonelystudios.palantir.utils.AbsentLiveData
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.sources.Source
import javax.inject.Inject

/**
 * Created by vicmns on 10/28/17.
 */
class SourceItemViewModel @Inject constructor(private var sourcesRepository: SourcesRepository): ViewModel() {

    val sourcesLiveData: LiveData<Resource<Source>>
    val triggerGetLogoInfo: MutableLiveData<Source> = MutableLiveData()

    init {
        sourcesLiveData = Transformations.switchMap(triggerGetLogoInfo) {
            if(it.urlToLogo.isNullOrEmpty()) sourcesRepository.getSourceLogo(it)
            else AbsentLiveData.create()
        }
    }

    fun getLogoInfo(source: Source) {
        triggerGetLogoInfo.value = source
    }
}