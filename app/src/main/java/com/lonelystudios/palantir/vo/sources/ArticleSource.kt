package com.lonelystudios.palantir.vo.sources

import android.arch.persistence.room.ColumnInfo
import com.google.gson.annotations.SerializedName

/**
 * Created by vicmns on 12/5/17.
 */
data class ArticleSource(
        @field:SerializedName("id")
        @ColumnInfo(name = "source_id") var sourceId: String?,
        @field:SerializedName("name")
        @ColumnInfo(name = "source_name") var sourceName: String?)