package com.lonelystudios.palantir.utils

import android.content.res.TypedArray
import android.databinding.BindingAdapter
import android.databinding.BindingConversion
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.lonelystudios.palantir.R
import com.lonelystudios.palantir.vo.sources.Source
import android.view.ViewGroup




/**
 * Created by vicmns on 10/27/17.
 */
object DataBindingAdapters {

    @JvmStatic
    @BindingAdapter("app:source")
    fun setSourceLogo(frameLayout: FrameLayout?, source: Source?) {
        frameLayout?.let {
            val imageView: ImageView = it.findViewById(R.id.sourceLogoImageView)
            val progressBar: ProgressBar = it.findViewById(R.id.sourceLogoProgressBar)
            source?.let {
                progressBar.visibility = View.VISIBLE
                if (!it.isUrlLogoAvailable) {
                    progressBar.visibility = View.GONE
                    imageView.setImageResource(R.drawable.placeholder)
                    return
                } else if (it.urlToLogo.isNullOrEmpty()) {
                    imageView.setImageResource(R.drawable.placeholder)
                    return
                }
                GlideApp.with(imageView.context).load(it.urlToLogo).listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                }).placeholder(R.drawable.placeholder).into(imageView)
            }
            progressBar.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("app:articleImageUrl")
    fun setArticleImage(imageView: ImageView?, imageUrl: String?) {
        imageView?.let { cImageView ->
            imageUrl?.let { cArticle ->
                GlideApp.with(cImageView.context).load(cArticle)
                        .placeholder(R.drawable.placeholder).into(cImageView)
            }
        }
    }

    @JvmStatic
    @BindingConversion
    fun convertBooleanToVisibility(visible: Boolean): Int =
            if (visible) View.VISIBLE else View.INVISIBLE

    @JvmStatic
    @BindingAdapter("android:layout_marginBottom")
    fun setBottomMargin(view: View, bottomMargin: Float) {
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin,
                layoutParams.rightMargin, bottomMargin.toInt())
        view.layoutParams = layoutParams
    }

    @JvmStatic
    @BindingAdapter("app:setColorScheme")
    fun setColorScheme(swipeRefreshLayout: SwipeRefreshLayout, typedArray: TypedArray) {
        val colors = IntArray(typedArray.length())
        for (i in 0 until typedArray.length()) {
            colors[i] = typedArray.getColor(i, Color.BLACK)
        }
        swipeRefreshLayout.setColorSchemeColors(*colors)
    }
}