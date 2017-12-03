package com.lonelystudios.palantir.ui.news

import android.arch.lifecycle.ViewModel
import javax.inject.Inject

/**
 * Created by vicmns on 11/30/17.
 */

class NewsActivityViewModel @Inject constructor(): ViewModel() {
    var currentFragment: String = AllNewsFragment.TAG
}
