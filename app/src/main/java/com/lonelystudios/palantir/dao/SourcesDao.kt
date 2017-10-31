package com.lonelystudios.palantir.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.lonelystudios.palantir.vo.sources.Source

/**
 * Created by vicmns on 10/25/17.
 */
@Dao
abstract class SourcesDao: BaseDao<Source> {

    @Query("SELECT * FROM Sources")
    abstract fun getSources(): LiveData<List<Source>>

    @Query("SELECT * FROM Sources WHERE isUserSelected = 1")
    abstract fun getSelectedSources(): LiveData<List<Source>>

    @Query("SELECT * FROM Sources WHERE id = :id")
    abstract fun getSourcesById(id: String): LiveData<Source>
}