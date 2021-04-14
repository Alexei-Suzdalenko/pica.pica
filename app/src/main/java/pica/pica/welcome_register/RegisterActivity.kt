package pica.pica.welcome_register
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import pica.pica.MainActivity
import pica.pica.R
import pica.pica.util.App
import pica.pica.util.Common
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

          auth = FirebaseAuth.getInstance()
          if( auth.currentUser != null ){
              startActivity(Intent(this, MainActivity::class.java)); finish()
          }

        btn_register.setOnClickListener { registerUser() }

    }


    private fun registerUser() {
        val name = name_register.text.toString().trim()
        val email = email_register.text.toString().trim()
        val password = password_register.text.toString().trim()

        val genid = man_woman.checkedRadioButtonId;
        val radioButton = findViewById<RadioButton>(genid)
        val gender = radioButton.text.toString()

        val search = search_person.checkedRadioButtonId;
        val radioSearch = findViewById<RadioButton>(search)
        val target = radioSearch.text.toString()

        val city = register_city.text.toString().trim()

        if( name.length < 3 || email.length < 3 || password.length < 4 || city.length < 3) Toast.makeText(this, resources.getString(R.string.enter_data),  Toast.LENGTH_LONG).show()
        else {
            auth.createUserWithEmailAndPassword( email, password ).addOnCompleteListener { task ->
                if( task.isSuccessful ){
                        val userHasMap = HashMap<String, Any>()
                            userHasMap[Common.ID] = auth.currentUser!!.uid
                            userHasMap[Common.NAME] = name
                            userHasMap[Common.EMAIL] = email
                            userHasMap[Common.PASSWORD] = password
                            userHasMap[Common.GENDER] = gender
                            userHasMap[Common.SEARCH] = target
                            userHasMap[Common.CITY] = city
                            userHasMap[Common.COVER_IMAGE] = ""
                            userHasMap[Common.ABOUT_ME] = ""
                            userHasMap[Common.LANG] = Locale.getDefault().language.toString()
                            userHasMap[Common.PROFILE_IMAGE] = ""

                    App.editor.putString(Common.NAME, name)
                    App.editor.putString(Common.PROFILE_IMAGE, "")
                    App.editor.apply()

                    App.userReference
                            .child(auth.currentUser!!.uid)
                            .setValue(userHasMap)
                            .addOnCompleteListener { task ->
                            if ( task.isSuccessful ){
                                val intent = Intent(this, MainActivity::class.java )
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, resources.getString(R.string.enter_data),  Toast.LENGTH_LONG).show()
                            }
                        }
                } else Toast.makeText(this,  task.exception?.message.toString(),  Toast.LENGTH_LONG).show()
            }
        }


    }



}






































