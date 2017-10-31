package com.lonelystudios.palantir.views

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet

/**
 * Created by vicmns on 10/20/17.
 */
class SquareConstraintLayout : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle)

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}