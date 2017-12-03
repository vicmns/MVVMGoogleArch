package com.lonelystudios.palantir.ui.news

import com.lonelystudios.palantir.vo.sources.Article

interface OnNewsItemInteraction {
    fun onListFragmentInteraction(item: Article)
}