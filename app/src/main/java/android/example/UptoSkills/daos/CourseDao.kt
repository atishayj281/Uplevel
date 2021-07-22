package android.example.UptoSkills.daos

import android.content.Context
import android.example.UptoSkills.models.Job
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class CourseDao {



//    fun getCourse(context: Context){
//        val url = "https://remotive.io/api/remote-jobs?category=software-dev"
//        val queue = Volley.newRequestQueue(context)
//        val jobArray: ArrayList<Job> = ArrayList()
//        val jsonObjectRequest =
//            JsonObjectRequest(Request.Method.GET, url, null, object : Response.Listener<JSONObject?> {
//                override fun onResponse(response: JSONObject?) {
//                    try {
//                        val jobjsonArray = response?.getJSONArray("jobs")
//                        if (jobjsonArray != null) {
//                            var n = jobjsonArray.length() - 1
//                            for (i in 0..n) {
//                                val jobJSONObject = jobjsonArray.getJSONObject(i)
//                                var job = Job(jobJSONObject.getString("id"),
//                                    jobJSONObject.getString("url"),
//                                    jobJSONObject.getString("title"),
//                                    jobJSONObject.getString("company_name"),
//                                    jobJSONObject.getString("category"),
//                                    jobJSONObject.getString("job_type"),
//                                    jobJSONObject.getString("publication_date"),
//                                    jobJSONObject.getString("candidate_required_location"),
//                                    jobJSONObject.getString("description"),
//                                    jobJSONObject.getString("company_logo_url")
//                                )
//                                jobArray.add(job)
//                            }
//
//                        }
//                    } catch (e: JSONException) {
//                        Toast.makeText(context, "jobs Updated", Toast.LENGTH_SHORT).show()
//                    } finally {
//                        adapter.updateJobs(jobArray)
//                    }
//                }
//            }, object : Response.ErrorListener {
//                override fun onErrorResponse(error: VolleyError?) {
//                    Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
//                }
//            })
//        queue.add(jsonObjectRequest)
//    }

}