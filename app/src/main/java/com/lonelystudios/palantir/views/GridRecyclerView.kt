package com.lonelystudios.palantir.views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.view.animation.GridLayoutAnimationController
import android.view.ViewGroup



/**
 * Created by vicmns on 11/7/17.
 */
class GridRecyclerView: RecyclerView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun attachLayoutAnimationParameters(child: View, params: ViewGroup.LayoutParams, index: Int, count: Int) {
        if (adapter != null && layoutManager is GridLayoutManager) {

            val animationParams: GridLayoutAnimationController.AnimationParameters?

            if (params.layoutAnimationParameters == null) {
                animationParams = GridLayoutAnimationController.AnimationParameters()
                params.layoutAnimationParameters = animationParams
            } else {
                animationParams = params.layoutAnimationParameters as GridLayoutAnimationController.AnimationParameters
            }

            val columns = (layoutManager as GridLayoutManager).spanCount

            animationParams.count = count
            animationParams.index = index
            animationParams.columnsCount = columns
            animationParams.rowsCount = count / columns

            val invertedIndex = count - 1 - index
            animationParams.column = columns - 1 - invertedIndex % columns
            animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns

        } else {
            super.attachLayoutAnimationParameters(child, params, index, count)
        }
    }
}