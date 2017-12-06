package com.lonelystudios.palantir.vo.sources

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
@Entity
data class Article(
        @PrimaryKey(autoGenerate = true) var id: Long? = null,

        var articlesId: Long = -1,

        @field:SerializedName("publishedAt")
        var publishedAt: String? = null,

        @field:SerializedName("author")
        var author: String? = null,

        @field:SerializedName("urlToImage")
        var urlToImage: String? = null,

        @field:SerializedName("description")
        var description: String? = null,

        @field:SerializedName("title")
        var title: String? = null,

        @field:SerializedName("url")
        var url: String? = null,

        @field:SerializedName("source")
        var source: ArticleSource
)