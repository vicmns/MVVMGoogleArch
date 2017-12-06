package com.lonelystudios.palantir.vo.sources

import com.google.gson.annotations.SerializedName

/**
 * Created by vicmns on 12/5/17.
 */
data class ArticleSource(
        @field:SerializedName("id") var id: String?,
        @field:SerializedName("name") var name: String?) {
}