package pica.pica
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_visit_receiver_profile.*
import pica.pica.models.User
import pica.pica.util.App
import pica.pica.util.Common
import java.util.*

class VisitReceiverProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_receiver_profile)

        if ( App.visitId == "" ) finish()

        App.userReference.child(App.visitId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user: User? = snapshot.getValue(User::class.java)
                    if( user != null ){
                        if(user.cover_image.length > 11){
                            try{
                                Glide.with(this@VisitReceiverProfile).load(user.cover_image).into(cover_image_receiver)
                            } catch (e: Exception){ }
                        }
                        if(user.profile_image.length > 11){
                            try{
                                Glide.with(this@VisitReceiverProfile).load(user.profile_image).into(user_image_receiver)
                            } catch (e: Exception){ }
                        }
                        user_name_receiver.text = user.name
                        about_me_receiver.text = user.about_me
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) { }
        })

        sentMsgButton_receiver.setOnClickListener {
            startActivity( Intent (this, MessageChatActivity::class.java) ); finish()
        }


    }
}