package com.lonelystudios.palantir.vo.sources

import com.google.gson.annotations.SerializedName

data class Articles(
        @field:SerializedName("articles")
        var articles: List<Article>? = ArrayList(),

        @field:SerializedName("status")
        var status: String? = ""
)