package pica.pica.ui.main
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pica.pica.R
import pica.pica.models.User
import pica.pica.ui.main.ProfileUserWork.REQUESTCODE
import pica.pica.util.adapters.ListConversationAdapter
import pica.pica.util.adapters.UsersAdapter

class PlaceholderFragment : Fragment() {
    var index = 0
    var root: View? = null
    lateinit var listUsersViewModel: ListUsersViewModel
    lateinit var workuserViewModel: ProfileViewModel

    private var userAdapter: UsersAdapter? = null
    private var users: MutableList<User>? = null
    private var recyclerView: RecyclerView? = null
    private var recyclerChatList: RecyclerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        index = arguments?.getInt(ARG_SECTION_NUMBER) ?: 1

        listUsersViewModel =  ViewModelProvider(this).get(ListUsersViewModel::class.java)
        workuserViewModel =  ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        when(index){
            1->{
                root = inflater.inflate(R.layout.all_users_fragment, container, false)
                recyclerView = root?.findViewById(R.id.recyclerview_listusers)
                recyclerView!!.setHasFixedSize(true)
                recyclerView!!.layoutManager = GridLayoutManager(context, 4)  // recyclerView!!.layoutManager = LinearLayoutManager(context)
                listUsersViewModel.getListUsersWithImage().observeForever { listUsers ->
                    if(listUsers.isNotEmpty()){
                        recyclerView!!.adapter = UsersAdapter(context!!, listUsers)
                    }
                }
            }
            2-> {   Log.d("tag", "start start start == start start start")
                root = inflater.inflate(R.layout.list_currents_chats, container, false)
                recyclerChatList = root?.findViewById(R.id.recyclerChatList)
                recyclerChatList!!.setHasFixedSize(true)
                recyclerChatList!!.layoutManager = LinearLayoutManager(context)
                listUsersViewModel.getConversationFromUser().observeForever { listConversation ->
                    if(listConversation.isNotEmpty()){
                        recyclerChatList!!.adapter = ListConversationAdapter(context!!, listConversation)
                    }
                }
            }
            3 -> {
                root = inflater.inflate(R.layout.my_profile_fragment, container, false)!!
                ProfileUserWork.prepare(root!!, workuserViewModel, container!!.context, this)
            }
        }
        return root
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }


    fun pickImage() {
        val intent = Intent(); intent.type = "image/"; intent.action = Intent.ACTION_GET_CONTENT; startActivityForResult(intent, ProfileUserWork.REQUESTCODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==  REQUESTCODE && Activity.RESULT_OK == resultCode && data?.data != null){
            val imageUri = data.data; ProfileUserWork.uploadImage(imageUri, context!!) }
    }

}