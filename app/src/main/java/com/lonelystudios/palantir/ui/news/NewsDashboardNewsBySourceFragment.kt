package com.lonelystudios.palantir.ui.news

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.SupportActivity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lonelystudios.palantir.R
import com.lonelystudios.palantir.databinding.FragmentNewsDashboardNewsBySourceBinding
import com.lonelystudios.palantir.di.Injectable
import com.lonelystudios.palantir.vo.sources.Article
import com.lonelystudios.palantir.vo.sources.Source
import com.lonelystudios.palantir.vo.sources.Status
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NewsDashboardNewsBySourceFragment.OnNewsDashboardNewsBySourceFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [NewsDashboardNewsBySourceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewsDashboardNewsBySourceFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var binding: FragmentNewsDashboardNewsBySourceBinding
    private lateinit var fragmentViewModel: NewsDashboardNewsBySourceFragmentViewModel
    private var listener: OnNewsItemInteraction? = null
    private lateinit var adapter: AllNewsRecyclerViewAdapter
    private lateinit var source: Source

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.takeIf { arguments.containsKey(SOURCE_ARGUMENT) }?.apply {
            source = arguments.getParcelable(SOURCE_ARGUMENT)
        }
        savedInstanceState?.takeIf { savedInstanceState.containsKey(SOURCE_ARGUMENT) }?.apply {
            source = savedInstanceState.getParcelable(SOURCE_ARGUMENT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_news_dashboard_news_by_source, container, false)
        fragmentViewModel = ViewModelProviders.of(this, viewModelFactory).get(NewsDashboardNewsBySourceFragmentViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle()
        initList()
        attachObservers()
        showProgressIndicator()
        getNewsBySource()
    }

    fun setToolbarTitle() {
        (activity as AppCompatActivity).supportActionBar?.title = source.name
    }

    private fun initList() {
        adapter = AllNewsRecyclerViewAdapter(listener)
        binding.newsSourcesList.layoutManager = LinearLayoutManager(activity)
        binding.newsSourcesList.adapter = adapter
    }

    private fun attachObservers() {
        fragmentViewModel.articlesLiveData.observe(this, Observer { response ->
            response?.takeIf { it.status != Status.LOADING }?.apply {
                if(response.status == Status.SUCCESS) {
                    updateNewsArticles(response.data?.articles)
                    hideProgressIndicator()
                } else {
                    hideProgressIndicator()
                }
            }

        })
    }

    private fun getNewsBySource() {
        fragmentViewModel.getAllArticlesFromSelectedSources(source)
    }

    private fun showProgressIndicator() {
        binding.listSwipeRefresh.isRefreshing = true
    }

    private fun hideProgressIndicator() {
        binding.listSwipeRefresh.isRefreshing = false
    }

    private fun updateNewsArticles(articles: List<Article>?) {
        articles?.takeIf { articles.isNotEmpty() }?.apply {
            adapter.updateArticles(articles.toMutableList())
            adapter.notifyDataSetChanged()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnNewsItemInteraction) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnNewsDashboardNewsBySourceFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(SOURCE_ARGUMENT, source)
    }

    companion object {
        val TAG = "NewsDashboardNewsBySourceFragment"
        val SOURCE_ARGUMENT = "source_argument"

        fun newInstance(source: Source): NewsDashboardNewsBySourceFragment {
            val fragment = NewsDashboardNewsBySourceFragment()
            val arguments = Bundle()
            arguments.putParcelable(SOURCE_ARGUMENT, source)
            fragment.arguments = arguments
            return fragment
        }
    }
}
