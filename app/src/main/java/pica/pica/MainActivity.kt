package pica.pica
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import pica.pica.notification.Token
import pica.pica.ui.main.SectionsPagerAdapter
import pica.pica.util.App
import pica.pica.util.Common
import pica.pica.util.Permissions
import pica.pica.welcome_register.WelcomeActivity

class MainActivity : AppCompatActivity() {
    val REQUEST_PERMISSIONS = 5
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        Permissions.requestPermission(this)
    }





 //  override fun onCreateOptionsMenu(menu: Menu): Boolean {
 //      menuInflater.inflate(R.menu.menu_user, menu)
 //      return true
 //  }
 //  override fun onOptionsItemSelected(item: MenuItem): Boolean {
 //      return when (item.itemId) {
 //          R.id.my_profile -> {
 //              startActivity(intent); finish()
 //              true
 //          }
 //          else -> false
 //      }
 //  }


    override fun onStart() {
        super.onStart()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if(firebaseUser == null){
            startActivity(Intent(this, WelcomeActivity::class.java)); finish()
        }
    }


    override fun onResume() {
        super.onResume()
        // Получить текущий регистрационный токен
        FirebaseMessaging.getInstance().token.addOnCompleteListener{ task ->
            if( task.isSuccessful ){
                // Get new FCM registration token
                val token = task.result
                // Log.d("tag", "MainActivity MainActivity MainActivity = " + token)
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                val ref = FirebaseDatabase.getInstance().reference.child(Common.TOKEN)
                val tokenObj = Token( token!! )
                ref.child(firebaseUser!!.uid).setValue(tokenObj)
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] +
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    //Call whatever you want
                    // myMethod()
                } else {
                    Snackbar.make(
                        findViewById(android.R.id.content), "Enable Permissions from settings",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(
                        "ENABLE"
                    ) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                        intent.data = Uri.parse("package:$packageName")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        startActivity(intent)
                    }.show()
                }
                return
            }
        }
    }



}

