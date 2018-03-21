package ng.canon.fastsaver.Church

import android.app.*
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
import android.content.*
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.os.Build
import ng.canon.fastsaver.R
import android.widget.Toast
import android.content.ClipboardManager.OnPrimaryClipChangedListener
import android.net.Uri
import android.os.Environment
import com.esafirm.rxdownloader.RxDownloader
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ng.canon.fastsaver.MainActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit


class Bayek : Service() {
    var observable: Observable<String>? = null
    var linkBox:ArrayList<String>? = null
    val clipBox = ArrayList<String>()
    companion object {
        val ACTION_PING = Bayek::class.java.name + ".PING"
        val ACTION_PONG = Bayek::class.java.name + ".PONG"
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, IntentFilter(ACTION_PING));
        notifySystem()

        val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.addPrimaryClipChangedListener {
            val currentText = clipboard.primaryClip.getItemAt(0).text.toString()
            if (currentText.contains("https://www.instagram.com/p/") || currentText.contains("https://www.instagram.com/v/")){

                val id = currentText.replace("https://www.instagram.com/p/","")
                if (!clipBox.contains(id)){



                   val dialogIntent =  Intent(this, MainActivity::class.java);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent)



                }


            }else{


            }

        }

        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy()
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_PING) {
                val manager = LocalBroadcastManager.getInstance(applicationContext)
                manager.sendBroadcast(Intent(ACTION_PONG))
            }
        }
    }




    fun notifySystem(){

        val title = getString(R.string.note_title)
        val texts = getString(R.string.note_text)




        var builder:Notification.Builder? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val ChannelId = "ng.canon.fastsaver";
        val ChannelName = "Fastsaver";
        val channel = NotificationChannel(ChannelId, ChannelName, NotificationManager.IMPORTANCE_LOW);
        val mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            mNotificationManager.createNotificationChannel(channel)
            builder = Notification.Builder(this, ChannelId)
        } else {
             builder = Notification.Builder(this)

        }

                builder!!.setContentTitle(title)
                .setContentText(texts)
                .setSmallIcon(R.drawable.ic_notif)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)

        val notification = builder.build()

        startForeground(110, notification)
    }



    fun linkCore(videoID:String)
    {
        linkBox = ArrayList<String>()
        observable = Observable.create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {


                try {

                    val start = "https://www.instagram.com/p/$videoID"
                    val end = "?__a=1"
                    val instaUrl = start+end
                    val respond = SaveDit(instaUrl)
                    val json = JSONObject(respond)
                    val type = json.getJSONObject("graphql").getJSONObject("shortcode_media").getString("__typename")

                    if(type.contains("GraphSidecar")){

                        val mediaBox = json.getJSONObject("graphql").getJSONObject("shortcode_media").getJSONObject("edge_sidecar_to_children").getJSONArray("edges")

                        for (i in 0..mediaBox.length() -1){

                            val jsonobj = mediaBox.getJSONObject(i)
                            val genre = jsonobj.getJSONObject("node").getString("__typename")
                            if (genre.contains("GraphVideo")){

                                val videoURL = jsonobj.getJSONObject("node").getString("video_url")
                                val viewURL = jsonobj.getJSONObject("node").getString("display_url")

                                linkBox!!.add(videoURL)
                            }else{

                                val photoURL = jsonobj.getJSONObject("node").getString("display_url")
                                linkBox!!.add(photoURL)

                            }
                        }

                    }


                    if(type.contains("GraphImage")){

                        val imageLink = json.getJSONObject("graphql").getJSONObject("shortcode_media").getString("display_url")
                        linkBox!!.add(imageLink)

                    }


                    if(type.contains("GraphVideo")){


                        val videoLink = json.getJSONObject("graphql").getJSONObject("shortcode_media").getString("video_url")
                        val photoLink = json.getJSONObject("graphql").getJSONObject("shortcode_media").getString("display_url")

                        linkBox!!.add(videoLink)


                    }



                    subscriber.onNext("")

                }catch (e:Exception){

                    subscriber.onError(e)
                }


                subscriber.onComplete()
            }
        })

        observable!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<String> {
                    override fun onSubscribe(d: Disposable) {



                    }

                    override fun onComplete() {





                        for (i in 0..linkBox!!.size -1){

                                mrSave(linkBox!![i])


                        }



                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(applicationContext,""+e.message, Toast.LENGTH_LONG).show()

                    }

                    override fun onNext(response: String) {


                    }
                })

    }



    //Downloader

    fun mrSave(urld: String){
        val rxDownloader = RxDownloader(applicationContext)
        var extension = ""
        var desc = ""



        if(urld.contains(".jpg")){
            extension = "jpg"
        }

        if(urld.contains(".png")){
            extension = "png"
        }


        if(urld.contains(".gif")){
            extension = "gif"
        }

        desc = getString(R.string.downloadPhoto)




        if (urld.contains(".mp4")){

            extension = "mp4"
            desc = getString(R.string.downloadVideo)


        }

        if (clipBox.size == 10){
            clipBox.clear()
        }


        val timeStamp =  System.currentTimeMillis()
        val filename = "insta_"+"_"+timeStamp
        val name = filename + "." + extension
        val dex = File(Environment.getExternalStorageDirectory().absolutePath, "fastsaver")
        if (!dex.exists())
            dex.mkdirs()

        val Download_Uri = Uri.parse(urld)
        val downloadManager =  getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request =  DownloadManager.Request(Download_Uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false)
        request.setTitle(name)
        request.setDescription(desc)
        request.setVisibleInDownloadsUi(true)
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir("/fastsaver",  name)

        rxDownloader.download(request).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<String> {
                    override fun onComplete() {


                    }

                    override fun onError(e: Throwable) {


                    }

                    override fun onNext(t: String) {


                    }

                    override fun onSubscribe(d: Disposable) {


                    }


                })

    }





    fun SaveDit(link:String):String{

        var pink = ""
        val saveclient = OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build()
        val saverequest = Request.Builder()
                .url(link)
                .build()
        val response = saveclient.newCall(saverequest).execute()


        val json = JSONObject(response.body()!!.string())


        return json.toString()
    }


}
