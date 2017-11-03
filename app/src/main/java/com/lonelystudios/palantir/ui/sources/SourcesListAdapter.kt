package com.lonelystudios.palantir.ui.sources

import android.arch.lifecycle.ViewModelProvider
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lonelystudios.palantir.databinding.AdapterSourcesListRowItemBinding
import com.lonelystudios.palantir.utils.DataBindingAdapters
import com.lonelystudios.palantir.vo.sources.Source
import timber.log.Timber

/**
 * Created by vicmns on 10/27/17.
 */
class SourcesListAdapter(activity: SourcesActivity,
                         viewModelFactory: ViewModelProvider.Factory) :
        CommonSourceAdapter<SourcesListAdapter.SourcesListViewHolder>(activity, viewModelFactory) {


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SourcesListViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val itemRowBinding = AdapterSourcesListRowItemBinding.inflate(layoutInflater, parent, false)
        return SourcesListViewHolder(itemRowBinding)
    }



    override fun onBindViewHolder(holder: SourcesListViewHolder?, position: Int, payloads: MutableList<Any>?) {
        if(payloads != null && payloads.contains(UPDATE_SOURCE_LOGO)) {
            Timber.d("Payloads: Updating source logo for position: %d", position)
            DataBindingAdapters.setSourceLogo(holder?.itemViewRowItemBinding?.sourceLogo,
                    sourcesList[position])
        } else {
            Timber.d("Calling general bind view holder on position: %d", position)
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    inner class SourcesListViewHolder(val itemViewRowItemBinding: AdapterSourcesListRowItemBinding) :
            SourcesViewHolder(itemViewRowItemBinding) {

        override fun bindRow(source: Source) {
            itemViewRowItemBinding.source = source
            itemViewRowItemBinding.handlers = this
        }
    }
}