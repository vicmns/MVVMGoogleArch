package com.lonelystudios.palantir.ui.sources

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewAnimationUtils

/**
 * Created by vicmns on 11/8/17.
 */
abstract class SourceCommonItemAnimator<in T : CommonSourceAdapter<T>.SourcesViewHolder> : DefaultItemAnimator() {

    var addSourceAnimationsMap: MutableMap<RecyclerView.ViewHolder, AnimatorSet> = HashMap()
    var removeSourceAnimationsMap: MutableMap<RecyclerView.ViewHolder, AnimatorSet> = HashMap()

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun recordPreLayoutInformation(state: RecyclerView.State,
                                            viewHolder: RecyclerView.ViewHolder,
                                            changeFlags: Int, payloads: List<Any>): ItemHolderInfo {
        if (changeFlags == RecyclerView.ItemAnimator.FLAG_CHANGED) {
            payloads.filterIsInstance<String>().forEach { return FeedItemHolderInfo(it) }
        }

        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
    }

    override fun animateChange(oldHolder: RecyclerView.ViewHolder,
                               newHolder: RecyclerView.ViewHolder,
                               preInfo: ItemHolderInfo,
                               postInfo: ItemHolderInfo): Boolean {

        cancelCurrentAnimationIfExists(newHolder)
        if (preInfo is FeedItemHolderInfo) {
            val holder = newHolder as T
            when(preInfo.updateAction) {
                CommonSourceAdapter.ACTION_ADD_SOURCE -> animateAddSource(holder)
                CommonSourceAdapter.ACTION_REMOVE_SOURCE -> animateRemoveSource(holder)
            }

        }

        return false
    }

    abstract fun animateAddSource(holder: T)
    abstract fun animateRemoveSource(holder: T)

    protected fun createCircularReveal(view: View, cX: Int, cY: Int, startRadius: Float, endRadius: Float): Animator {
        return ViewAnimationUtils.createCircularReveal(view, cX, cY,
                startRadius, endRadius)
    }

    protected fun createHideAnimation(view: View): ObjectAnimator {
        val alpha = ObjectAnimator.ofFloat<View>(view,
                View.ALPHA, 1f, 0f)
        alpha.setAutoCancel(true)
        alpha.addListener(object : AnimatorListenerAdapter() {
            private var isCanceled: Boolean = false

            override fun onAnimationStart(anim: Animator) {
            }

            override fun onAnimationCancel(anim: Animator) {
                isCanceled = true
            }

            override fun onAnimationEnd(anim: Animator) {
                if (!isCanceled) {
                    view.alpha = 1f
                    view.visibility = View.GONE
                }
            }
        })

        return alpha
    }

    protected fun dispatchChangeFinishedIfAllAnimationsEnded(holder: T) {
        if (addSourceAnimationsMap.containsKey(holder) || removeSourceAnimationsMap.containsKey(holder)) {
            return
        }

        dispatchAnimationFinished(holder)
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        super.endAnimation(item)
        cancelCurrentAnimationIfExists(item)
    }

    private fun cancelCurrentAnimationIfExists(item: RecyclerView.ViewHolder) {
        if (addSourceAnimationsMap.containsKey(item)) {
            addSourceAnimationsMap[item]?.cancel()
        }
        if(removeSourceAnimationsMap.containsKey(item)) {
            removeSourceAnimationsMap[item]?.cancel()
        }
    }

    override fun endAnimations() {
        super.endAnimations()
        for (animatorSet in addSourceAnimationsMap.values) {
            animatorSet.cancel()
        }
    }

    class FeedItemHolderInfo(var updateAction: String) : ItemHolderInfo()
}