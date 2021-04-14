package pica.pica.util.adapters
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import pica.pica.MessageChatActivity
import pica.pica.R
import pica.pica.VisitReceiverProfile
import pica.pica.models.User
import pica.pica.util.App
import pica.pica.util.Common

class UsersAdapter(val context: Context, val users: MutableList<User>): RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val userName: TextView = view.findViewById(R.id.name_user_id)
        val profileImageView: CircleImageView = view.findViewById(R.id.image_person_list_users)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersAdapter.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.users_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersAdapter.ViewHolder, position: Int) {
            holder.userName.text = users[position].name
            try {
                Glide.with(context).load(users[position].profile_image).placeholder(R.drawable.user).into(holder.profileImageView)
            } catch (e: Exception) { }

        holder.profileImageView.setOnClickListener {
            val myImage = App.sharedPreferences.getString(Common.PROFILE_IMAGE, null)

            if(myImage == null || myImage.toString().length < 11){
                Toast.makeText(context, context.resources.getString(R.string.upload_profile_image), Toast.LENGTH_LONG).show()
            } else {
                App.visitId = users[position].id
                App.visitName = users[position].name
                App.visitImage = users[position].profile_image

                val options = arrayOf(context.resources.getString(R.string.visit_profile), context.resources.getString(R.string.sent_message))
                val builder = AlertDialog.Builder(context)
                builder.setTitle( context.resources.getString(R.string.options) )
                builder.setItems(options) { _, position ->
                    if ( position == 0 ) context.startActivity( Intent (context, VisitReceiverProfile::class.java) )
                    if ( position == 1 ) context.startActivity( Intent (context, MessageChatActivity::class.java) )
                }
                builder.show()
            }
        }
    }

    override fun getItemCount(): Int = users.size
}