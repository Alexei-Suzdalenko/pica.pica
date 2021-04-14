package pica.pica.util
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class App: Application() {
    companion object {
        var visitId: String = ""
        var visitName: String = ""
        var visitImage: String = ""
        var conversationId: String = ""
        var miId: String = ""
        var permissionSentMessage = true
        lateinit var thread: Thread

        lateinit var userReference: DatabaseReference
        val storageReference by lazy { FirebaseStorage.getInstance().reference.child(Common.CHATS_IMAGES) }

        fun startPermissionSentMessage() {
            App.thread = Thread{
                while (true){
                    Thread.sleep(2000)
                    App.permissionSentMessage = true
                }
            }
            App.thread.start()
        }

        lateinit var sharedPreferences: SharedPreferences
        lateinit var editor: SharedPreferences.Editor

        var theAppIsWork = false
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()
        userReference = FirebaseDatabase.getInstance().reference.child(Common.USERS).child(Locale.getDefault().language.toString())
        startPermissionSentMessage()

        sharedPreferences = getSharedPreferences(Common.NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

}