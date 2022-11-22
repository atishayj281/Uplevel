package android.example.uptoskills

import android.content.Intent
import android.example.uptoskills.Adapters.QuesFillAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class QuestionActivity : AppCompatActivity() {
    private lateinit var quesText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: QuesFillAdapter
    private lateinit var backbtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        recyclerView = findViewById(R.id.quesRecyclerView)
        backbtn = findViewById(R.id.back)
        backbtn.setOnClickListener {
            if(parent == null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            finish()
        }

        val bundle: Bundle? = intent.getBundleExtra("ques")
        var questions: ArrayList<String>? = bundle?.get("Ques") as ArrayList<String>?
        if(questions != null) {
            fillActivity(questions)
        }
    }

    private fun fillActivity(questions: ArrayList<String>) {
        adapter = QuesFillAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.updateQues(questions)
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