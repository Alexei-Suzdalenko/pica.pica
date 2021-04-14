package pica.pica.notification
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessagingService
import pica.pica.util.Common

class MyFirebaseInstanceId: FirebaseMessagingService() {
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