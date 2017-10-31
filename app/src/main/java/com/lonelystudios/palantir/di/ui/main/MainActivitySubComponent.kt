package com.lonelystudios.palantir.di.ui.main

import com.lonelystudios.palantir.di.FragmentBuildersModule
import com.lonelystudios.palantir.di.SupportActivityModule
import com.lonelystudios.palantir.di.scope.PerActivity
import com.lonelystudios.palantir.di.viewmodel.ViewModelModule
import com.lonelystudios.palantir.ui.MainActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

/**
 * Created by vicmns on 8/3/17.
 */

@PerActivity
@Subcomponent(modules = arrayOf(MainActivitySubcomponentModule::class,
        SupportActivityModule::class, FragmentBuildersModule::class))
interface MainActivitySubComponent : AndroidInjector<MainActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>()
}
