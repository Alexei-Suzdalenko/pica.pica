package pica.pica.ui.main
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import pica.pica.R
import pica.pica.models.User
import pica.pica.util.App
import pica.pica.util.Common
import pica.pica.welcome_register.WelcomeActivity
import java.util.*

class ProfileViewModel: ViewModel() {
    private var liveUser = MutableLiveData<User>()

    fun getUser(context: Context): MutableLiveData<User>{

        App.userReference
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user: User = snapshot.getValue(User::class.java)!!
                    liveUser.value = user
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, context.resources.getString(R.string.error_ocurrid), Toast.LENGTH_LONG).show()
                context.startActivity(Intent( context, WelcomeActivity::class.java))
            }

        })
        return liveUser
    }
}