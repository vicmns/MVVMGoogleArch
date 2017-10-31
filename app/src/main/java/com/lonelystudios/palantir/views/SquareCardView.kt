package com.lonelystudios.palantir.views

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet

/**
 * Created by vicmns on 10/20/17.
 */
class SquareCardView : CardView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}