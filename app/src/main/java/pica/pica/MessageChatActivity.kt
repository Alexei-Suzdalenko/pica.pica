package pica.pica
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_message_chat.*
import pica.pica.models.Message
import pica.pica.util.App
import pica.pica.util.Common
import pica.pica.util.IndeterminateProgressDialog
import pica.pica.util.adapters.FirestoreMesssageAdapter
import pica.pica.util.adapters.SimpleMessageAdapter
import pica.pica.util.message_activity_util.MessageActivityConversationExist
import pica.pica.util.message_activity_util.TrySentNotification
class MessageChatActivity : AppCompatActivity() {
    lateinit var messageActivityConversationExist: MessageActivityConversationExist
    var IamAndVisiterHaveConversation = false
    var adapter: SimpleMessageAdapter? = null
    var adapterFirestore: FirestoreMesssageAdapter? = null
    lateinit var firebase: Firebase
    val REQUEST_CODE = 9
    var chatList: MutableList<Message>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)
        chatList = mutableListOf()

        App.conversationId = ""
        messageActivityConversationExist = ViewModelProvider(this).get(MessageActivityConversationExist::class.java)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerViewChats.setHasFixedSize(true)
        recyclerViewChats.layoutManager = linearLayoutManager

        val remoteReceiverName = intent.getStringExtra(Common.NAME)
        val remoteConversationId = intent.getStringExtra(Common.CONVERSATION)
        val remoteUserName = intent.getStringExtra(Common.NAME)
        if(remoteReceiverName != null && remoteConversationId != null && remoteUserName != null){
            IamAndVisiterHaveConversation = true
            App.visitName = remoteUserName as String
            App.conversationId = remoteConversationId as String
            App.visitImage = intent.getStringExtra(Common.PROFILE_IMAGE).toString()
            App.visitId = intent.getStringExtra(Common.RECEIVER).toString()
            retrieveMensages()
        } else {
            App.miId = FirebaseAuth.getInstance().currentUser!!.uid // App.visitId
            messageActivityConversationExist.verificConvesationExist(App.miId, App.visitId).observeForever { conversacionExist ->
                if(conversacionExist){
                    IamAndVisiterHaveConversation = conversacionExist
                    //  showMeListMessages()
                    retrieveMensages()
                }
            }
        }


        profileImageChatReceiver.setOnClickListener { startActivity(Intent(this, VisitReceiverProfile::class.java)); finish() }

        try {
            Glide.with(this).load(App.visitImage).into(profileImageChatReceiver)
            userNameChatReceiver.text = App.visitName
        } catch (e: Exception){ finish() }

        sentMessageBtn.setOnClickListener { setMessage()  }
        attachImageFileBtn.setOnClickListener {
            if(App.conversationId.length > 5){
                val intent = Intent(); intent.action = Intent.ACTION_GET_CONTENT; intent.type = "image/*" ; startActivityForResult(Intent.createChooser(intent, "_"), REQUEST_CODE) }
            }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data?.data != null) {
            val dialog = IndeterminateProgressDialog(this);dialog.setCanceledOnTouchOutside(false);dialog.setCancelable(false);dialog.show()
            val filePath = App.storageReference.child(System.currentTimeMillis().toString() + ".jpg")

            filePath.putFile(data.data!!).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { it ->  Toast.makeText(this, "Error: " + it.message.toString(), Toast.LENGTH_LONG).show() }
                }
                dialog.hide()
                filePath.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val url = task.result.toString()
                    val message: Message = Message(System.currentTimeMillis().toString(), "", url, App.miId, App.visitId)
                    Firebase.firestore.collection(Common.CONVERSATION).document(Common.CONVERSATION).collection(App.conversationId).document().set(message)
                    // implement notification
                    TrySentNotification.sentNotification(App.visitId, App.visitName, "Image")
                }
            }
        }
    }


    private fun retrieveMensages() {
        Firebase.firestore.collection(Common.CONVERSATION).document(Common.CONVERSATION).collection(App.conversationId).orderBy(Common.ID).addSnapshotListener { value, _ ->
            if(value?.isEmpty != true){
                chatList?.clear()
                for (doc in value!!) {
                    val message: Message = doc.toObject(Message::class.java)
                    chatList!!.add(message)
                }
                adapter = SimpleMessageAdapter(this@MessageChatActivity, chatList!!)
                recyclerViewChats.adapter = adapter
            }
        }
    }

    private fun showMeListMessages(){
       if(App.conversationId.length > 5){
           val query = Firebase.firestore.collection(Common.CONVERSATION).document(Common.CONVERSATION).collection(App.conversationId).orderBy(Common.ID)
           val options = FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message::class.java).build()
           adapterFirestore = FirestoreMesssageAdapter(options, this@MessageChatActivity)
           recyclerViewChats.adapter = adapterFirestore
           adapterFirestore!!.startListening()
       }
    }

    override fun onResume() {
        super.onResume()
        App.theAppIsWork = true
        // showMeListMessages()
        // retrieveMensages()
        // adapterFirestore?.startListening()
    }


    private fun setMessage(){
        val message = textMessage.text.toString()
        if( message.isNotEmpty() && message.isNotBlank() && App.permissionSentMessage){
            if( !IamAndVisiterHaveConversation ){
                App.conversationId = System.currentTimeMillis().toString()
                messageActivityConversationExist.createConversation(App.miId).observeForever { conversationCreated ->
                    if(conversationCreated){
                        val mes = Message(System.currentTimeMillis().toString(), message, "", App.miId, App.visitId)
                        Firebase.firestore.collection(Common.CONVERSATION).document(Common.CONVERSATION).collection(App.conversationId).document().set(mes)
                        // implement notification
                        TrySentNotification.sentNotification(App.visitId, App.visitName, message)
                    }
                }
            } else {
                    val mes = Message(System.currentTimeMillis().toString(), message, "", App.miId, App.visitId)
                    Firebase.firestore.collection(Common.CONVERSATION).document(Common.CONVERSATION).collection(App.conversationId).document().set(mes)
                    // implement notification
                    TrySentNotification.sentNotification(App.visitId, App.visitName, message)
                    App.permissionSentMessage = false
                }
            textMessage.setText("")
        }
    }



    override fun onStop() {
        super.onStop()
        //  adapter?.stopListening()
        App.theAppIsWork = false
    }
    override fun onDestroy() {
        super.onDestroy()
        App.theAppIsWork = false
    }


    fun scrollToBottom(){
        recyclerViewChats.smoothScrollToPosition(adapterFirestore?.itemCount ?: 0)
    }
}