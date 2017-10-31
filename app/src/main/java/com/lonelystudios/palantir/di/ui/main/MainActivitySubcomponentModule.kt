package com.lonelystudios.palantir.di.ui.main

import android.support.v7.app.AppCompatActivity

import com.lonelystudios.palantir.ui.MainActivity
import com.lonelystudios.palantir.di.scope.PerActivity

import dagger.Binds
import dagger.Module

/**
 * Created by vicmns on 9/22/17.
 */

@Module
interface MainActivitySubcomponentModule {
    @PerActivity
    @Binds
    fun baseActivity(activity: MainActivity): AppCompatActivity
}
