package com.lonelystudios.palantir.ui.sources

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.lonelystudios.palantir.ui.SourceItemViewModel
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.sources.Source
import com.lonelystudios.palantir.vo.sources.Status
import timber.log.Timber
import java.util.*

/**
 * Created by vicmns on 10/30/17.
 */
abstract class CommonSourceAdapter<T : CommonSourceAdapter<T>.SourcesViewHolder>(
        protected val activity: SourcesActivity,
        private val viewModelFactory: ViewModelProvider.Factory) : RecyclerView.Adapter<T>() {

    val random = Random()
    var sourcesList: MutableList<Source> = ArrayList()
        set(value) {
            field = value
            value.forEachIndexed { index, source -> sourcesListIdToPositionsMap[source.id] = index }
        }
    var sourcesListIdToPositionsMap: HashMap<String, Int> = HashMap()
    var sourcesLogoCallsMap: MutableList<Int> = ArrayList()
    private var animationsLocked = false
    private var delayEnterAnimation = true
    private var lastAnimatedPosition = -1
    private var recyclerView: RecyclerView? = null

    override fun onBindViewHolder(holder: T, position: Int) {
        //runEnterAnimation(holder.itemView, position)
        Timber.d("onBindViewHolder: positon: %d", position)
        val item = sourcesList[position]
        val viewModel: SourceItemViewModel =
                ViewModelProviders.of(activity, viewModelFactory).get(item.id, SourceItemViewModel::class.java)
        holder.bindRow(sourcesList[position])
        if(!sourcesLogoCallsMap.contains(position)) {
            if (item.urlToLogo.isNullOrEmpty() && item.isUrlLogoAvailable) {
                Timber.d("Url logo not available for item: %d", position)
                viewModel.sourcesLiveData.observe(activity, object : Observer<Resource<Source>?> {
                    override fun onChanged(resource: Resource<Source>?) {
                        if (resource != null) {
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    if (resource.data != null) {
                                        viewModel.sourcesLiveData.removeObserver(this)
                                        val cPosition = holder.adapterPosition
                                        if(cPosition >= 0) {
                                            sourcesLogoCallsMap.remove(cPosition)
                                            sourcesList[cPosition].urlToLogo = resource.data.urlToLogo
                                            sourcesList[cPosition].isUrlLogoAvailable = resource.data.isUrlLogoAvailable
                                            recyclerView?.post({
                                                notifyItemChanged(cPosition, UPDATE_SOURCE_LOGO)
                                            })
                                        }
                                    }
                                }
                                Status.CANCEL, Status.ERROR -> {
                                    viewModel.sourcesLiveData.removeObserver(this)
                                    val cPosition = holder.adapterPosition
                                    if(cPosition >= 0) {
                                        sourcesLogoCallsMap.remove(cPosition)
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                })
                viewModel.getLogoInfo(sourcesList[position])
                sourcesLogoCallsMap.add(position)
            }
        }
    }

    private fun runEnterAnimation(view: View?, position: Int) {
        if (animationsLocked) return

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position
            view?.translationY = 100f
            view?.alpha = 0f
            view?.animate()
                    ?.translationY(0f)?.alpha(1f)
                    ?.setStartDelay(if (delayEnterAnimation) (20 * position).toLong() else 0)
                    ?.setInterpolator(DecelerateInterpolator(2f))
                    ?.setDuration(300)
                    ?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                            animationsLocked = true
                            super.onAnimationEnd(animation, isReverse)
                        }
                    })
                    ?.start()
        }
    }

    override fun getItemCount(): Int {
        return sourcesList.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return sourcesList[position].id.hashCode().toLong()
    }

    inner abstract class SourcesViewHolder(itemViewRowItemBinding: ViewDataBinding) :
            RecyclerView.ViewHolder(itemViewRowItemBinding.root), HolderHandlers {

        abstract fun bindRow(source: Source)

        override fun onClick(view: View) {
            activity.onCardClicked(adapterPosition, sourcesList[adapterPosition])
        }
    }

    interface HolderHandlers {
        fun onClick(view: View)
    }

    interface AdapterHandlers {
        fun onCardClicked(position: Int, source: Source)
    }

    companion object {
        val UPDATE_SOURCE_LOGO = "update_source_logo"
    }
}