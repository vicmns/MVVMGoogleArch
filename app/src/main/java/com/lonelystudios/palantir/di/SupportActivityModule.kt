package com.lonelystudios.palantir.di

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity


import com.lonelystudios.palantir.di.scope.PerActivity

import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Named

/**
 * Created by vicmns on 8/2/17.
 */

@Module
abstract class SupportActivityModule {

    @Binds
    @PerActivity
    @Named(ACTIVITY_CONTEXT)
    abstract fun activityContext(activity: AppCompatActivity): Context

    @Module
    companion object {
        const val ACTIVITY_CONTEXT = "ActivityContext"

        @Provides
        @PerActivity
        @JvmStatic
        fun activityFragmentManager(activity: AppCompatActivity): FragmentManager {
            return activity.supportFragmentManager
        }
    }

}
