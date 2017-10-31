package com.lonelystudios.palantir.vo.sources

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Relation
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class ArticlesArticle(
        @Embedded
        var articlesItem: Articles? = null,

        @Relation(parentColumn = "id", entityColumn = "articlesId", entity = Article::class)
        var articles: List<Article>? = ArrayList()
)