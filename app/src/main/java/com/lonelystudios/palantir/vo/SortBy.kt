package com.lonelystudios.palantir.vo

import android.support.annotation.StringDef

/**
 * Created by vicmns on 10/25/17.
 */
class SortBy {
    companion object {

        @StringDef(TOP, LATEST, POPULAR)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Sort

        const val TOP = "top"
        const val LATEST = "latest"
        const val POPULAR = "popular"
    }

    @Sort
    private lateinit var sortBy: String

    fun setSortBy(@Sort sortBy: String) {
        this.sortBy = sortBy
    }
}