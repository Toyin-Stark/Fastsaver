package ng.canon.fastsaver

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import kotlinx.android.synthetic.main.activity_main.*
import ng.canon.fastsaver.FragPack.Home
import ng.canon.fastsaver.FragPack.PhotoBucket
import ng.canon.fastsaver.FragPack.StoryBook

class MainActivity : AppCompatActivity() {
    var mInterstitialAd: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadNavigation()

        mInterstitialAd = InterstitialAd(this@MainActivity,getString(R.string.intersistal))
        mInterstitialAd!!.setAdListener(object : InterstitialAdListener {
            override fun onLoggingImpression(p0: Ad?) {


            }

            override fun onAdLoaded(p0: Ad?) {

                mInterstitialAd!!.show();

            }

            override fun onError(p0: Ad?, p1: AdError?) {


            }

            override fun onInterstitialDismissed(p0: Ad?) {


            }

            override fun onAdClicked(p0: Ad?) {


            }

            override fun onInterstitialDisplayed(p0: Ad?) {


            }


        })



        // Load ads into Interstitial Ads
        mInterstitialAd!!.loadAd()
    }


    fun loadNavigation(){


        val home = AHBottomNavigationItem(R.string.home_tab, R.drawable.ic_home,android.R.color.white)
        val story = AHBottomNavigationItem(R.string.story_tab, R.drawable.ic_stories,android.R.color.white)
        val save = AHBottomNavigationItem(R.string.save_tab, R.drawable.ic_downloadx,android.R.color.white)

        bottomNavigation.addItem(home)
        bottomNavigation.addItem(story)
        bottomNavigation.addItem(save)

        bottomNavigation.defaultBackgroundColor = ContextCompat.getColor(applicationContext, R.color.colorBase)
        bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        bottomNavigation.isForceTint = true
        bottomNavigation.accentColor = ContextCompat.getColor(applicationContext,android.R.color.white)

        bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        bottomNavigation.isForceTint = true


        val faces = Home()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frames, faces)
        transaction.addToBackStack(null)
        transaction.commit()


        bottomNavigation.setOnTabSelectedListener(object:AHBottomNavigation.OnTabSelectedListener{
            override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {


                if (position == 0){

                    val homes = Home()
                    val hometransaction = supportFragmentManager.beginTransaction()
                    hometransaction.replace(R.id.frames, homes)
                    hometransaction.addToBackStack(null)
                    hometransaction.commit()

                }


                if (position == 1){

                    val story = StoryBook()
                    val storytransaction = supportFragmentManager.beginTransaction()
                    storytransaction.replace(R.id.frames, story)
                    storytransaction.addToBackStack(null)
                    storytransaction.commit()

                }

                if (position == 2){

                    val photopale = PhotoBucket()
                    val phototransaction = supportFragmentManager.beginTransaction()
                    phototransaction.replace(R.id.frames, photopale)
                    phototransaction.addToBackStack(null)
                    phototransaction.commit()

                }


                return true
            }


        })

    }
}
