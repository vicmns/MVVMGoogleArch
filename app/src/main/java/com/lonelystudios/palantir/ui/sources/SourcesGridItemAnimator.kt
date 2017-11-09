package com.lonelystudios.palantir.ui.sources

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.view.View


/**
 * Created by vicmns on 11/8/17.
 */
class SourcesGridItemAnimator : SourceCommonItemAnimator<SourcesGridAdapter.SourcesGridViewHolder>() {


    override fun animateAddSource(holder: SourcesGridAdapter.SourcesGridViewHolder) {
        val animatorSet = AnimatorSet()
        val x = holder.itemViewRowItemBinding.overlayView.right
        val y = holder.itemViewRowItemBinding.overlayView.bottom
        val overlayAnim = createCircularReveal(holder.itemViewRowItemBinding.overlayView,
                x / 2, y / 2,
                Math.max(holder.itemViewRowItemBinding.overlayView.width,
                        holder.itemViewRowItemBinding.overlayView.height) / 2.toFloat(),
                Math.hypot(holder.itemViewRowItemBinding.overlayView.width.toDouble(),
                        holder.itemViewRowItemBinding.overlayView.height.toDouble()).toFloat())
        holder.itemViewRowItemBinding.overlayView.visibility = View.VISIBLE

        val cx = holder.itemViewRowItemBinding.selectionImage.measuredWidth / 2
        val cy = holder.itemViewRowItemBinding.selectionImage.measuredHeight / 2
        val iconAnim = createCircularReveal(holder.itemViewRowItemBinding.selectionImage, cx, cy,
                0f,
                (Math.max(holder.itemViewRowItemBinding.selectionImage.width,
                        holder.itemViewRowItemBinding.selectionImage.height) / 2).toFloat())
        iconAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                addSourceAnimationsMap.remove(holder)
                dispatchChangeFinishedIfAllAnimationsEnded(holder)
            }
        })
        holder.itemViewRowItemBinding.selectionImage.visibility = View.VISIBLE
        animatorSet.play(overlayAnim).with(iconAnim)
        animatorSet.start()

        addSourceAnimationsMap.put(holder, animatorSet)
    }

    override fun animateRemoveSource(holder: SourcesGridAdapter.SourcesGridViewHolder) {
        val animatorSet = AnimatorSet()
        val hideOverlayAnim = createHideAnimation(holder.itemViewRowItemBinding.overlayView)
        val hideSelectionImageAnim = createHideAnimation(holder.itemViewRowItemBinding.selectionImage)
        hideSelectionImageAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                removeSourceAnimationsMap.remove(holder)
            }
        })
        animatorSet.play(hideOverlayAnim).with(hideSelectionImageAnim)
        animatorSet.start()

        removeSourceAnimationsMap.put(holder, animatorSet)
    }
}