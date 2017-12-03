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
import android.support.customtabs.CustomTabsSession
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
        OnNewsItemInteraction, NewsDashboardFragment.OnNewsDashboardFragmentInteractionListener {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var fragmentManager: FragmentManager
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var binding: ActivityNewsBinding
    var customTabsSession: CustomTabsSession? = null
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
                if(fragmentManager.backStackEntryCount > 0) {
                    showNewsBySourceFragment()
                }
                else {

                    setNewsDashboard()
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private val connection: CustomTabsServiceConnection = object : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
            customTabsClient = client
            customTabsClient?.warmup(0L)
            customTabsSession = customTabsClient?.newSession(null)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            customTabsClient = null
            customTabsSession = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(NewsActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news)
        binding.handler = handler
        setSupportActionBar(binding.toolbar)
        attachListeners()
        setFragment()
        val packageName = CustomTabsHelper.getPackageNameToUse(this)
        CustomTabsClient.bindCustomTabsService(this, packageName, connection)
    }

    private fun attachListeners() {
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        fragmentManager.addOnBackStackChangedListener {
            val count = fragmentManager.backStackEntryCount
            if(count == 0) {
                when(viewModel.currentFragment) {
                    NewsDashboardNewsBySourceFragment.TAG ->
                        setNewsDashboard()
                }
            }
        }
    }

    private fun setFragment() {
        when(viewModel.currentFragment) {
            AllNewsFragment.TAG -> setAllNewsFragment()
            NewsDashboardFragment.TAG -> setNewsDashboard()
            NewsDashboardNewsBySourceFragment.TAG -> showNewsBySourceFragment()
        }
    }

    private fun setAllNewsFragment() {
        var fragment = fragmentManager.findFragmentByTag(AllNewsFragment.TAG)
        viewModel.currentFragment = AllNewsFragment.TAG
        binding.toolbar.setTitle(R.string.app_name)
        if (fragment == null) {
            fragment = AllNewsFragment.newInstance(1)
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(binding.fragmentContainer.id, fragment, AllNewsFragment.TAG)
            fragmentTransaction.commit()
        } else {
        }
        showFragment(fragment)
    }

    private fun setNewsDashboard() {
        var fragment = fragmentManager.findFragmentByTag(NewsDashboardFragment.TAG)
        viewModel.currentFragment = NewsDashboardFragment.TAG
        binding.toolbar.setTitle(R.string.app_name)
        if (fragment == null) {
            fragment = NewsDashboardFragment.newInstance()
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(binding.fragmentContainer.id, fragment, NewsDashboardFragment.TAG)
            fragmentTransaction.commit()
        }
        showFragment(fragment)
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
        val builder = CustomTabsIntent.Builder(customTabsSession)
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
            data?.takeIf { it.getBooleanExtra(SourcesActivity.UPDATE_NEWS_RESPONSE_BUNDLE, false) }?.apply {
                refreshFragments()
            }
        }
    }

    private fun refreshFragments() {
        val currentFragment = fragmentManager.findFragmentByTag(NewsDashboardNewsBySourceFragment.TAG)
        if(currentFragment != null) {
            fragmentManager.popBackStack()
        }
        for (cFragment in fragmentManager.fragments) {
            when(cFragment) {
                is AllNewsFragment -> {cFragment.updateNewsList()}
                is NewsDashboardFragment -> {
                    cFragment.updateUserSources()
                }
            }
        }
    }

    override fun onBackPressed() {
        when (viewModel.currentFragment) {
            NewsDashboardFragment.TAG -> {
                if(fragmentManager.backStackEntryCount > 0) {
                    super.onBackPressed()
                }
                else {
                    val allNewsFragment = fragmentManager.findFragmentByTag(AllNewsFragment.TAG)
                    if(allNewsFragment != null) showFragment(allNewsFragment)
                    navigation.selectedItemId = R.id.navigation_home
                }
            }
            AllNewsFragment.TAG -> {
                finish()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onSourceSelected(sources: Source) {
        showNewsBySource(sources)
    }

    private fun showNewsBySource(source: Source) {
        var fragment = fragmentManager.findFragmentByTag(NewsDashboardNewsBySourceFragment.TAG)
        viewModel.currentFragment = NewsDashboardNewsBySourceFragment.TAG
        if (fragment == null) {
            fragment = NewsDashboardNewsBySourceFragment.newInstance(source)
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(binding.fragmentContainer.id, fragment, NewsDashboardNewsBySourceFragment.TAG)
            fragmentTransaction.addToBackStack(NewsDashboardNewsBySourceFragment.TAG)
            fragmentTransaction.commit()
            showFragment(fragment)
        } else {
            showFragment(fragment)
        }
    }

    private fun showNewsBySourceFragment() {
        val fragment = fragmentManager.findFragmentByTag(NewsDashboardNewsBySourceFragment.TAG)
        viewModel.currentFragment = NewsDashboardNewsBySourceFragment.TAG
        if(fragment != null) {
            (fragment as NewsDashboardNewsBySourceFragment).setToolbarTitle()
            showFragment(fragment)
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector

    interface NewsActivityHandlers {
        fun onOpenSourcesClick()
    }

    companion object {
        val UPDATE_NEWS_REQUEST_CODE = "UPDATE_NEWS_REQUEST_CODE".hashCode().and(0x0f)
    }
}
