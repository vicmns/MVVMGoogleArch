package com.lonelystudios.palantir.di.ui.sources

import com.lonelystudios.palantir.di.FragmentBuildersModule
import com.lonelystudios.palantir.di.SupportActivityModule
import com.lonelystudios.palantir.di.scope.PerActivity
import com.lonelystudios.palantir.ui.sources.SourcesActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

/**
 * Created by vicmns on 8/3/17.
 */

@PerActivity
@Subcomponent(modules = arrayOf(SourcesActivitySubcomponentModule::class,
        SupportActivityModule::class, FragmentBuildersModule::class))
interface SourcesActivitySubComponent : AndroidInjector<SourcesActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SourcesActivity>()
}
