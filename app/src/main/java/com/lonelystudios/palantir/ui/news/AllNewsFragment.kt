package com.lonelystudios.palantir.ui.news

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lonelystudios.palantir.R
import com.lonelystudios.palantir.databinding.FragmentAllNewsListBinding
import com.lonelystudios.palantir.di.Injectable
import com.lonelystudios.palantir.vo.sources.Article
import com.lonelystudios.palantir.vo.sources.Source
import com.lonelystudios.palantir.vo.sources.Status
import javax.inject.Inject

/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnNewsItemInteraction]
 * interface.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class AllNewsFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var fragmentViewModel: AllNewsFragmentViewModel
    private var mColumnCount = 1
    private var listener: OnNewsItemInteraction? = null
    private lateinit var binding: FragmentAllNewsListBinding
    private lateinit var adapter: AllNewsRecyclerViewAdapter
    private var isArticlesWSCalled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mColumnCount = arguments.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_all_news_list, container, false)
        fragmentViewModel = ViewModelProviders.of(this, viewModelFactory).get(AllNewsFragmentViewModel::class.java)
        initAdapter()
        return binding.root
    }

    private fun initAdapter() {
        adapter = AllNewsRecyclerViewAdapter(listener)
        if (mColumnCount <= 1) {
            binding.list.layoutManager = LinearLayoutManager(context)
        } else {
            binding.list.layoutManager = GridLayoutManager(context, mColumnCount)
        }
        binding.list.adapter = adapter
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachObservers()
        setViewsListeners()
        showLoadingIndicator()
        getUserSelectedSources()
    }

    private fun attachObservers() {
        fragmentViewModel.sourcesLiveData.observe(this, Observer {
            if(it != null ) {
                if(it.isNotEmpty()) {
                    fragmentViewModel.userSources = it
                    getAllArticlesBySources(it)
                    if(binding.progressLayout.isEmpty) {
                        binding.progressLayout.showContent()
                    }
                } else {
                    binding.progressLayout.showEmpty(R.drawable.ic_view_list_black_24dp,
                            "No news to show",
                            "There's no selected sources to pull news, please add new sources using the FAB icon.")
                    hideLoadingIndicator()
                }
            }
        })

        fragmentViewModel.articlesLiveData.observe(this, Observer {
            if(it != null) {
                when(it.status) {
                    Status.SUCCESS -> {
                        isArticlesWSCalled = false
                        updateAdapterList(it.data?.articles?.toMutableList())
                        hideLoadingIndicator()
                    }
                    Status.LOADING -> {
                        //Do nothing
                    }
                    else -> {
                        isArticlesWSCalled = false
                        hideLoadingIndicator()
                    }
                }
            }
        })
    }

    private fun setViewsListeners() {
        binding.listSwipeRefresh.setOnRefreshListener {
            if(isArticlesWSCalled) return@setOnRefreshListener

            if(fragmentViewModel.userSources.isNotEmpty()) {
                getAllArticlesBySources(fragmentViewModel.userSources)
            } else {
                hideLoadingIndicator()
            }
        }
    }

    private fun showLoadingIndicator() {
        binding.listSwipeRefresh.isRefreshing = true
    }

    private fun hideLoadingIndicator() {
        binding.listSwipeRefresh.isRefreshing = false
    }

    private fun getUserSelectedSources() {
        fragmentViewModel.getSelectedSources()
    }

    private fun getAllArticlesBySources(sources: List<Source>) {
        isArticlesWSCalled = true
        fragmentViewModel.getAllArticlesFromSelectedSources(sources)
    }

    private fun updateAdapterList(articles: MutableList<Article>?) {
        if(articles != null) {
            adapter.updateArticles(articles)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnNewsItemInteraction) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnNewsItemInteraction")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun updateNewsList() {
        showLoadingIndicator()
        getUserSelectedSources()
    }


    companion object {
        val TAG = "AllNewsFragment"
        private val ARG_COLUMN_COUNT = "column-count"

        fun newInstance(columnCount: Int): AllNewsFragment {
            val fragment = AllNewsFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}
