package com.lonelystudios.palantir.di.ui.sources


import android.app.Activity
import com.lonelystudios.palantir.ui.sources.SourcesActivity
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by vicmns on 8/3/17.
 */

@Module(subcomponents = arrayOf(SourcesActivitySubComponent::class))
abstract class SourcesActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(SourcesActivity::class)
    abstract fun bindSourceActivityInjectorFactory(
            builder: SourcesActivitySubComponent.Builder): AndroidInjector.Factory<out Activity>
}
