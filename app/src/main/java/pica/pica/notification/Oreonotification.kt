package pica.pica.notification
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi

class Oreonotification(base: Context?): ContextWrapper(base) {
    var notificationManager: NotificationManager? = null

    init {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH )
        channel.enableLights(false)
        channel.enableVibration(false)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        getManager!!.createNotificationChannel(channel)
    }

    val getManager: NotificationManager? get() {
        if( notificationManager == null ) {
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationManager
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getOreoNotification(title: String?, body: String?, pendingIntent: PendingIntent?, soundUri: Uri?, icon: String?): Notification.Builder {
        return Notification.Builder(applicationContext, "channel_id")
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(body)
           // .setSmallIcon(icon!!.toInt())
            .setAutoCancel(true)
    }


}