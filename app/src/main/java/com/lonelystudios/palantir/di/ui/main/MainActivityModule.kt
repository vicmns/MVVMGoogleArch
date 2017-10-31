package com.lonelystudios.palantir.di.ui.main


import android.app.Activity
import com.lonelystudios.palantir.ui.MainActivity
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by vicmns on 8/3/17.
 */

@Module(subcomponents = arrayOf(MainActivitySubComponent::class))
abstract class MainActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(MainActivity::class)
    abstract fun bindMainActivityInjectorFactory(
            builder: MainActivitySubComponent.Builder): AndroidInjector.Factory<out Activity>
}
