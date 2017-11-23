package com.lonelystudios.palantir.di.ui.news

import com.lonelystudios.palantir.di.FragmentBuildersModule
import com.lonelystudios.palantir.di.SupportActivityModule
import com.lonelystudios.palantir.di.scope.PerActivity
import com.lonelystudios.palantir.di.viewmodel.ViewModelModule
import com.lonelystudios.palantir.ui.MainActivity
import com.lonelystudios.palantir.ui.news.NewsActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

/**
 * Created by vicmns on 8/3/17.
 */

@PerActivity
@Subcomponent(modules = arrayOf(NewsActivitySubcomponentModule::class,
        SupportActivityModule::class, FragmentBuildersModule::class))
interface NewsActivitySubComponent : AndroidInjector<NewsActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<NewsActivity>()
}
