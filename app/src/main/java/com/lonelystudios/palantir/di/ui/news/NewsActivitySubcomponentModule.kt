package com.lonelystudios.palantir.di.ui.news

import android.support.v7.app.AppCompatActivity

import com.lonelystudios.palantir.ui.MainActivity
import com.lonelystudios.palantir.di.scope.PerActivity
import com.lonelystudios.palantir.ui.news.NewsActivity

import dagger.Binds
import dagger.Module

/**
 * Created by vicmns on 9/22/17.
 */

@Module
interface NewsActivitySubcomponentModule {
    @PerActivity
    @Binds
    fun baseActivity(activity: NewsActivity): AppCompatActivity
}
