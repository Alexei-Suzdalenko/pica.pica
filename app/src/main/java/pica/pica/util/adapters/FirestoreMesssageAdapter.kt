package pica.pica.util.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import pica.pica.MessageChatActivity
import pica.pica.R
import pica.pica.models.Message

class FirestoreMesssageAdapter(val options: FirestoreRecyclerOptions<Message>, val activity: MessageChatActivity) : FirestoreRecyclerAdapter<Message, FirestoreMesssageAdapter.viewInner>(options) {

    val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid

    override fun getItemViewType(position: Int): Int {
          if(firebaseUser == options.snapshots[position].sender) return 1
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

    override fun onBindViewHolder(holder: viewInner, position: Int, model: Message) {
        if( model.text == "" && model.url.length > 22 ){
            if( model.sender == firebaseUser) {
                holder.textMessage.visibility = View.GONE
                holder.rightImageView?.visibility = View.VISIBLE
                // Glide.with(activity).load(model.url).into(holder.rightImageView!!)
                Picasso.get().load(model.url).into(holder.rightImageView!!)
            } else if(options.snapshots[position].sender != firebaseUser) {
                holder.textMessage.visibility = View.GONE
                holder.leftImageView?.visibility = View.VISIBLE
                // Glide.with(activity).load(model.url).into(holder.leftImageView!!)
                Picasso.get().load(model.url).into(holder.leftImageView!!)
            }
        } else {
            holder.textMessage.text = model.text
        }
    }

      override fun onDataChanged() {
          super.onDataChanged()
          activity.scrollToBottom()
      }






}