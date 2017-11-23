package com.lonelystudios.palantir.di.ui.news


import android.app.Activity
import com.lonelystudios.palantir.ui.MainActivity
import com.lonelystudios.palantir.ui.news.NewsActivity
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by vicmns on 8/3/17.
 */

@Module(subcomponents = arrayOf(NewsActivitySubComponent::class))
abstract class NewsActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(NewsActivity::class)
    abstract fun bindNewsActivityInjectorFactory(
            builder: NewsActivitySubComponent.Builder): AndroidInjector.Factory<out Activity>
}
