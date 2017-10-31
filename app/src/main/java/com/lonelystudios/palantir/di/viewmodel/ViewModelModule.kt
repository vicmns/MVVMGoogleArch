package com.lonelystudios.palantir.di.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.lonelystudios.palantir.ui.SourceItemViewModel
import com.lonelystudios.palantir.ui.sources.SourcesViewModel

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SourcesViewModel::class)
    abstract fun bidSourcesViewModel(sourcesViewModel: SourcesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SourceItemViewModel::class)
    abstract fun bidSourcesItemViewModel(sourcesItemViewModel: SourceItemViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: PalantirViewModelFactory): ViewModelProvider.Factory

}
