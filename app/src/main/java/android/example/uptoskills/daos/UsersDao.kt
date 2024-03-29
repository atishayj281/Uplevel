package android.example.uptoskills.daos

import android.content.ContentResolver
import android.content.Context
import android.example.uptoskills.models.Users
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UsersDao {

//    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
//    val ref = database.getReference("users")
    private val storageReference = FirebaseStorage.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    private val db = FirebaseFirestore.getInstance()
    val userCollection = db.collection("user")


    fun addBookMark(itemId: String, itemType: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val user = auth.currentUser?.uid?.let { getUserById(it).await().toObject(Users::class.java) }
            if(user != null) {
                if(user.bookmarks?.containsKey(itemId) == true) {
                    user.bookmarks.remove(itemId)
                } else {
                    user.bookmarks?.put(itemId, itemType)
                }
                auth.currentUser?.uid?.let { updateUser(user, it) }
            }
        }
    }

    fun addUser(user: Users?, id: String) {
        user?.let {

            GlobalScope.launch(Dispatchers.IO) {
                userCollection.document(id).set(it)
            }
        }
    }

    fun getUserById(uId: String): Task<DocumentSnapshot> {
        return userCollection.document(uId).get()
    }

    fun updateUser(user: Users, id: String){
        GlobalScope.launch {
            userCollection.document(id).set(user)
        }
    }

    fun uploadProfileImage(imageUri: Uri,user: Users, context: Context, id: String) {
        if(imageUri.toString().isNotBlank() && imageUri.toString().trim() != "null") {
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
        } else {
            addUser(user, id)
        }
    }


    // get Extension of the imageFile to be Uploaded
    fun getFileExtension(imageUri: Uri, context: Context): String{
        var contentResolver: ContentResolver = context.contentResolver
        var mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(imageUri)).toString()
    }

    fun uploadResumeWithImage(resumeUri: Uri, imageUri: Uri, user: Users?, context: Context, id: String) {
        if (user != null) {
            uploadResume(resumeUri, user, context, id)
            uploadProfileImage(imageUri, user, context, id)
        }

    }

    fun uploadResume(resumeUri: Uri, user: Users, context: Context, id: String): Users {
        if(resumeUri.toString().isNotBlank() && resumeUri.toString().trim() != "null"){
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
        } else {
            addUser(user, id)
        }

        return user
    }
}