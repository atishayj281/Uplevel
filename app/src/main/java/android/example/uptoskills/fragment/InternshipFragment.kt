package android.example.uptoskills.fragment

import android.content.Intent
import android.example.uptoskills.Adapters.InternshipAdapter
import android.example.uptoskills.Adapters.JobItemClicked
import android.example.uptoskills.JobViewActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.uptoskills.R
import android.example.uptoskills.daos.InternshipDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.models.Internship
import android.example.uptoskills.models.Users
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InternshipFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InternshipFragment : Fragment(), JobItemClicked, onJobSearch {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InternshipAdapter
    private lateinit var internshipDao: InternshipDao
    private lateinit var progressBar: ProgressBar
    private var curUser: Users = Users()
    private lateinit var userDao: UsersDao
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_internship, container, false)
        auth = FirebaseAuth.getInstance()
        userDao = UsersDao()
        GlobalScope.launch(Dispatchers.IO) {
            val user = auth.currentUser?.let { userDao.getUserById(it.uid).await().toObject(Users::class.java) }
            if(user != null) {
                curUser = user
            }
        }
        recyclerView = view.findViewById(R.id.internshiprecyclerView)
        progressBar = view.findViewById(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        // Setting the internship
        setUpInternshipRecyclerView(view, "")
        return view
    }

    private fun setUpInternshipRecyclerView(view: View, filter: String){
        internshipDao = InternshipDao()
        val internshipCollection = internshipDao.jobCollection
        val query: Query

        if(filter.isEmpty()) {
            query = internshipCollection.orderBy("id", Query.Direction.DESCENDING)
            val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Internship>().setQuery(query, Internship::class.java).build()
            adapter = InternshipAdapter(view.context, recyclerViewOptions, this, R.layout.internship_item)
        } else {
            query = internshipCollection.whereEqualTo("category", filter)
            val recyclerOptions = FirestoreRecyclerOptions.Builder<Internship>().setQuery(query, Internship::class.java).build()
            adapter.updateOptions(recyclerOptions)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        progressBar.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InternshipFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InternshipFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onJobCLick(jobId: String) {
        val intent = Intent(view?.context, JobViewActivity::class.java)
        intent.putExtra("jobId", jobId)
        intent.putExtra("category", "internship")
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onbookmarkCLick(itemId: String, itemtype: String): Boolean {
        var idBookmarked = true
        var itemType = "internship"
        Log.e(itemId, itemtype)
        if(curUser.bookmarks?.containsKey(itemId) == true && curUser.bookmarks!![itemId] == itemType) {
            idBookmarked = false
            curUser.bookmarks!!.remove(itemId, itemType)
        } else {
            curUser.bookmarks?.set(itemId, itemType)
        }
        internshipDao.addbookmark(itemId)
        auth.currentUser?.let { userDao.updateUser(curUser, it.uid) }
        return idBookmarked
    }

    override fun shareJob(itemId: String, itemtype: String) {
        createReferLink(curUser.id, "${itemtype}#${itemId}")
    }

    private fun createReferLink(uId: String, productId: String) {
        var link: String = "https://uptoskill.page.link/?"+
                "link=https://www.uptoskill.com/myrefer.php?uId="+uId+"-"+productId+
                "&apn="+activity?.packageName+
                "&st=Join me on UptoSkills"+
                "&sd=Reward UsCash 250"+
                "&si=https://www.uptoskills.com/wp-content/uploads/2019/10/logo-dark.png"

        // shorten the link

        val shortLinkTask = activity?.let {
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://uptoskill.page.link") // Set parameters
                .buildShortDynamicLink()
                .addOnCompleteListener(it
                ) { task ->
                    if (task.isSuccessful) {
                        // Short link created
                        val shortLink = task.result.shortLink
                        val flowchartLink = task.result.previewLink

                        val intent = Intent()
                        intent.action = Intent.ACTION_SEND
                        intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                        intent.type = "text/plain"
                        startActivity(intent)
                        // ------ click -> link -> google play store -> installed/not ---------

                    } else {
                        // Error
                        // ...
                    }
                }
        }
    }


    override fun updateRecyclerView(query: String) {
        view?.let { setUpInternshipRecyclerView(it, query) }
    }
}