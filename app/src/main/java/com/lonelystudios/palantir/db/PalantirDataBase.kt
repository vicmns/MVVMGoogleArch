package com.lonelystudios.palantir.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.lonelystudios.palantir.dao.ArticlesDao
import com.lonelystudios.palantir.dao.SourcesDao
import com.lonelystudios.palantir.vo.sources.Article
import com.lonelystudios.palantir.vo.sources.Articles
import com.lonelystudios.palantir.vo.sources.Source

/**
 * Created by vicmns on 10/25/17.
 */
@Database(entities = [(Article::class), (Source::class)],
        version = 1)
@TypeConverters(EnumConverter::class)
abstract class PalantirDataBase: RoomDatabase() {

    abstract fun articlesDao(): ArticlesDao

    abstract fun sourcesDao(): SourcesDao
}