package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.Fragment.*
import android.example.uptoskills.daos.UsersDao
import android.example.uptoskills.databinding.ActivityMainBinding
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.facebook.FacebookSdk
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wessam.library.NetworkChecker


class MainActivity : AppCompatActivity(), onMenuItemSelectedListener{

    private lateinit var displayName: String
    private lateinit var binding: ActivityMainBinding
    private lateinit var userDao: UsersDao
    private lateinit var auth: FirebaseAuth
    private lateinit var profile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, HomeFragment())
        transaction.commit()

        setContentView(binding.root)

        userDao = UsersDao()
        auth = FirebaseAuth.getInstance()
        profile = findViewById(R.id.profileImage)

        binding.bottomNavigation.setOnNavigationItemSelectedListener {

            val transaction = supportFragmentManager.beginTransaction()
            when(it.itemId){
                R.id.home -> transaction.replace(R.id.container, HomeFragment())
                R.id.Job -> transaction.replace(R.id.container, JobFragment())
                R.id.courses -> transaction.replace(R.id.container, CourseFragment())
                R.id.Blog -> transaction.replace(R.id.container, BlogFragment())
            }

            transaction.commit()
            true
        }

        binding.hometoolBar.setOnClickListener {
            binding.homedrawerLayout.openDrawer(GravityCompat.START)
        }

        binding.homeNavigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            menuItem.isChecked = true
            binding.homedrawerLayout.closeDrawer(GravityCompat.START)

            when(menuItem.itemId){
                R.id.Job -> binding.bottomNavigation.selectedItemId = R.id.Job
                R.id.courses -> binding.bottomNavigation.selectedItemId = R.id.courses
                R.id.Blog -> {
                    binding.bottomNavigation.selectedItemId = R.id.Blog

                    }
                R.id.LogOut ->{
                    val gso =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

                    val googleSignInClient = GoogleSignIn.getClient(this, gso)
                    googleSignInClient.signOut()
                        .addOnCompleteListener(object : OnCompleteListener<Void?> {
                            override fun onComplete(task: Task<Void?>) {
                                if (task.isSuccessful) {
                                    FirebaseAuth.getInstance()
                                        .signOut() // very important if you are using firebase.
                                    val login_intent = Intent(FacebookSdk.getApplicationContext(),
                                        SignInActivity::class.java)
                                    login_intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // clear previous task (optional)
                                    startActivity(login_intent)
                                    finish()
                                }
                            }
                        })
                }
                R.id.privacyPolicy -> {
                    val intent = Intent(this, PrivacyPolicyActivity::class.java)
                    startActivity(intent)
                }

            }
            menuItem.isChecked = false


            true
        }

        // setUpProfileImage
        setUpProfileImage()



        profile.setOnClickListener {
            var intent = Intent(this, UserDetailsActivity::class.java)
            intent.putExtra("username", displayName )
            intent.putExtra("id", FirebaseAuth.getInstance().currentUser?.uid.toString())
            intent.putExtra("parent", "MoreFragment")
            startActivity(intent)
        }


        // setUp Navigation header Profile click
        val navigationView = findViewById<NavigationView>(R.id.homeNavigationView)
        val headerView: View = navigationView.getHeaderView(0)
        headerView.findViewById<MaterialCardView>(R.id.profile).setOnClickListener {
            var intent = Intent(this, UserDetailsActivity::class.java)
            intent.putExtra("username", displayName )
            intent.putExtra("id", FirebaseAuth.getInstance().currentUser?.uid.toString())
            intent.putExtra("parent", "MoreFragment")
            startActivity(intent)
        }


        //set NavigationViewheader
        setUpNavigationViewHeader()
    }

    // Method for delteting the user
    private fun deleteuser(email: String, password: String) {
        val user = FirebaseAuth.getInstance().currentUser

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        val credential = EmailAuthProvider.getCredential(email, password)
        Log.d("email", email)
        Log.d("passwrd", password)

        // Prompt the user to re-provide their sign-in credentials
        user?.reauthenticate(credential)?.addOnCompleteListener {
            if(it.isSuccessful){
                var id: String = user.uid
                user.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this, SignInActivity::class.java))
                            userDao.ref.child(id).removeValue().addOnSuccessListener {
                                Toast.makeText(this, "Successfully Deleted", Toast.LENGTH_SHORT).show()
                            }.addOnCanceledListener {
                                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                            }
                            finish()
                        } else {
                            Log.d("DeleteError", task.exception?.message.toString())
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Method for displaying no-internet Connection Available layout
    private fun isInternetConnected(): Boolean{
        if(!NetworkChecker.isNetworkConnected(this)) {
            return false
        }

        return true
    }

    private fun setUpProfileImage() {
        userDao = UsersDao()

        userDao.ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImage =
                    auth.currentUser?.uid?.let { snapshot.child(it).child("userImage").getValue(String::class.java).toString() }
                displayName = snapshot.child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("displayName").getValue(String::class.java).toString()
                if (profileImage != null) {
                    if(profileImage.isNotEmpty() && !profileImage.equals("null")){
                        profile.setImageResource(R.drawable.image_circle)
                        this@MainActivity.let { Glide.with(it).load(profileImage).circleCrop().into(profile) }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun setUpNavigationViewHeader() {
        val navigationView = findViewById<NavigationView>(R.id.homeNavigationView)
        val headerView: View = navigationView.getHeaderView(0)
        val username: TextView = headerView.findViewById(R.id.username)
        val email: TextView = headerView.findViewById(R.id.email)
        var profile: ImageView = headerView.findViewById(R.id.headerProfileImage)


        userDao.ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImage = snapshot.child(auth.currentUser!!.uid).child("userImage").getValue(String::class.java).toString()
                username.text = snapshot.child(auth.currentUser?.uid.toString()).child("displayName").getValue(String::class.java).toString()
                email.text = snapshot.child(auth.currentUser?.uid.toString()).child("email").getValue(String::class.java).toString()
                if(profileImage.isNotEmpty() && !profileImage.equals("null")){
                    profile.setImageResource(R.drawable.image_circle)
                    this@MainActivity.let { Glide.with(it).load(profileImage).circleCrop().into(profile) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


    }


    override fun onBackPressed() {
        if(binding.bottomNavigation.selectedItemId == R.id.home){
            super.onBackPressed()
        } else {
            binding.bottomNavigation.selectedItemId = R.id.home
        }

    }

    override fun onItemSelected(itemId: Int) {
        binding.bottomNavigation.selectedItemId = itemId
    }
}

