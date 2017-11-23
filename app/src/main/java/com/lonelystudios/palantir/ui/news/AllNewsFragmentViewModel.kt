package com.lonelystudios.palantir.ui.news

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.lonelystudios.palantir.repository.ArticlesRepository
import com.lonelystudios.palantir.repository.SourcesRepository
import com.lonelystudios.palantir.utils.AbsentLiveData
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.sources.Articles
import com.lonelystudios.palantir.vo.sources.Source
import javax.inject.Inject

/**
 * Created by vicmns on 11/20/17.
 */
class AllNewsFragmentViewModel @Inject constructor(articlesRepository: ArticlesRepository,
                                                   sourcesRepository: SourcesRepository) : ViewModel() {

    val articlesLiveData: LiveData<Resource<Articles>>
    val sourcesLiveData: LiveData<List<Source>>
    var userSources: List<Source> = ArrayList()
    private val triggerGetArticles: MutableLiveData<List<Source>> = MutableLiveData()
    private val triggerGetSources: MutableLiveData<Boolean> = MutableLiveData()


    init {
        articlesLiveData = Transformations.switchMap(triggerGetArticles) {
            if (it != null && it.isNotEmpty()) articlesRepository.getArticlesBySources(it)
            else AbsentLiveData.create()
        }

        sourcesLiveData = Transformations.switchMap(triggerGetSources) {
            if(it) sourcesRepository.getSelectedSources()
            else AbsentLiveData.create()
        }
    }

    fun getAllArticlesFromSelectedSources(sources: List<Source>) {
        triggerGetArticles.value = sources
    }

    fun getSelectedSources() {
        triggerGetSources.value = true
    }
}