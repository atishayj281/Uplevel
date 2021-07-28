package android.example.uptoskills.daos

import android.content.ContentResolver
import android.content.Context
import android.example.uptoskills.models.Users
import android.net.Uri
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UsersDao {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref = database.getReference("users")
    private val storageReference = FirebaseStorage.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    fun addUser(user: Users?, id: String) {
        user?.let {

            GlobalScope.launch(Dispatchers.IO) {
                //usersCollection.document(id).set(it)
                ref.child(id).setValue(it)
            }
        }
    }

    fun getUserById(uId: String): Task<DataSnapshot> {
        return ref.child(uId).get()
    }

    fun updateUser(user: Users, id: String){
        GlobalScope.launch {
            ref.child(id).setValue(user)

        }
    }

    fun uploadProfileImage(imageUri: Uri,user: Users, context: Context, id: String) {
        var fileRef: StorageReference = storageReference.child("users/"+auth.currentUser?.uid+"/profile."+getFileExtension(imageUri, context))
        fileRef.putFile(imageUri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener {
                user.userImage = it.toString()
                updateUser(user, id)
                Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_SHORT)
            }
        }.addOnProgressListener {

        }.addOnFailureListener{
            Toast.makeText(context, "Uploading Failed...", Toast.LENGTH_SHORT).show()
        }
    }


    // get Extension of the imageFile to be Uploaded
    fun getFileExtension(imageUri: Uri, context: Context): String{
        var contentResolver: ContentResolver = context.contentResolver
        var mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(imageUri)).toString()
    }

    fun uploadResumeWithImage(resumeUri: Uri,imageUri: Uri, user: Users, context: Context, id: String) {
        var updatedUser = uploadResume(resumeUri, user, context, id)
        uploadProfileImage(imageUri, user, context, id)

    }

    fun uploadResume(resumeUri: Uri, user: Users, context: Context, id: String): Users {

        var fileRef: StorageReference = storageReference.child("users/"+auth.currentUser?.uid+"/"+user.displayName+"Resume."+getFileExtension(resumeUri, context))
        fileRef.putFile(resumeUri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener {
                user.resume = it.toString()
                auth.currentUser?.uid?.let { it1 -> updateUser(user, it1) }
            }
        }.addOnProgressListener {

        }.addOnFailureListener{
            Toast.makeText(context, "Uploading Failed...", Toast.LENGTH_SHORT).show()
        }
        return user
    }

    fun getResumeUri(): String{
        var resumeUri: String = ""
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var ref = snapshot.child(auth.currentUser?.uid.toString())
                resumeUri = ref.child("resume").getValue(String::class.java).toString()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return resumeUri
    }

    fun getProfileImageUri(): String{
        var ImageUri: String = ""
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var ref = snapshot.child(auth.currentUser?.uid.toString())
                ImageUri = ref.child("userImage").getValue(String::class.java).toString()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return ImageUri
    }
}