package com.lonelystudios.palantir.vo.sources

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.lonelystudios.palantir.vo.SortBy
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
@Entity(tableName = "sources")
data class Source(

        @field:SerializedName("id")
        @PrimaryKey var id: String = "",

        @field:SerializedName("country")
        var country: String? = null,

        var urlToLogo: String? = null,

        var isUrlLogoAvailable: Boolean = true,

        @field:SerializedName("name")
        var name: String? = null,

        @field:SerializedName("sortBysAvailable")
        var sortBysAvailable: List<String> = emptyList(),

        @field:SerializedName("description")
        var description: String? = null,

        @field:SerializedName("language")
        var language: String? = null,

        @field:SerializedName("category")
        var category: String? = null,

        @field:SerializedName("url")
        var url: String? = null,

        var isUserSelected: Boolean = false
)