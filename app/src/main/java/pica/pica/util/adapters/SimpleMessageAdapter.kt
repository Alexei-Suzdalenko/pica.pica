package pica.pica.util.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import pica.pica.R
import pica.pica.models.Message

class SimpleMessageAdapter(val context: Context, val chatList: List<Message>): RecyclerView.Adapter<SimpleMessageAdapter.viewInner?>() {
    val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun getItemViewType(position: Int): Int {
         if(firebaseUser == chatList[position].sender) return 1
         return 0
    }

    class viewInner(view: View) : RecyclerView.ViewHolder(view) {
        var textMessage: TextView = view.findViewById(R.id.textMessageChat)
        var rightImageView: ImageView? = null
        var leftImageView: ImageView? = null
        init {
            rightImageView = view.findViewById(R.id.rightImageView) ?: null
            leftImageView = view.findViewById(R.id.leftImageView) ?: null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewInner {
        if (viewType == 1 ) return viewInner( LayoutInflater.from(parent.context).inflate(R.layout.message_item_rigth, parent, false))
        return viewInner(LayoutInflater.from(parent.context).inflate(R.layout.message_item_left, parent, false))
    }

    override fun onBindViewHolder(holder: viewInner, position: Int) {
        if( chatList[position].text == "" && chatList[position].url.length > 22 ) {
            if( chatList[position].sender == firebaseUser) {
                holder.textMessage.visibility = View.GONE
                holder.rightImageView?.visibility = View.VISIBLE
                Glide.with(context).load(chatList[position].url).into(holder.rightImageView!!)
            } else if(chatList[position].sender != firebaseUser){
                holder.textMessage.visibility = View.GONE
                holder.leftImageView?.visibility = View.VISIBLE
                Glide.with(context).load(chatList[position].url).into(holder.leftImageView!!)
            }
        } else {
            holder.textMessage.text = chatList[position].text
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }


}