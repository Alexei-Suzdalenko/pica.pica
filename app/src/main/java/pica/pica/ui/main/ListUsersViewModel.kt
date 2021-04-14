package pica.pica.ui.main
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import pica.pica.models.User
import pica.pica.util.Common
import pica.pica.models.Conversation
import java.util.*

class ListUsersViewModel: ViewModel() {
    val firebaseUserId = FirebaseAuth.getInstance().currentUser!!.uid

    var simpleListUser:  MutableList<User> = mutableListOf()
    var listUsers = MutableLiveData<MutableList<User>>()

    var listConversation:  MutableList<Conversation> = mutableListOf()
    val liveDataConversation: MutableLiveData<MutableList<Conversation>> = MutableLiveData<MutableList<Conversation>>()

    val listUserWithConversation: MutableList<User> = mutableListOf()

    fun getListUsersWithImage(): MutableLiveData<MutableList<User>> {
        FirebaseDatabase.getInstance().reference.child(Common.USERS).child(Locale.getDefault().language.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                simpleListUser.clear()
                for (snap in snapshot.children){
                    val user: User? = snap.getValue(User::class.java)
                    if ( user != null && user.id != firebaseUserId && user.profile_image.length > 11 ) {
                        simpleListUser.add(user)
                    }
                }
                val reverse = simpleListUser.asReversed().toMutableList()
                listUsers.value = reverse
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        return listUsers
    }


    fun getConversationFromUser(): MutableLiveData<MutableList<Conversation>> {
        FirebaseDatabase.getInstance().reference.child(Common.CONVERSATION).child(firebaseUserId).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) { }
            override fun onDataChange(snapshot: DataSnapshot) {
                listConversation.clear()
                for(snap in snapshot.children){
                    val conversation: Conversation? = snap.getValue(Conversation::class.java)
                    if(conversation != null && conversation.id.length > 5){
                        listConversation.add(conversation)
                    }
                }
                liveDataConversation.value = listConversation
            }
        })
        return liveDataConversation
    }




}














