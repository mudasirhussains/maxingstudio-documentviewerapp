package com.mudassir.documentviewer

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.navigation.NavigationView
import com.mudassir.documentviewer.bottomnavigation.favorite.FavoriteActivity
import com.mudassir.documentviewer.bottomnavigation.feedback.FeedbackActivity
import com.mudassir.documentviewer.bottomnavigation.home.HomeFragment
import com.mudassir.documentviewer.bottomnavigation.recent.RecentActivity
import com.mudassir.documentviewer.databinding.ActivityMainBinding
import com.mudassir.documentviewer.details.CommonDetailActivity
import com.mudassir.documentviewer.search.SearchActivity
import com.mudassir.documentviewer.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private var exitDialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setDrawer()
        setDefaultFragment(savedInstanceState)
        setDrawerToggle()

        val adRequest = AdRequest.Builder().build()
        val ss = exitDialog?.findViewById<AdView>(R.id.textExitNo)
        ss?.loadAd(adRequest)


        binding.bSheetFavorite.setOnClickListener {
            startActivity(Intent(applicationContext, FavoriteActivity::class.java))
        }
        binding.bSheetFeedback.setOnClickListener {
            startActivity(Intent(applicationContext, FeedbackActivity::class.java))
        }
        binding.bSheetRecent.setOnClickListener {
            startActivity(Intent(applicationContext, RecentActivity::class.java))
        }
    }


    private fun setDrawer() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        binding.toolbar.setTitleTextColor(Color.WHITE)
        binding.toolbar.post(Runnable {
            val d = ResourcesCompat.getDrawable(resources, R.drawable.ic_navigation_icon, null)
            binding.toolbar.navigationIcon = d
        })
        binding.toolbar.overflowIcon!!.setColorFilter(
            ContextCompat.getColor(this, R.color.white),
            PorterDuff.Mode.SRC_ATOP
        )
    }

    private fun setDefaultFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {

            val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(
                R.id.flContent,
                HomeFragment()
            )
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    private fun setDrawerToggle() {
        drawerToggle = setupDrawerToggle()
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()
        binding.drawerLayout.addDrawerListener(drawerToggle)
        setupDrawerContent(binding.navView)
    }

    // to use three dots
    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
              openSearchScreen()
            }
            R.id.action_setting -> {
                openSettings()
            }
        }
        return super.onOptionsItemSelected(item)
    }



    private fun setupDrawerToggle(): ActionBarDrawerToggle {
        return ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            selectDrawerItem(menuItem)
            false
        }
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_drawer_all_files ->
                closeDrawer()
            R.id.nav_drawer_favorite -> {
                openFavorite()
            }
            R.id.nav_drawer_rate_us -> {
                rateUs()
            }
            R.id.nav_drawer_share -> {
                shareUs()
            }
            R.id.nav_drawer_privacy -> {
                policyIntent()
            }
            else ->
                closeDrawer()
        }
        closeDrawer()
    }

    private fun policyIntent() {
        val url = "https://maxingstudio.blogspot.com/2022/09/privacy-policy.html"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun openFavorite() {
        startActivity(Intent(applicationContext, FavoriteActivity::class.java))
    }

    private fun openSettings() {
        startActivity(Intent(applicationContext, SettingsActivity::class.java))
    }

    private fun openSearchScreen() {
        startActivity(Intent(applicationContext, SearchActivity::class.java))
    }

    private fun closeDrawer() {
        binding.drawerLayout.closeDrawers()
    }

    private fun shareUs() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
            var shareMessage = "\nLet me recommend you this application\n\n"
            shareMessage =
                """${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}""".trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun rateUs() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }


    private fun setBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //mViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    fun checkPerms() {
//        if (ContextCompat.checkSelfPermission(
//                this@MainActivity,
//                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    this@MainActivity,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//            ) {
//
//                // Show an explanation to the user asynchronously -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//            } else {
//
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(
//                    this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                    222
//                )
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                startActivity(Intent(this, CommonDetailActivity::class.java))
            } else { //request for the permission
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        } else {
            //below android 11=======
            startActivity(Intent(this, CommonDetailActivity::class.java))
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                222
            )
        }
    }
    override fun onBackPressed() {
        showExitDialog()
    }

    private fun showExitDialog() {
        if (exitDialog == null) {
            val builder = AlertDialog.Builder(this@MainActivity)
            val view: View = layoutInflater.inflate(R.layout.exit_dialog, null)
            builder.setView(view)
            exitDialog = builder.create()

            val nativeAdView = view.findViewById<NativeAdView>(R.id.nativeAdView)

            val adLoader = AdLoader.Builder(
                this@MainActivity,
                "ca-app-pub-3940256099942544/2247696110"
            ) //testing unit id
                .forNativeAd { ad: NativeAd ->
                    // Show the ad.
                    if (ad != null) {
                        populateNativeAdView(ad, nativeAdView)
                    }
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        if (adError.code == 0) {
                            Log.d("adError", "onAdFailedToLoad: $adError")
                        } else {
                            Log.d("adError", "onAdFailedToLoad: $adError")
                        }

                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        // Methods to specify individual options settings.
                        .build()
                )
                .build()

            adLoader.loadAd(AdRequest.Builder().build())


            if (exitDialog!!.window != null) {
                exitDialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
            }

            view.findViewById<View>(R.id.textExitYes).setOnClickListener {
                finish()
            }

            view.findViewById<View>(R.id.textExitNo)
                .setOnClickListener { exitDialog!!.dismiss() }
        }
        exitDialog!!.show()
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {

        // Set the media view.
        adView.mediaView =
            adView.findViewById<com.google.android.gms.ads.nativead.MediaView>(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and media content are guaranteed to be in every NativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView?.mediaContent = nativeAd.mediaContent

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView?.visibility = View.INVISIBLE
        } else {
            adView.priceView?.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView?.visibility = View.INVISIBLE
        } else {
            adView.storeView?.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView?.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView?.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView?.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView?.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
    }
}