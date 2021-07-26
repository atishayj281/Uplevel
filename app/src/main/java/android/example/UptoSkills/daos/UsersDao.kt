package android.example.UptoSkills.daos

import android.content.ContentResolver
import android.content.Context
import android.example.UptoSkills.models.GoogleUser
import android.example.UptoSkills.models.Users
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
}