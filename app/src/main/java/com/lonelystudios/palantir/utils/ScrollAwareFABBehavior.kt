package com.lonelystudios.palantir.utils

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.View


/**
 * Created by vicmns on 11/30/17.
 */
class ScrollAwareFABBehavior(context: Context, attrs: AttributeSet): FloatingActionButton.Behavior(context, attrs) {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: FloatingActionButton, dependency: View): Boolean =
            dependency is AppBarLayout || super.layoutDependsOn(parent, child, dependency)

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: FloatingActionButton, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            if (dependency.getBottom() == 0 && child.visibility == View.VISIBLE) {
                child.hide()
                return true
            } else if (dependency.getBottom() == dependency.getHeight() && child.visibility != View.VISIBLE) {
                // update the translation Y
                child.translationY = getFabTranslationYForSnackbar(parent, child)
                child.show()
                return true
            }
            return false
        } else {
            return super.onDependentViewChanged(parent, child, dependency)
        }
    }

    /**
     * @see android.support.design.widget.FloatingActionButton.Behavior.getFabTranslationYForSnackbar
     */
    private fun getFabTranslationYForSnackbar(parent: CoordinatorLayout, fab: FloatingActionButton): Float {
        var minOffset = 0f
        val dependencies = parent.getDependencies(fab)
        var i = 0
        val z = dependencies.size
        while (i < z) {
            val view = dependencies[i]
            if (view is Snackbar.SnackbarLayout) {
                minOffset = Math.min(minOffset, view.getTranslationY() - view.getHeight())
            }
            i++
        }

        return minOffset
    }
}