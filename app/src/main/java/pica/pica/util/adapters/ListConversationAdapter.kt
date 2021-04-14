package pica.pica.util.adapters
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import pica.pica.MessageChatActivity
import pica.pica.R
import pica.pica.VisitReceiverProfile
import pica.pica.models.Conversation
import pica.pica.util.App
import java.util.zip.Inflater

class ListConversationAdapter(private val context: Context, private val listConversation: MutableList<Conversation>): RecyclerView.Adapter<ListConversationAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val linearLayout: LinearLayout = view.findViewById(R.id.linearlayoutConversation)
        val userName: TextView = view.findViewById(R.id.userNameConversation)
        val userImage: CircleImageView = view.findViewById(R.id.userImageConversation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListConversationAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_conversation, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userName.text = listConversation[position].name
        try { Glide.with(context).load(listConversation[position].image).into(holder.userImage) } catch (e: Exception) { }
        Log.d("tag", "(listConversation[position]" + listConversation[position].toString() )

        holder.linearLayout.setOnClickListener {
            App.visitId = listConversation[position].id
            App.visitName = listConversation[position].name
            App.visitImage = listConversation[position].image

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

    override fun getItemCount(): Int = listConversation.size
}