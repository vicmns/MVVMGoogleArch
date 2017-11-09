package com.lonelystudios.palantir.ui.sources

import android.arch.lifecycle.ViewModelProvider
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lonelystudios.palantir.databinding.AdapterSourcesGridRowItemBinding
import com.lonelystudios.palantir.utils.DataBindingAdapters
import com.lonelystudios.palantir.vo.sources.Source
import timber.log.Timber


/**
 * Created by vicmns on 10/27/17.
 */
class SourcesGridAdapter(activity: SourcesActivity,
                         viewModelFactory: ViewModelProvider.Factory) :
        CommonSourceAdapter<SourcesGridAdapter.SourcesGridViewHolder>(activity, viewModelFactory) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SourcesGridViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val itemRowBinding = AdapterSourcesGridRowItemBinding.inflate(layoutInflater, parent, false)
        return SourcesGridViewHolder(itemRowBinding)
    }

    override fun onBindViewHolder(holder: SourcesGridViewHolder?, position: Int, payloads: MutableList<Any>?) {
        if(payloads != null && payloads.contains(UPDATE_SOURCE_LOGO)) {
               DataBindingAdapters.setSourceLogo(holder?.itemViewRowItemBinding?.sourceLogo,
                       sourcesList[position])
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    inner class SourcesGridViewHolder(val itemViewRowItemBinding: AdapterSourcesGridRowItemBinding) :
            SourcesViewHolder(itemViewRowItemBinding) {

        override fun bindRow(source: Source) {
            itemViewRowItemBinding.source = source
            itemViewRowItemBinding.handlers = this
        }
    }

}