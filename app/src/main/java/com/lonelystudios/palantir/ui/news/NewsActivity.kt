package com.lonelystudios.palantir.ui.news

import android.app.Activity
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
import com.lonelystudios.palantir.utils.customtabs.CustomTabsHelper
import com.lonelystudios.palantir.vo.sources.Article
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_news.*
import javax.inject.Inject


class NewsActivity : AppCompatActivity(), HasSupportFragmentInjector, AllNewsFragment.OnNewsItemInteraction {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var fragmentManager: FragmentManager
    lateinit var binding: ActivityNewsBinding
    private var customTabsClient: CustomTabsClient? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                setAllNewsFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        setAllNewsFragment()

    }

    private fun setAllNewsFragment() {
        var currentFragment = fragmentManager.findFragmentByTag(AllNewsFragment.TAG)
        if(currentFragment == null) {
            currentFragment = AllNewsFragment.newInstance(1)
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(binding.fragmentContainer.id, currentFragment, AllNewsFragment.TAG)
            //fragmentTransaction.addToBackStack(AllNewsFragment.TAG)
            fragmentTransaction.commit()
        } else {
            showFragment(currentFragment)
        }
    }

    fun setNewsDashboard() {

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

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector
}
