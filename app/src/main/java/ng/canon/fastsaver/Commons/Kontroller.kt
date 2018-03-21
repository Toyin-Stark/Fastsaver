package ng.canon.fastsaver.Commons

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import java.io.IOException
import java.util.*
import android.util.Base64
import android.view.View
import java.io.InputStream
import java.util.regex.Pattern
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Patterns
import android.webkit.WebView
import ng.canon.fastsaver.R
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit




// INJECT JAVASCRIPT INTO WEBVIEW
 fun injectScriptFile(context: Context,scriptFile: String,mWebView: WebView) {
    val rand = Random()
    val verse = rand.nextInt(80 - 65) + 65
    val input: InputStream
    try {
        input = context.assets.open(scriptFile)
        val buffer = ByteArray(input.available())
        input.read(buffer)
        input.close()

        // String-ify the script byte-array using BASE64 encoding !!!
        val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)

        mWebView.evaluateJavascript("javascript:(function() {" +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var script = document.createElement('script');" +
                "script.type = 'text/javascript';" +
                // Tell the browser to BASE64-decode the string into your script !!!
                "script.innerHTML = decodeURIComponent(escape(window.atob('" + encoded + "')));" +
                "parent.appendChild(script)" +
                "})()") { }


    } catch (e: IOException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    }

}


// INJECT CSS INTO WEBVIEW
 fun injectCSS(context: Context,filespaces: String,mWebView: WebView) {

    try {
        val inputStream = context.assets.open(filespaces)
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()
        val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)
        mWebView!!.loadUrl("javascript:(function() {" +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var style = document.createElement('style');" +
                "style.type = 'text/css';" +
                // Tell the browser to BASE64-decode the string into your script !!!
                "style.innerHTML = decodeURIComponent(escape(window.atob('" + encoded + "')));" +
                "parent.appendChild(style)" +
                "})()")
    } catch (e: Exception) {
        e.printStackTrace()
    }

}



// EXTRACT LINKS FROM STRINGS
fun pullLinks(text: String): ArrayList<String> {
    val links = ArrayList<String>()

    //String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
    val regex = "\\(?\\b(https?://|www[.]|ftp://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]"

    val p = Pattern.compile(regex)
    val m = p.matcher(text)

    while (m.find()) {
        var urlStr = m.group()

        if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
            urlStr = urlStr.substring(1, urlStr.length - 1)
        }

        links.add(urlStr)
    }

    return links
}




// SHOW PERMISSION PROMT DIALOG

fun showDialog(context: Context)
{
    AlertDialog.Builder(context)
            .setTitle(R.string.permissionTitle)
            .setMessage(R.string.permissionMessage)
            .setPositiveButton(R.string.permissionPositive,object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                }


            })

            .setNegativeButton(R.string.permissionNegative,object:DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                }


            })
            .setCancelable(false)
            .show()

}



//SHOW SNACKBAR
fun snackUp(context: Context,message:String,view: View)
{
    val snacks = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snacks.view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
    snacks.show()
}


// INITIAL DOWNLOAD NOTIFICATION
fun Alariwo(context: Context,noteID:Int){

    val notificationBuilder =  NotificationCompat.Builder(context, "M_CH_ID")

    notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(context.getString(R.string.downloadStart))
            .setProgress(0, 0, true)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(noteID, notificationBuilder.build());
}




fun Checkmate(activity: Activity,context: Context){

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


    }else{


        // load permission request method here

    }

}








fun Saveit(link:String):String{

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



fun extracTors(text: String): Array<String> {
    val links = ArrayList<String>()
    val m = Patterns.WEB_URL.matcher(text)
    while (m.find()) {
        val urls = m.group()
        links.add(urls)
    }

    return links.toTypedArray()
}













