package com.lonelystudios.palantir.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

/**
 * Created by vicmns on 10/28/17.
 */
class SquareImageView: ImageView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        val size = Math.min(width, height)
        super.onMeasure(size, size)
    }
}