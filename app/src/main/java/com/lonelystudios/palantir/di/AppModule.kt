package com.lonelystudios.palantir.di

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.lonelystudios.palantir.dao.ArticlesDao
import com.lonelystudios.palantir.dao.SourcesDao
import com.lonelystudios.palantir.db.PalantirDataBase
import com.lonelystudios.palantir.di.scope.PerApp
import com.lonelystudios.palantir.di.viewmodel.ViewModelModule
import dagger.Module
import dagger.Provides

/**
 * Created by vicmns on 10/19/17.
 */
@Module(includes = arrayOf(NetModule::class, ViewModelModule::class))
class AppModule {

    @Provides
    @PerApp
    fun provideAppContext(app: Application): Context = app

    @Provides
    @PerApp
    fun providesDB(app: Application): PalantirDataBase {
        return Room.databaseBuilder(app, PalantirDataBase::class.java, "palantir.db").build()
    }

    @Provides
    @PerApp
    fun providesSourcesDao(db: PalantirDataBase): SourcesDao {
        return db.sourcesDao()
    }

    @Provides
    @PerApp
    fun providesArticlesDao(db: PalantirDataBase): ArticlesDao {
        return db.articlesDao()
    }
}