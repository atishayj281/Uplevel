package android.example.uptoskills.Fragment

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.app.Activity
import android.content.Intent
import android.example.uptoskills.*
import android.example.uptoskills.Adapters.CourseViewPagerAdapter
import android.example.uptoskills.daos.CourseDao
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.models.Users
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.facebook.FacebookSdk
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CourseFragment: Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var auth: FirebaseAuth
    private lateinit var userDao: UsersDao
    private var curUser: Users = Users()

    private lateinit var hometoolBar: MaterialToolbar
    private lateinit var homedrawerLayout: DrawerLayout
    private lateinit var homeNavigationView: NavigationView
    private var displayName: String = ""
    private lateinit var search: TextView
    private val categoryItems = ArrayList<String>()
    private lateinit var spinnerDialog: SpinnerDialog

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
        val view =  inflater.inflate(R.layout.fragment_course, container, false)


        userDao = UsersDao()
        hometoolBar = view.findViewById(R.id.hometoolBar)
        homedrawerLayout = view.findViewById(R.id.homedrawerLayout)
        homeNavigationView = view.findViewById(R.id.homeNavigationView)
        search = view.findViewById(R.id.search)
        auth = FirebaseAuth.getInstance()


        tabLayout = view.findViewById(R.id.courseTabLayout)
        viewPager = view.findViewById(R.id.courseViewPager)
        tabLayout.post { tabLayout.setupWithViewPager(viewPager) }



//        auth.currentUser?.let { userDao.ref.child(it.uid).addValueEventListener(object :
//            ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                snapshot.getValue(Users::class.java)?.also { curUser = it }
//                displayName = curUser.displayName.toString()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        }) }
        GlobalScope.launch(Dispatchers.IO) {
            val temp = auth.currentUser?.uid?.let { userDao.getUserById(it).await().toObject(Users::class.java) }
            if(temp != null) {
                curUser = temp
                displayName = curUser.displayName.toString()
            }
        }

        hometoolBar.setOnClickListener {
            homedrawerLayout.openDrawer(GravityCompat.START)
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

        val freeCourse = FreeCourseFragment()
        val paidCourse = PaidCourseFragment()
        var adapter = CourseViewPagerAdapter(parentFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        adapter.addFragment(freeCourse, "Free")
        adapter.addFragment(paidCourse, "Paid")
        viewPager.adapter = adapter

        // Adding the category items
        categoryItems.add("Software Development");
        categoryItems.add("Coding");
        categoryItems.add("Design");
        categoryItems.add("Competitive Programming");
        categoryItems.add("Android Development");
        categoryItems.add("Google");

        spinnerDialog =
            SpinnerDialog(activity,categoryItems,"Select or Search Category",R.style.DialogAnimations_SmileWindow,"Close")

        spinnerDialog.setCancellable(true); // for cancellable
        spinnerDialog.setShowKeyboard(false);// for open keyboard by default

        spinnerDialog.bindOnSpinerListener { item, position ->
            freeCourse.updateRecyclerView(item)
            paidCourse.updateRecyclerView(item)
            search.text = item
        }

        search.setOnClickListener {
            spinnerDialog.showSpinerDialog()
        }

        return view
    }


    private fun setUpNavigationViewHeader(activity: Activity) {
        val navigationView = activity.findViewById<NavigationView>(R.id.homeNavigationView)
        val headerView: View = navigationView.getHeaderView(0)
        val username: TextView = headerView.findViewById(R.id.username)
        val email: TextView = headerView.findViewById(R.id.email)
        var profile: ImageView = headerView.findViewById(R.id.headerProfileImage)
        val coins: TextView = headerView.findViewById(R.id.coins)
        val profileCompleted: TextView = headerView.findViewById(R.id.profileCompleted)

        GlobalScope.launch(Dispatchers.IO) {
            val temp = auth.currentUser?.let { userDao.getUserById(it.uid).await().toObject(Users::class.java) }
            if(temp != null) {
                withContext(Dispatchers.Main) {
                    username.text = temp.displayName
                    displayName = temp.displayName.toString()
                    email.text = temp.email
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
        var link: String = "https://uptoskills.page.link/?"+
                "link=https://www.uptoskills.com/myrefer.php?uId="+uId+"-"+productId+
                "&apn="+activity?.packageName+
                "&st=Join me on UptoSkills"+
                "&sd=Reward UsCash 500"+
                "&si=https://www.uptoskills.com/wp-content/uploads/2019/10/logo-dark.png"

        // https://uptoskills.page.link?apn=android.example.getwork&ibi=com.example.ios&link=https%3A%2F%2Fwww.uptoskills.com%2F
        Log.e("sharelink", link)
        // shorten the link
        val msg = "Hey! I have a wonderful gift for you. Enroll UptoSkills Value  added Skill Courses & Avail EXTRA ??? 100 OFF on your EVERY Paid Course. Click on this link to enjoy referral benefits.\nDownload the app: \n"

        val shortLinkTask = activity?.let {
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://uptoskills.page.link") // Set parameters
                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(it
                ) { task ->
                    if (task.isSuccessful) {
                        // Short link created
                        val shortLink = task.result.shortLink
                        val flowchartLink = task.result.previewLink
                        Log.e("short link", msg+shortLink)

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
//                    Log.e("long link", ""+dynamicLinkUri)
    }

    override fun onStart() {
        super.onStart()
        activity?.let { setUpNavigationViewHeader(it) }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}