package com.lonelystudios.palantir.di

import android.app.Application
import com.lonelystudios.palantir.PalantirApp
import com.lonelystudios.palantir.di.scope.PerApp
import com.lonelystudios.palantir.di.ui.main.MainActivityModule
import com.lonelystudios.palantir.di.ui.news.NewsActivityModule
import com.lonelystudios.palantir.di.ui.sources.SourcesActivityModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule

/**
 * Created by vicmns on 7/29/17.
 */

@PerApp
@Component(modules = arrayOf(AndroidSupportInjectionModule::class,
        AppModule::class, MainActivityModule::class,
        SourcesActivityModule::class, NewsActivityModule::class))
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun netModule(netModule: NetModule): Builder

        fun build(): AppComponent
    }

    fun inject(app: PalantirApp)
}
