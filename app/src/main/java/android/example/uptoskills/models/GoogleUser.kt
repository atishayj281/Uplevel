package android.example.uptoskills.models

data class GoogleUser(

                        val skills: String="",
                        val address: String = "",
                        val experience: Pair<String, String>,
                        var courses: HashMap<String, String>? = HashMap(),
                        val uid: String = "",
                      val displayName: String? = "",
                      val education: String="",
                      val job: String="",
                      val full_name: String="",
                      val email: String="",
                      val college_name: String="",
                      val userImage: String="",
                      val mobileNo: String="",
                      val resume: String=""
                      )