package android.example.uptoskills

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.RequestQueue

import android.widget.EditText

import android.widget.ImageButton

import androidx.recyclerview.widget.RecyclerView
import android.example.uptoskills.Adapters.MessageRVAdapter

import android.example.uptoskills.models.MessageModal
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.appevents.codeless.internal.UnityReflection.sendMessage
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject
import android.view.ViewTreeObserver.OnGlobalLayoutListener

import android.example.uptoskills.R
import android.util.Log
import android.widget.ImageView


class ChatbotActivity : AppCompatActivity() {


    // creating variables for our
    // widgets in xml file.
    private lateinit var chatsRV: RecyclerView
    private lateinit var sendMsgIB: ImageButton
    private lateinit var userMsgEdt: EditText
    private lateinit var back: ImageView
    private val USER_KEY = "user"
    private val BOT_KEY = "bot"
    private lateinit var auth: FirebaseAuth

    // creating a variable for
    // our volley request queue.
    private lateinit var mRequestQueue: RequestQueue

    // creating a variable for array list and adapter class.
    private lateinit var messageModalArrayList: ArrayList<MessageModal>
    private lateinit var messageRVAdapter: MessageRVAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        // on below line we are initializing all our views.
        chatsRV = findViewById(R.id.idRVChats)
        sendMsgIB = findViewById(R.id.idIBSend)
        userMsgEdt = findViewById(R.id.idEdtMessage)
        back = findViewById(R.id.back)
        auth = FirebaseAuth.getInstance()

        //Back to the MainActivity
        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // below line is to initialize our request queue.
        mRequestQueue = Volley.newRequestQueue(this)
        mRequestQueue.cache.clear()

        // creating a new array list
        messageModalArrayList = ArrayList()

        // adding on click listener for send message button.
        sendMsgIB.setOnClickListener{
            // checking if the message entered
            // by user is empty or not.
            if (userMsgEdt.text.toString().isEmpty()) {
                // if the edit text is empty display a toast message.
                Toast.makeText(this, "Please enter your message..", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // calling a method to send message
            // to our bot to get response.
            sendMessage(userMsgEdt.text.toString())

            // below line we are setting text in our edit text as empty
            userMsgEdt.setText("")
        }

        // on below line we are initialing our adapter class and passing our array list to it.
        messageRVAdapter = MessageRVAdapter(messageModalArrayList, this)

        // below line we are creating a variable for our linear layout manager.
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        // below line is to set layout
        // manager to our recycler view.
        chatsRV.layoutManager = linearLayoutManager

        // below line we are setting
        // adapter to our recycler view.
        chatsRV.adapter = messageRVAdapter

        val activityRootView = findViewById<View>(R.id.activityRoot)
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = activityRootView.rootView.height - activityRootView.height
            if (heightDiff > 100) {
                try {
                    chatsRV.smoothScrollToPosition(messageRVAdapter.itemCount - 1)
                } catch (e: Exception) {
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sendMessage(userMsg: String) {
        messageModalArrayList.add(MessageModal(userMsg, USER_KEY))
        messageRVAdapter.notifyDataSetChanged()

        val url = "http://api.brainshop.ai/get?bid=161640&key=eAgWHsOr0OShIPZp&uid=${auth.currentUser?.uid}&msg=${userMsgEdt.text.toString().trim()}"

        // creating a variable for our request queue.
        val queue: RequestQueue = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, {
            try {
                // in on response method we are extracting data
                // from json response and adding this response to our array list.
                val botResponse: String = it.getString("cnt")
                messageModalArrayList.add(MessageModal(botResponse, BOT_KEY))

                // notifying our adapter as data changed.
                messageRVAdapter.notifyDataSetChanged()
            } catch (e: JSONException) {
                e.printStackTrace()

                // handling error response from bot.
                messageModalArrayList.add(MessageModal("No response", BOT_KEY))
                messageRVAdapter.notifyDataSetChanged()
            }
        }, {
            messageModalArrayList.add(MessageModal("Sorry no response found", BOT_KEY))
            Toast.makeText(this, "No response from the bot..", Toast.LENGTH_SHORT).show()
        })



        // on below line we are making a json object request for a get request and passing our url .

        // at last adding json object
        // request to our queue.
        queue.add(jsonObjectRequest)
    }

    override fun onBackPressed() {
        if(parent == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        super.onBackPressed()
    }

}