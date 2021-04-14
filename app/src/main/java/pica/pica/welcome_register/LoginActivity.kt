package pica.pica.welcome_register
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.btn_register
import pica.pica.MainActivity
import pica.pica.R

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        btn_login.setOnClickListener {
            val emailCompany = email_login.text.toString().trim()
            val passwordCompany =  password_login.text.toString().trim()
            if( emailCompany.length < 4 || passwordCompany.length < 5 ) Toast.makeText(this, resources.getString(
                R.string.enter_data
            ),  Toast.LENGTH_LONG).show()
            else {
                auth.signInWithEmailAndPassword(emailCompany, passwordCompany).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent);
                        finish()
                    } else {
                        Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}