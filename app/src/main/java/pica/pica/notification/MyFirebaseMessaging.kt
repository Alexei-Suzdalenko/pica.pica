package pica.pica.notification
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.android.synthetic.main.my_profile_fragment.view.*
import pica.pica.MainActivity
import pica.pica.MessageChatActivity
import pica.pica.R
import pica.pica.util.App
import pica.pica.util.Common

class MyFirebaseMessaging: FirebaseMessagingService() {
    lateinit var channel: NotificationChannel
    lateinit var manager: NotificationManager
    lateinit var builder: Notification.Builder
    private val channelId = "12345"
    private val description = "Test Notification"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val miId: String = remoteMessage.data["myid"].toString()
        val receiverId = remoteMessage.data["receiverid"].toString()
        val conversationId = remoteMessage.data["conversationid"].toString()
        val receiverImage = remoteMessage.data["image"].toString()
        val message = remoteMessage.data["body"].toString()
        val title = remoteMessage.data["title"].toString()

        val j = miId.replace("[\\D]".toRegex(), "").toInt(); var i = 1; if(j > 0) i = j
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val intent = Intent(this, MessageChatActivity::class.java)
            intent.putExtra(Common.ID, miId)
            intent.putExtra(Common.RECEIVER, receiverId)
            intent.putExtra(Common.CONVERSATION, conversationId)
            intent.putExtra(Common.PROFILE_IMAGE, receiverImage)
            intent.putExtra(Common.TEXT, message)
            intent.putExtra(Common.NAME, title)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val isWorkiing = App.theAppIsWork as Boolean

       if( firebaseUser != null && receiverId == firebaseUser.uid && !isWorkiing){
           if( Build.VERSION.SDK_INT > Build.VERSION_CODES.O){

               channel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
               manager.createNotificationChannel(channel)
               builder = Notification.Builder(this, channelId)
                       .setContentTitle(title)
                       .setContentText(message)
                       .setSmallIcon(R.mipmap.ic_launcher)
                       .setContentIntent(pendingIntent)
               manager.notify(i, builder.build())

                } else {

                    val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val builder = NotificationCompat.Builder(this)
                       .setContentIntent(pendingIntent)
                       .setContentTitle(title)
                       .setContentText(message)
                       .setSmallIcon(R.mipmap.ic_launcher)
                       .setSound(defaultSound)
                       .setAutoCancel(true)
                    manager.notify(i, builder.build())
           }
       }




    }


    private fun sendNotificatioin(remoteMessage: RemoteMessage) {
        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]
        val notification = remoteMessage.notification
        val j = user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(this, MessageChatActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userId", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.user)
                .setSound(defaultSound)
            .setAutoCancel(true)
        val noti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var i = 0
        if( j > 0){
            i = j
        }
        noti.notify(11, builder.build())
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendOreoNotificatioin(remoteMessage: RemoteMessage) {
        val user = remoteMessage.data["user"]
        val icon = remoteMessage.data["icon"]
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]
        val notification = remoteMessage.notification
        val j = user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(this, MessageChatActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userId", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val oreoNotification = Oreonotification(this)
        val builder: Notification.Builder = oreoNotification.getOreoNotification(title, body, pendingIntent, defaultSound,  icon)
        var i = 0
        if( j > 0){
            i = j
        }
        oreoNotification.getManager!!.notify(11, builder.build())
    }



    // onNewToken вызов onNewToken срабатывает всякий раз, когда создается новый токен.
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                updateToken(p0)
            }
    }
    private fun updateToken(refreshToken: String?) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child(Common.TOKEN)
        val token = Token( refreshToken!! )
        ref.child(firebaseUser!!.uid).setValue(token)
    }
}
