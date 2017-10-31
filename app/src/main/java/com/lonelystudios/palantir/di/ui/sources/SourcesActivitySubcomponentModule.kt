package com.lonelystudios.palantir.di.ui.sources

import android.support.v7.app.AppCompatActivity
import com.lonelystudios.palantir.di.scope.PerActivity
import com.lonelystudios.palantir.ui.sources.SourcesActivity
import dagger.Binds
import dagger.Module

/**
 * Created by vicmns on 9/22/17.
 */

@Module
interface SourcesActivitySubcomponentModule {
    @PerActivity
    @Binds
    fun baseActivity(activity: SourcesActivity): AppCompatActivity
}
