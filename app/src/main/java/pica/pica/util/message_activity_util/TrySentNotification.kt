package pica.pica.util.message_activity_util
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import pica.pica.notification.*
import pica.pica.util.App
import pica.pica.util.Common
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object TrySentNotification {
    var apiService: APIService? = null
    val myImage = App.sharedPreferences.getString(Common.PROFILE_IMAGE, "").toString()
    val myName = App.sharedPreferences.getString(Common.NAME, "").toString()

    fun sentNotification(receiverId: String, username: String, message: String){
        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        val ref = FirebaseDatabase.getInstance().reference.child(Common.TOKEN)
        val query = ref.orderByKey().equalTo(receiverId)
            query.addValueEventListener(object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) { }
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val token: Token? = child.getValue(Token::class.java)
                        val data = Data(
                                App.miId,
                                myImage,
                                message,
                                myName,
                                receiverId,
                                App.conversationId
                        )
                        val sender = Sender(data, token!!.token)
                        // post request to https://fcm.googleapis.com/fcm/send
                        apiService?.sendNotification(sender)?.enqueue(object : Callback<MyResponse> {
                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {}
                            override fun onResponse(call: Call<MyResponse>, response: Response<MyResponse>) {
                                if (response.code() == 200) {
                                    if (response.body()?.success != 1) {
                                        Log.d("tag", "EROROROROROROROROROROROROROOROROROROROROOROROROROROROOROROOROROROROORO")
                                    }
                                }
                            }
                        })

                    }
                }
            })
    }
}