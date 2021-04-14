package pica.pica.ui.main
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import io.grpc.InternalChannelz.id
import pica.pica.R
import pica.pica.models.Conversation
import pica.pica.models.User
import pica.pica.util.App
import pica.pica.util.Common
import pica.pica.util.IndeterminateProgressDialog


object ProfileUserWork {
    var userTop = User()
    val REQUESTCODE = 3
    private var coverChecker: String = ""

    fun prepare(view: View, workuserViewModel: ProfileViewModel, context: Context, placeholderFragment: PlaceholderFragment){

        val coverImage: ImageView = view.findViewById(R.id.cover_image)
            coverImage.setOnClickListener { coverChecker = "cover"
                placeholderFragment.pickImage()
            }

        val userImage: CircleImageView = view.findViewById(R.id.user_image)
            userImage.setOnClickListener {
                placeholderFragment.pickImage()
            }

        val userName: TextView = view.findViewById(R.id.user_name)
            userName.setOnClickListener {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle(context.resources.getString(R.string.name))
                alertDialog.setMessage(context.resources.getString(R.string.change_name))
                val input = EditText(context)
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                input.layoutParams = lp
                input.setText(userTop.name)
                alertDialog.setView(input)
                alertDialog.setIcon(R.mipmap.ic_launcher)
                alertDialog.setPositiveButton(context.resources.getString(R.string.yes)) { dialog, _ ->
                    if (input.text.toString().length < 3) {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.enter_data),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val userHashMap = HashMap<String, Any>()
                        userHashMap[Common.NAME] = input.text.toString()

                        App.editor.putString(Common.NAME, input.text.toString())
                        App.editor.apply()

                        App.userReference.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(userHashMap)
                    }
                    dialog.dismiss()
                }
                alertDialog.setNegativeButton(context.resources.getString(R.string.no)) { dialog, _ -> dialog.cancel() }
                alertDialog.show()
            }

        val aboutme: TextView = view.findViewById(R.id.about_me)
            aboutme.setOnClickListener {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle(context.resources.getString(R.string.about_me))
                alertDialog.setMessage(context.resources.getString(R.string.change_about_me))
                val input = EditText(context)
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                input.layoutParams = lp
                input.setText(userTop.about_me)
                alertDialog.setView(input)
                alertDialog.setIcon(R.mipmap.ic_launcher)
                alertDialog.setPositiveButton(context.resources.getString(R.string.yes)) { dialog, _ ->
                    if (input.text.toString().length < 3) {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.enter_data),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val userHashMap = HashMap<String, Any>()
                            userHashMap[Common.ABOUT_ME] = input.text.toString()
                        App.userReference.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(userHashMap)
                    }
                    dialog.dismiss()
                }
                alertDialog.setNegativeButton(context.resources.getString(R.string.no)) { dialog, _ -> dialog.cancel() }
                alertDialog.show()
            }

        workuserViewModel.getUser(context).observeForever { user ->
            if(user.cover_image.length > 11) {
                try {
                    Glide.with(context).load(user.cover_image).placeholder(R.drawable.cover).into(coverImage)
                } catch (e: Exception){}
            }
            if(user.profile_image.length > 11) {
                try {
                    Glide.with(context).load(user.profile_image).placeholder(R.drawable.user).into(userImage)
                } catch (e: Exception){}
            }
            userTop = user
            userName.text = user.name
            if(user.about_me.length > 3){
                aboutme.text = user.about_me
            }
        }


    }


    fun uploadImage(imageUri: Uri?, context: Context){
        val dialog = IndeterminateProgressDialog(context); dialog.setCanceledOnTouchOutside(false); dialog.setCancelable(false); dialog.show()
        if(imageUri != null) {
            val fileRef = FirebaseStorage.getInstance().reference.child(Common.USERS).child(System.currentTimeMillis().toString() + ".jpg")
            fileRef.putFile(imageUri).continueWithTask { task ->
                if ( !task.isSuccessful ) {
                    task.exception?.let { it -> dialog.hide(); Toast.makeText(context, "Error: " + it.message.toString(), Toast.LENGTH_LONG).show() }; }
                fileRef.downloadUrl
            }.addOnCompleteListener { task -> dialog.hide()
                if (task.isSuccessful) {
                    val downloadUri = task.result.toString(); dialog.hide()
                    val map = HashMap<String, Any>()
                    if( coverChecker == "cover" ) {
                        map[Common.COVER_IMAGE] = downloadUri
                        App.userReference.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(map)
                        coverChecker = ""
                    } else {
                        map[Common.PROFILE_IMAGE] = downloadUri

                        App.editor.putString(Common.PROFILE_IMAGE, downloadUri)
                        App.editor.apply()

                        App.userReference.child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(map)
                      // updataUserProfileImageInListConversation(downloadUri)
                    }
                } else { dialog.hide()
                    Toast.makeText(context, "Error file upload", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


 //  fun updataUserProfileImageInListConversation(downloadUri: String){
 //      FirebaseDatabase.getInstance().reference.child(Common.CONVERSATION).child(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener { snapshots ->
 //          if(snapshots.exists()){
 //              for( item in snapshots.children){
 //                  val conversation: Conversation? = item.getValue(Conversation::class.java)
 //                  if(conversation != null && conversation.id.length > 7){
 //                      conversation.image = downloadUri
 //                      val id = conversation.id
 //                      FirebaseDatabase.getInstance().reference.child(Common.CONVERSATION).child(FirebaseAuth.getInstance().currentUser!!.uid).child(id).setValue(conversation)
 //                  }
 //              }
 //          }
 //      }
 //  }




}