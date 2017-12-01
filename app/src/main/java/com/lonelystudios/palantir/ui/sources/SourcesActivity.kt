package com.lonelystudios.palantir.ui.sources

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import com.lonelystudios.palantir.R
import com.lonelystudios.palantir.databinding.ActivitySourcesBinding
import com.lonelystudios.palantir.utils.GridSpacingItemDecoration
import com.lonelystudios.palantir.vo.Resource
import com.lonelystudios.palantir.vo.sources.Source
import com.lonelystudios.palantir.vo.sources.Status
import timber.log.Timber
import javax.inject.Inject


class SourcesActivity : AppCompatActivity(), CommonSourceAdapter.AdapterHandlers {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var sourcesViewModel: SourcesViewModel
    private lateinit var detailBinding: ActivitySourcesBinding

    private lateinit var sourceGridAdapter: SourcesGridAdapter
    private lateinit var sourceListAdapter: SourcesListAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var listLayoutManager: LinearLayoutManager
    private lateinit var gridItemDecoration: GridSpacingItemDecoration
    private lateinit var gridItemAnimator: SourcesGridItemAnimator
    private lateinit var listItemAnimator: SourcesListItemAnimator
    private lateinit var gridItemsAnimator: LayoutAnimationController
    private lateinit var listItemsAnimator: LayoutAnimationController

    private var isGridMode = true
    private var hasSourcesChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_sources)
        sourcesViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(SourcesViewModel::class.java)
        setSupportActionBar(detailBinding.toolbar)
        initItemAnimators()
        initRecyclerView()
        initAdapters()
        initLayoutManagers()
        initItemDecorators()
        initSourcesRecyclerView()
        attachObservers()
        sourcesViewModel.getAllSources()
    }

    private fun initItemAnimators() {
        gridItemsAnimator = AnimationUtils.loadLayoutAnimation(this, R.anim.grid_layout_animation_from_bottom)
        gridItemAnimator = SourcesGridItemAnimator()
        gridItemAnimator.supportsChangeAnimations = false
        listItemsAnimator = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_bottom)
        listItemAnimator = SourcesListItemAnimator()
        listItemAnimator.supportsChangeAnimations = false
    }

    private fun initRecyclerView() {
        detailBinding.contentRecyclerView.setHasFixedSize(true)
    }

    private fun initAdapters() {
        sourceGridAdapter = SourcesGridAdapter(this, viewModelFactory)
        sourceListAdapter = SourcesListAdapter(this, viewModelFactory)
    }

    private fun initLayoutManagers() {
        gridLayoutManager = GridLayoutManager(this, 2)
        listLayoutManager = LinearLayoutManager(this)
    }

    private fun initItemDecorators() {
        gridItemDecoration = GridSpacingItemDecoration(2, resources
                .getDimensionPixelOffset(R.dimen.source_grid_item_inset), true, 0)
    }

    private fun initSourcesRecyclerView() {
        setGridAdapter()
    }

    private fun setGridAdapter() {
        detailBinding.contentRecyclerView.itemAnimator = gridItemAnimator
        detailBinding.contentRecyclerView.adapter = sourceGridAdapter
        detailBinding.contentRecyclerView.layoutManager = gridLayoutManager
        detailBinding.contentRecyclerView.addItemDecoration(gridItemDecoration)


        detailBinding.contentRecyclerView.layoutAnimation = gridItemsAnimator
        detailBinding.contentRecyclerView.scheduleLayoutAnimation()
    }

    private fun setListAdapter() {
        detailBinding.contentRecyclerView.itemAnimator = listItemAnimator
        detailBinding.contentRecyclerView.removeItemDecoration(gridItemDecoration)
        detailBinding.contentRecyclerView.adapter = sourceListAdapter
        detailBinding.contentRecyclerView.layoutManager = listLayoutManager

        detailBinding.contentRecyclerView.layoutAnimation = listItemsAnimator
        detailBinding.contentRecyclerView.scheduleLayoutAnimation()
    }

    private fun attachObservers() {
        sourcesViewModel.sourcesLiveData.observe(this, Observer<Resource<List<Source>>> { resources ->

            if (resources != null) {
                when (resources.status) {
                    Status.SUCCESS -> {
                        sourcesViewModel.sourcesLiveData.removeObservers(this)
                        setDataToAdapters(resources.data)
                        hideLoadingIndicator()
                    }
                    Status.ERROR -> {
                        sourcesViewModel.sourcesLiveData.removeObservers(this)
                        hideLoadingIndicator()
                    }
                    Status.LOADING -> {
                        showLoadingIndicator()
                    }
                    Status.CANCEL -> {
                        sourcesViewModel.sourcesLiveData.removeObservers(this)
                        hideLoadingIndicator()
                    }
                }
            }
        })
    }

    private fun showLoadingIndicator() {
        detailBinding.contentSwipeRefreshLayout.isRefreshing = true
    }

    private fun hideLoadingIndicator() {
        detailBinding.contentSwipeRefreshLayout.isRefreshing = false
    }

    private fun setDataToAdapters(sources: List<Source>?) {
        sourceGridAdapter.sourcesList = sources?.toMutableList() ?: ArrayList()
        sourceListAdapter.sourcesList = sources?.toMutableList() ?: ArrayList()
        if (isGridMode) sourceGridAdapter.notifyDataSetChanged()
        else sourceListAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_sources, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        val gridModeIcon = menu?.findItem(R.id.action_show_grid)
        val listModeIcon = menu?.findItem(R.id.action_show_list)
        gridModeIcon?.isVisible = !isGridMode
        listModeIcon?.isVisible = isGridMode
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_show_grid -> {
                isGridMode = true
                invalidateOptionsMenu()
                setGridAdapter()
                true
            }
            R.id.action_show_list -> {
                isGridMode = false
                invalidateOptionsMenu()
                setListAdapter()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCardClicked(position: Int, source: Source) {
        sourcesViewModel.updateSelectedSource(source)
        hasSourcesChanged = true
        Timber.d("Item clicked: %d", position)
    }

    override fun finish() {
        val resultIntent = Intent()
        resultIntent.putExtra(UPDATE_NEWS_RESPONSE_BUNDLE, hasSourcesChanged)
        setResult(Activity.RESULT_OK, resultIntent)
        super.finish()
    }

    companion object {
        val UPDATE_NEWS_RESPONSE_BUNDLE = "update_news_response_bundle"
    }
}
