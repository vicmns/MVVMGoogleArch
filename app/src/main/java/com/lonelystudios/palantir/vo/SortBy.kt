package com.lonelystudios.palantir.vo

import android.support.annotation.StringDef

/**
 * Created by vicmns on 10/25/17.
 */
class SortBy {
    @StringDef(TOP, LATEST, POPULAR)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Sort

    companion object {

        const val TOP = "top"
        const val LATEST = "latest"
        const val POPULAR = "popular"
    }
}