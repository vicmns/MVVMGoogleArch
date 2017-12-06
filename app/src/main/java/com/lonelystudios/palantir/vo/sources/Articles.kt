package com.lonelystudios.palantir.vo.sources

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.lonelystudios.palantir.vo.SortBy
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
@Entity
data class Articles(
        @PrimaryKey(autoGenerate = true) var id: Long = -1,

        @Ignore
        @field:SerializedName("articles")
        var articles: List<Article>? = ArrayList(),

        @field:SerializedName("status")
        var status: String? = ""
)