package android.example.UptoSkills.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.UptoSkills.R
import android.example.UptoSkills.SignInActivity
import android.example.UptoSkills.UserDetailsActivity
import android.example.UptoSkills.daos.GoogleUsersDao
import android.example.UptoSkills.daos.UsersDao
import android.example.UptoSkills.models.GoogleUser
import android.example.UptoSkills.models.Users
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MoreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MoreFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var usersDao: UsersDao
    private lateinit var displayName: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_more, container, false)


        displayName = view.findViewById(R.id.displayName)
        auth = Firebase.auth
        setUpDisplayName()


        var connection = view.findViewById<LinearLayout>(R.id.jobs)
        connection.setOnClickListener {
            var transaction = this.parentFragmentManager.beginTransaction()
            transaction.replace(R.id.container, JobFragment())
            transaction.commit()
        }

        var logout: LinearLayout = view.findViewById(R.id.LogOut)
        logout.setOnClickListener {
            auth.signOut()
            var intent = Intent(activity, SignInActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        var profile: LinearLayout = view.findViewById(R.id.username)
        profile.setOnClickListener {

            var intent = Intent(activity, UserDetailsActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    private fun setUpDisplayName() {
        usersDao = UsersDao()
        var name: String=""
        GlobalScope.launch(Dispatchers.IO) {
            usersDao.ref.orderByValue().equalTo(auth.currentUser?.uid).addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    name = snapshot.child("displayName").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

            withContext(Dispatchers.Main){
                displayName.text = name
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MoreFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MoreFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}