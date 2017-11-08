package com.lonelystudios.palantir.utils

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
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
                if(!it.isUrlLogoAvailable) {
                    progressBar.visibility = View.GONE
                    imageView.setImageResource(R.drawable.placeholder)
                    return
                } else if(it.urlToLogo.isNullOrEmpty()) {
                    imageView.setImageResource(R.drawable.placeholder)
                    return
                }
                GlideApp.with(imageView.context).load(it.urlToLogo).listener(object: RequestListener<Drawable> {
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
}