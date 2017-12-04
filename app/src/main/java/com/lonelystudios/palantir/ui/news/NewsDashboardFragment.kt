package com.lonelystudios.palantir.ui.news

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lonelystudios.palantir.R
import com.lonelystudios.palantir.databinding.FragmentNewsSourcesBinding
import com.lonelystudios.palantir.di.Injectable
import com.lonelystudios.palantir.vo.sources.Source
import timber.log.Timber
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NewsDashboardFragment.OnNewsDashboardFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NewsDashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewsDashboardFragment : Fragment(), Injectable, NewsDashboardSourcesAdapter.AdapterHandlers {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var fragmentViewModel: NewsDashboardFragmentViewModel
    private lateinit var binding: FragmentNewsSourcesBinding
    private lateinit var adapter: NewsDashboardSourcesAdapter
    private var listener: OnNewsDashboardFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_news_sources, container, false)
        fragmentViewModel = ViewModelProviders.of(this, viewModelFactory).get(NewsDashboardFragmentViewModel::class.java)
        initList()
        return binding.root
    }

    private fun initList() {
        adapter = NewsDashboardSourcesAdapter(activity, viewModelFactory, this)
        binding.newsSourcesList.layoutManager = LinearLayoutManager(context)
        binding.newsSourcesList.adapter = adapter
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachObservers()
        attachListeners()
        updateSources()
        getUserSources()
    }

    private fun attachObservers() {
        fragmentViewModel.sourcesLiveData.observe(this, Observer { sources ->
            if(sources != null && sources.isNotEmpty()) {
                fragmentViewModel.sources = sources
                updateSources()
                hideProgressIndicator()
                if(binding.progressLayout.isEmpty) {
                    binding.progressLayout.showContent()
                }
            } else {
                hideProgressIndicator()
                binding.progressLayout.showEmpty(R.drawable.ic_view_list_black_24dp,
                        "No news to show",
                        "There's no selected sources to pull news, please add new sources using the FAB icon.")
            }
        })
    }

    private fun attachListeners() {
        binding.listSwipeRefresh.setOnRefreshListener {
            getUserSources()
        }
    }

    private fun updateSources() {
        adapter.sourcesList = fragmentViewModel.sources.toMutableList()
        adapter.notifyDataSetChanged()
    }

    private fun getUserSources() {
        showProgressIndicator()
        fragmentViewModel.getSelectedSources()
    }

    private fun showProgressIndicator() {
        binding.listSwipeRefresh.isRefreshing =  true
    }

    private fun hideProgressIndicator() {
        binding.listSwipeRefresh.isRefreshing =  false
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnNewsDashboardFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnNewsDashboardFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCardClicked(position: Int, source: Source) {
        listener?.onSourceSelected(source)
    }

    fun updateUserSources() {
        showProgressIndicator()
        getUserSources()
    }


    interface OnNewsDashboardFragmentInteractionListener {
        fun onSourceSelected(sources: Source)
    }

    companion object {
        val TAG = "NewsDashboard"
        fun newInstance(): NewsDashboardFragment {
            val fragment = NewsDashboardFragment()
            return fragment
        }
    }
}