package com.lonelystudios.palantir.di.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.lonelystudios.palantir.ui.SourceItemViewModel
import com.lonelystudios.palantir.ui.news.AllNewsFragmentViewModel
import com.lonelystudios.palantir.ui.news.NewsActivity
import com.lonelystudios.palantir.ui.news.NewsActivityViewModel
import com.lonelystudios.palantir.ui.sources.SourcesViewModel

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(NewsActivityViewModel::class)
    abstract fun bidNewsActivityViewModel(newsActivityViewModel: NewsActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SourcesViewModel::class)
    abstract fun bidSourcesViewModel(sourcesViewModel: SourcesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SourceItemViewModel::class)
    abstract fun bidSourcesItemViewModel(sourcesItemViewModel: SourceItemViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AllNewsFragmentViewModel::class)
    abstract fun bindNewsFragmentViewModel(newsFragmentViewModel: AllNewsFragmentViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: PalantirViewModelFactory): ViewModelProvider.Factory

}
