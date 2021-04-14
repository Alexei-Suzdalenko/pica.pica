package pica.pica.util.message_activity_util
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pica.pica.models.Message
import pica.pica.util.App
import pica.pica.util.Common
import pica.pica.models.Conversation

class MessageActivityConversationExist: ViewModel() {
    val firebase = FirebaseDatabase.getInstance().reference.child(Common.CONVERSATION)

    fun verificConvesationExist(miId: String, receiverId: String): MutableLiveData<Boolean>{
         val liveBoolean : MutableLiveData<Boolean> = MutableLiveData()

      FirebaseDatabase.getInstance().reference.child(Common.CONVERSATION).child(miId).child(receiverId).addValueEventListener(object : ValueEventListener {
          override fun onDataChange(snapshot: DataSnapshot) {
              if(snapshot.exists()){
                  val conversation: Conversation? = snapshot.getValue(Conversation::class.java)
                  if(conversation != null){
                      App.conversationId = conversation.conversationId
                      liveBoolean.value = true
                  }
              } else liveBoolean.value = false
          }
          override fun onCancelled(error: DatabaseError) {}
      })

     //  FirebaseDatabase.getInstance().reference.child(Common.CONVERSATION).child(receiverId).child(miId).addValueEventListener(object :
     //      ValueEventListener {
     //      override fun onDataChange(snapshot: DataSnapshot) {
     //          if(snapshot.exists()){
     //              val conversation: Conversation? = snapshot.getValue(Conversation::class.java)
     //              if(conversation != null){
     //                  App.conversationId = conversation.conversationId
     //                  liveBoolean.value = true
     //              }
     //          } else liveBoolean.value = false
     //      }
     //      override fun onCancelled(error: DatabaseError) {}
     //  })

        return liveBoolean
    }


    fun createConversation(miId: String): MutableLiveData<Boolean> {
        val conversationCreated: MutableLiveData<Boolean> = MutableLiveData()
        val miConversation = Conversation(App.visitId, App.conversationId, App.visitName, App.visitImage)
        firebase.child(miId).child(App.visitId).setValue(miConversation)

        val myName = App.sharedPreferences.getString(Common.NAME, " ").toString()
        val myImage = App.sharedPreferences.getString(Common.PROFILE_IMAGE, " ").toString()

        val visitConversation = Conversation(miId, App.conversationId, myName, myImage)
        firebase.child(App.visitId).child(miId).setValue(visitConversation).addOnCompleteListener { task ->
            if (task.isSuccessful){
                conversationCreated.value = true
            }
        }
        return conversationCreated
    }


    fun getListMessages(): MutableLiveData<MutableList<Message>> {
        val list: MutableList<Message> = mutableListOf()
        val listMessages: MutableLiveData<MutableList<Message>> = MutableLiveData<MutableList<Message>>()

        Firebase.firestore.collection(Common.CONVERSATION).document(Common.CONVERSATION).collection(App.conversationId)
            .orderBy(Common.ID)
            .addSnapshotListener { result, e ->
                list.clear()
                for (doc in result!!) {
                    val message = doc.toObject(Message::class.java)
                    list.add(message)

                }
                listMessages.value = list
            }
        return listMessages
    }



}