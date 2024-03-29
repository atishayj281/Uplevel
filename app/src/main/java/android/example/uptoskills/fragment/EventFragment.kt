package android.example.uptoskills.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.example.uptoskills.*
import android.example.uptoskills.Adapters.EventsAdapter
import android.example.uptoskills.Adapters.IEventClickListener
import android.example.uptoskills.daos.BlogDao
import android.example.uptoskills.daos.EventDao
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.models.Events
import android.example.uptoskills.models.Users
import android.net.Uri
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.FacebookSdk
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.Query
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
 * Use the [EventFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventFragment : Fragment(), IEventClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var curUser: Users = Users()
    private lateinit var hometoolBar: MaterialToolbar
    private lateinit var homedrawerLayout: DrawerLayout
    private lateinit var homeNavigationView: NavigationView
    private var displayName: String = ""
    private lateinit var menuBar: ImageView
    private lateinit var userDao: UsersDao
    private lateinit var auth: FirebaseAuth
    private lateinit var eventDao: EventDao
    private lateinit var adapter: EventsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var postDao: BlogDao
    private lateinit var onNavDrawerListener: onNavDrawerListener


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            var parent: Activity
            if (context is Activity) {
                parent = context
                onNavDrawerListener = parent as onNavDrawerListener
            }
        } catch (e: Exception) {

        }
    }

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
        val view = inflater.inflate(R.layout.fragment_event, container, false)

        userDao = UsersDao()
        hometoolBar = view.findViewById(R.id.hometoolBar)
        homedrawerLayout = view.findViewById(R.id.homedrawerLayout)
        homeNavigationView = view.findViewById(R.id.homeNavigationView)
        menuBar = view.findViewById(R.id.menuBar)
        auth = FirebaseAuth.getInstance()
        recyclerView = view.findViewById(R.id.eventsRecyclerView)
        progressBar = view.findViewById(R.id.progress_bar)

        progressBar.visibility = View.VISIBLE
        menuBar.setOnClickListener {
            homedrawerLayout.openDrawer(GravityCompat.START)
            onNavDrawerListener.onDrawerVisible(true)
        }



        GlobalScope.launch(Dispatchers.IO) {
            val temp = auth.currentUser?.uid?.let { userDao.getUserById(it).await().toObject(Users::class.java) }
            if(temp != null) {
                curUser = temp
                displayName = curUser.displayName.toString()
            }
        }

        homeNavigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            menuItem.isChecked = true
            homedrawerLayout.closeDrawer(GravityCompat.START)

            when(menuItem.itemId){
                R.id.help -> {
                    val intent = Intent(activity, CourseEnquiryActivity::class.java)
                    startActivity(intent)
                }
                R.id.MyCourses -> {
                    val intent = Intent(activity, MyCourseActivity::class.java)
                    startActivity(intent)
                }
                R.id.certificate -> {
                    val intent = Intent(activity, CertificateActivity::class.java)
                    startActivity(intent)
                }
                R.id.LogOut ->{
                    val gso =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

                    val googleSignInClient = activity?.let { GoogleSignIn.getClient(it, gso) }
                    googleSignInClient?.signOut()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            FirebaseAuth.getInstance()
                                .signOut() // very important if you are using firebase.
                            val login_intent = Intent(FacebookSdk.getApplicationContext(),
                                SignInActivity::class.java)
                            login_intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // clear previous task (optional)
                            startActivity(login_intent)
                            activity?.finish()
                        }
                    }
                }
                R.id.privacyPolicy -> {
                    val intent = Intent(activity, PrivacyPolicyActivity::class.java)
                    startActivity(intent)
                }

                R.id.share -> {
                    auth.currentUser?.let { createReferLink(it.uid, "1234533dsd") }
                }

                R.id.bookmark ->
                {
                    val intent = Intent(activity, BookmarkActivity::class.java)
                    startActivity(intent)
                }

                R.id.appliedJobs -> {
                    val intent = Intent(activity, MyJobsActivity::class.java)
                    startActivity(intent)
                }
                R.id.TermsCondition -> {
                    val intent = Intent(activity, TermsAndConditionActivity::class.java)
                    startActivity(intent)
                }

                R.id.myevents -> {
                    val intent = Intent(activity, MyEventsActivity::class.java)
                    startActivity(intent)
                }

            }
            menuItem.isChecked = false


            true
        }

        // setUp Navigation header Profile click
        val headerView: View = homeNavigationView.getHeaderView(0)
        headerView.findViewById<MaterialCardView>(R.id.profile).setOnClickListener {
            var intent = Intent(activity, UserDetailsActivity::class.java)
            intent.putExtra("username", displayName )
            intent.putExtra("id", FirebaseAuth.getInstance().currentUser?.uid.toString())
            intent.putExtra("parent", "MoreFragment")
            startActivity(intent)
        }
        setUpNavigationDrawerListener()
        setUpRecyclerView()
        return view
    }


    private fun setUpNavigationDrawerListener() {
        homedrawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
                onNavDrawerListener.onDrawerVisible(true)
            }

            override fun onDrawerClosed(drawerView: View) {
                onNavDrawerListener.onDrawerVisible(false)
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })
    }

    private fun setUpNavigationViewHeader(activity: Activity) {
        val navigationView = activity.findViewById<NavigationView>(R.id.homeNavigationView)
        val headerView: View = navigationView.getHeaderView(0)
        val username: TextView = headerView.findViewById(R.id.username)
        val email: TextView = headerView.findViewById(R.id.email)
        var profile: ImageView = headerView.findViewById(R.id.headerProfileImage)
        val coins: TextView = headerView.findViewById(R.id.coins)
        val profileCompleted: TextView = headerView.findViewById(R.id.profileCompleted)

        setDetails(username, email)
        GlobalScope.launch(Dispatchers.IO) {
            val temp = auth.currentUser?.let { userDao.getUserById(it.uid).await().toObject(Users::class.java) }
            if(temp != null) {
                withContext(Dispatchers.Main) {
                    coins.text = temp.coins.toString()
                    val profilePrecentage: String = (profileComplete(temp)*10).toString() + "%"
                    profileCompleted.text = profilePrecentage
                    val profileImage = temp.userImage
                    if(profileImage.trim().isNotBlank() && profileImage != "null") {
                        profile.setImageResource(R.drawable.image_circle)
                        view?.context?.let { Glide.with(it).load(profileImage).circleCrop().into(profile) }
                    }

                }
            }
        }
    }

    private fun setDetails(username: TextView, email: TextView) {
        username.text = CONSTANTS.getUsername()
        email.text = CONSTANTS.getEmail()
    }

    // Calculating Profile Completeness
    private fun profileComplete(user: Users): Int {
        var ans = 0

        if(user.displayName?.trim()?.isNotEmpty() == true){ ans++ }
        if(user.full_name.trim().isNotEmpty()){ ans++ }
        if(user.college_name.trim().isNotEmpty()){ ans++ }
        if(user.mobileNo.trim().isNotEmpty()){ ans++ }
        if(user.address.trim().isNotEmpty()){ ans++ }
        if(user.education.trim().isNotEmpty()){ ans++ }
        if(user.job.trim().isNotEmpty()){ ans++ }
        if(user.email.trim().isNotEmpty()){ ans++ }
        if(user.skills.trim().isNotEmpty()){ ans++ }
        if(user.experienceDesc.trim().isNotEmpty()){ ans++ }

        return ans
    }

    // create referral link
    private fun createReferLink(uId: String, productId: String) {
        var link: String = "https://uptoskill.page.link/?"+
                "link=https://www.uptoskill.com/myrefer.php?uId="+uId+"-"+productId+
                "&apn="+activity?.packageName+
                "&st=Join me on UptoSkills"+
                "&sd=Reward UsCash 250"+
                "&si=https://www.uptoskills.com/wp-content/uploads/2019/10/logo-dark.png"

        // shorten the link
        val msg = "Hey! I have a wonderful gift for you. Enroll UptoSkills Value  added Skill Courses & Avail EXTRA ₹ 100 OFF on your EVERY Paid Course. Click on this link to enjoy referral benefits.\nDownload the app: \n"

        val shortLinkTask = activity?.let {
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://uptoskill.page.link") // Set parameters
                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(it
                ) { task ->
                    if (task.isSuccessful) {
                        // Short link created
                        val shortLink = task.result.shortLink
                        val flowchartLink = task.result.previewLink

                        val intent = Intent()
                        intent.action = Intent.ACTION_SEND
                        intent.putExtra(Intent.EXTRA_TEXT, msg + shortLink.toString())
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

    override fun onStart() {
        super.onStart()
        activity?.let { setUpNavigationViewHeader(it) }
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    private fun setUpRecyclerView() {
        eventDao = EventDao()
        val collection = eventDao.postCollections
        val query = collection.orderBy("id", Query.Direction.DESCENDING)
        val config: PagedList.Config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(10)
            .setPageSize(3)
            .build()
        val recyclerViewOptions = FirestorePagingOptions.Builder<Events>().setQuery(query, config, Events::class.java).build()

        adapter = EventsAdapter(recyclerViewOptions, this, R.layout.event_item)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view?.context)
        progressBar.visibility = View.GONE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EventFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(event: Events) {
        val intent = Intent(activity, EventViewActivity::class.java)
        intent.putExtra("eventId", event.id)
        startActivity(intent)
    }
}