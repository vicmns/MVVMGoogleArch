package com.lonelystudios.palantir.ui.news

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsServiceConnection
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.lonelystudios.palantir.R
import com.lonelystudios.palantir.databinding.ActivityNewsBinding
import com.lonelystudios.palantir.ui.sources.SourcesActivity
import com.lonelystudios.palantir.utils.customtabs.CustomTabsHelper
import com.lonelystudios.palantir.vo.sources.Article
import com.lonelystudios.palantir.vo.sources.Source
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_news.*
import javax.inject.Inject


class NewsActivity : AppCompatActivity(), HasSupportFragmentInjector,
        AllNewsFragment.OnNewsItemInteraction, NewsDashboardFragment.OnNewsDashboardFragmentInteractionListener {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var fragmentManager: FragmentManager
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var binding: ActivityNewsBinding
    private lateinit var viewModel: NewsActivityViewModel
    private var customTabsClient: CustomTabsClient? = null
    private val handler = object : NewsActivityHandlers {
        override fun onOpenSourcesClick() {
            val intent = Intent(this@NewsActivity, SourcesActivity::class.java)
            startActivityForResult(intent, UPDATE_NEWS_REQUEST_CODE)
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                setAllNewsFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                setNewsDashboard()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    var connection: CustomTabsServiceConnection = object : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
            customTabsClient = client
            customTabsClient?.warmup(0L)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            customTabsClient = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NewsActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news)
        binding.handler = handler
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        setAllNewsFragment()
    }

    private fun setAllNewsFragment() {
        var fragment = fragmentManager.findFragmentByTag(AllNewsFragment.TAG)
        viewModel.currentFragment = AllNewsFragment.TAG
        if (fragment == null) {
            fragment = AllNewsFragment.newInstance(1)
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(binding.fragmentContainer.id, fragment, AllNewsFragment.TAG)
            //fragmentTransaction.addToBackStack(AllNewsFragment.TAG)
            fragmentTransaction.commit()
        } else {
            showFragment(fragment)
        }
    }

    private fun setNewsDashboard() {
        var fragment = fragmentManager.findFragmentByTag(NewsDashboardFragment.TAG)
        viewModel.currentFragment = NewsDashboardFragment.TAG
        if (fragment == null) {
            fragment = NewsDashboardFragment.newInstance()
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(binding.fragmentContainer.id, fragment, NewsDashboardFragment.TAG)
            fragmentTransaction.commit()
            showFragment(fragment)
        } else {
            showFragment(fragment)
        }
    }

    private fun showFragment(fragment: Fragment) {
        val ft = fragmentManager.beginTransaction()
        for (cFragment in fragmentManager.fragments) {
            ft.hide(cFragment)
        }
        ft.show(fragment)
        ft.commit()

    }

    override fun onListFragmentInteraction(item: Article) {
        val builder = CustomTabsIntent.Builder()
        //builder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left)
        //builder.setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right)
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        openCustomTab(this, builder.build(), Uri.parse(item.url))
    }

    private fun openCustomTab(activity: Activity,
                              customTabsIntent: CustomTabsIntent,
                              uri: Uri) {
        val packageName = CustomTabsHelper.getPackageNameToUse(activity)

        //If we cant find a package name, it means theres no browser that supports
        //Chrome Custom Tabs installed. So, we fallback to the webview
        if (packageName == null) {
            openDefaultUri(activity, uri)
        } else {
            customTabsIntent.intent.`package` = packageName
            customTabsIntent.launchUrl(activity, uri)
        }
    }

    private fun openDefaultUri(activity: Activity, uri: Uri) {
        val intent = Intent(activity, WebviewActivity::class.java)
        intent.putExtra(WebviewActivity.EXTRA_URL, uri.toString())
        activity.startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_NEWS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.takeIf { it.getBooleanExtra(SourcesActivity.UPDATE_NEWS_RESPONSE_BUNDLE, false) }.apply {
                refreshNewsItems()
            }
        }
    }

    private fun refreshNewsItems() {
        val currentFragment = fragmentManager.findFragmentByTag(AllNewsFragment.TAG)
        if (currentFragment is AllNewsFragment) {
            currentFragment.updateNewsList()
        }
    }

    override fun onBackPressed() {
        when (viewModel.currentFragment) {
            NewsDashboardFragment.TAG -> {
                val allNewsFragment = fragmentManager.findFragmentByTag(AllNewsFragment.TAG)
                if(allNewsFragment != null) showFragment(allNewsFragment)
                navigation.selectedItemId = R.id.navigation_home
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onSourceSelected(sources: Source) {

    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector

    interface NewsActivityHandlers {
        fun onOpenSourcesClick()
    }

    companion object {
        val UPDATE_NEWS_REQUEST_CODE = "UPDATE_NEWS_REQUEST_CODE".hashCode().and(0x0f)
    }
}
