package com.lonelystudios.palantir.utils

import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by vicmns on 7/20/16.
 */

class CommonItemDecoration(private val mLeftInset: Int, private val mTopInset: Int, private val mRightInset: Int, private val mBottomInset: Int) : RecyclerView.ItemDecoration() {

    constructor(inset: Int) : this(inset, inset, inset, inset) {}

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        var leftInset = mLeftInset
        var rightInset = mRightInset
        var topInset = mTopInset
        val bottomInset = mBottomInset

        val columns = (parent.layoutManager as GridLayoutManager).spanCount
        if (columns > 1) {
            rightInset = mRightInset / 2
            leftInset = mLeftInset / 2
        }

        outRect.set(leftInset, topInset, rightInset, bottomInset)
    }
}