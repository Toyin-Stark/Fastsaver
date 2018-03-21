package ng.canon.fastsaver.FragPack


import android.content.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.github.angads25.toggle.LabeledSwitch
import com.github.angads25.toggle.interfaces.OnToggledListener
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.home.view.*
import ng.canon.fastsaver.GlideApp

import ng.canon.fastsaver.R
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import ng.canon.fastsaver.Church.Bayek
import android.support.v4.content.LocalBroadcastManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.ContextCompat.startActivity






class Home : Fragment() {

    private var isSvcRunning = false

    var switches:LabeledSwitch? = null
    var display:ImageView? = null
    var status:TextView? = null
    var launchers:Button? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.home, container, false)

        switches = v.switches
        display = v.displays
        status  = v.status
        launchers = v.launchers

        switches!!.setOnToggledListener(object:OnToggledListener{
            override fun onSwitched(labeledSwitch: LabeledSwitch?, isOn: Boolean) {

                val box = PreferenceManager.getDefaultSharedPreferences(activity)

                if (isOn){

                    GlideApp.with(this@Home).asGif().load(R.raw.output).into(displays!!)
                    box.edit().putBoolean("locked", true).apply()
                    status!!.text = getString(R.string.switch_message_on)
                    launchers!!.visibility =View.VISIBLE
                    Genesis()

                }else{


                    GlideApp.with(this@Home).load(R.raw.off).into(displays!!)
                    box.edit().putBoolean("locked", false).apply()
                    status!!.text = getString(R.string.switch_message)
                    launchers!!.visibility =View.GONE
                    Revelation()


                }
            }




        })


        v.launchers.setOnClickListener {

            callInstagram(activity!!.applicationContext,"com.instagram.android")
        }

        return v
    }



    fun Genesis(){

        val intu = Intent(activity,Bayek::class.java)
        ContextCompat.startForegroundService(activity!!.applicationContext,intu)
    }



    fun Revelation(){

        val intu = Intent(activity,Bayek::class.java)
        activity!!.stopService(intu)
    }


    override fun onResume() {
        val manager = LocalBroadcastManager.getInstance(activity!!.applicationContext)
        manager.registerReceiver(mReceiver, IntentFilter(Bayek.ACTION_PONG))
        // the service will respond to this broadcast only if it's running
        manager.sendBroadcast(Intent(Bayek.ACTION_PING))
        super.onResume()
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(activity!!.applicationContext).unregisterReceiver(mReceiver);
        super.onStop()
    }


    protected var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // here you receive the response from the service
            if (intent.action == Bayek.ACTION_PONG) {
                isSvcRunning = true
                watchTower()
                GlideApp.with(this@Home).asGif().load(R.raw.output).into(displays!!)
                status!!.text = getString(R.string.switch_message_on)
                launchers!!.visibility =View.VISIBLE

            }
        }
    }


    fun watchTower(){

        switches!!.isOn = isSvcRunning

    }




    private fun callInstagram(context: Context, packageN: String) {
        val apppackage = packageN
        try {
            val i = context.packageManager.getLaunchIntentForPackage(apppackage)
            context.startActivity(i)
        } catch (e: Exception) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageN)))
        }

    }

}// Required empty public constructor
