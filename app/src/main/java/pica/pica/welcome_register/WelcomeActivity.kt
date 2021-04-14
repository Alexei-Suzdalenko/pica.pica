package pica.pica.welcome_register
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_welcome.*
import pica.pica.MainActivity
import pica.pica.R
import pica.pica.util.App

class WelcomeActivity : AppCompatActivity() {
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        welcome_register.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)); finish() }
        welcome_login.setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)); finish() }
    }

    override fun onStart() {
        super.onStart()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if(firebaseUser != null){
            startActivity(Intent(this, MainActivity::class.java)); finish()
        }
    }

    override fun onResume() {
        super.onResume()

    //  FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener {
    //      val fbToken = it.result!!.token
    //      Log.d("tag", "MyFirebaseInstanceId token WelcomeActivity = " + fbToken)
    //      if (firebaseUser != null) {
    //          Log.d("tag", "token1 ................................................................................................ ")
    //          Log.d("tag", "token1 ..."  + fbToken)
    //          Log.d("tag", "token2 ..." +  fbToken)
    //      }
    //  }

    }
    
}