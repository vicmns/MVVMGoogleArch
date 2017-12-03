package com.lonelystudios.palantir.ui.news

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.OnRebindCallback
import android.databinding.ViewDataBinding
import android.support.transition.TransitionManager
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lonelystudios.palantir.databinding.FragmentNewsDashboardSourcesItemBinding
import com.lonelystudios.palantir.ui.SourceItemViewModel
import com.lonelystudios.palantir.ui.sources.CommonSourceAdapter
import com.lonelystudios.palantir.utils.DataBindingAdapters
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.sources.Source
import com.lonelystudios.palantir.vo.sources.Status
import timber.log.Timber
import java.util.*

/**
 * Created by vicmns on 12/1/17.
 */
class NewsDashboardSourcesAdapter(protected val activity: FragmentActivity,
                                  private val viewModelFactory: ViewModelProvider.Factory,
                                  private val adapterHandlers:
                                  NewsDashboardSourcesAdapter.AdapterHandlers) :
        RecyclerView.Adapter<NewsDashboardSourcesAdapter.ViewHolder>() {
    var sourcesList: MutableList<Source> = ArrayList()
        set(value) {
            field = value
            value.forEachIndexed { index, source -> sourcesListIdToPositionsMap[source.id] = index }
        }

    var sourcesListIdToPositionsMap: HashMap<String, Int> = HashMap()
    var sourcesLogoCallsMap: MutableList<Int> = ArrayList()
    private var recyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val itemRowBinding = FragmentNewsDashboardSourcesItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(itemRowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val item = sourcesList[position]
        val viewModel: SourceItemViewModel =
                ViewModelProviders.of(activity, viewModelFactory).get(item.id, SourceItemViewModel::class.java)
        holder?.bindRow(sourcesList[position])
        holder?.itemBinding?.addOnRebindCallback(object : OnRebindCallback<ViewDataBinding>() {
            override fun onPreBind(binding: ViewDataBinding?): Boolean {
                TransitionManager.beginDelayedTransition(
                        binding!!.root as ViewGroup)
                return super.onPreBind(binding)
            }
        })
        if (!sourcesLogoCallsMap.contains(position)) {
            if (item.urlToLogo.isNullOrEmpty() && item.isUrlLogoAvailable) {
                Timber.d("Url logo not available for item: %d", position)
                viewModel.sourcesLiveData.observe(activity, object : Observer<Resource<Source>?> {
                    override fun onChanged(resource: Resource<Source>?) {
                        if (resource != null) {
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    if (resource.data != null) {
                                        viewModel.sourcesLiveData.removeObserver(this)
                                        val cPosition = holder?.adapterPosition
                                        if (cPosition != null && cPosition >= 0) {
                                            sourcesLogoCallsMap.remove(cPosition)
                                            sourcesList[cPosition].urlToLogo = resource.data.urlToLogo
                                            sourcesList[cPosition].isUrlLogoAvailable = resource.data.isUrlLogoAvailable
                                            recyclerView?.post({
                                                notifyItemChanged(cPosition, CommonSourceAdapter.UPDATE_SOURCE_LOGO)
                                            })
                                        }
                                    }
                                }
                                Status.CANCEL, Status.ERROR -> {
                                    viewModel.sourcesLiveData.removeObserver(this)
                                    val cPosition = holder?.adapterPosition
                                    if (cPosition != null && cPosition >= 0) {
                                        sourcesLogoCallsMap.remove(cPosition)
                                    }
                                }
                                else -> {
                                }
                            }
                        }
                    }
                })
                viewModel.getLogoInfo(sourcesList[position])
                sourcesLogoCallsMap.add(position)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        if (payloads != null && payloads.contains(UPDATE_SOURCE_LOGO)) {
            DataBindingAdapters.setSourceLogo(holder?.itemBinding?.sourceLogo,
                    sourcesList[position])
        } else {
            super.onBindViewHolder(holder, position, payloads)
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

    inner class ViewHolder(val itemBinding: FragmentNewsDashboardSourcesItemBinding) :
            RecyclerView.ViewHolder(itemBinding.root), NewsDashboardSourcesAdapter.HolderHandlers {

        fun bindRow(source: Source) {
            itemBinding.source = source
            itemBinding.handlers = this
        }

        override fun onClick(view: View) {
            adapterHandlers.onCardClicked(adapterPosition, sourcesList[adapterPosition])
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
        val ACTION_ADD_SOURCE = "action_add_source"
        val ACTION_REMOVE_SOURCE = "action_remove_source"
    }
}