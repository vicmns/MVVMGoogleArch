package com.lonelystudios.palantir.vo.sources

import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
data class Sources(

        @field:SerializedName("sources")
        val sources: List<Source> = emptyList(),

        @field:SerializedName("status")
        val status: String? = null
)