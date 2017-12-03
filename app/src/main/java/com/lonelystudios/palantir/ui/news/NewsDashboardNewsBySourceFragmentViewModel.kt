package com.lonelystudios.palantir.ui.news

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.lonelystudios.palantir.repository.ArticlesRepository
import com.lonelystudios.palantir.utils.AbsentLiveData
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.sources.Articles
import com.lonelystudios.palantir.vo.sources.Source
import java.util.*
import javax.inject.Inject

/**
 * Created by vicmns on 12/1/17.
 */
class NewsDashboardNewsBySourceFragmentViewModel @Inject constructor(articlesRepository: ArticlesRepository): ViewModel() {
    val articlesLiveData: LiveData<Resource<Articles>>
    private val triggerGetArticles: MutableLiveData<List<Source>> = MutableLiveData()

    init {
        articlesLiveData = Transformations.switchMap(triggerGetArticles) {
            if (it != null && it.isNotEmpty()) articlesRepository.getArticlesBySources(it)
            else AbsentLiveData.create()
        }
    }

    fun getAllArticlesFromSelectedSources(source: Source) {
        triggerGetArticles.value = Collections.singletonList(source)
    }
}