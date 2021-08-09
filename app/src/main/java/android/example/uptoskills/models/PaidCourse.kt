package android.example.uptoskills.models

data class PaidCourse(val category: String="",
                      val certificate: Boolean=false,
                      val course_description: String="",
                      val course_duration: String="",
                      val course_image:String="",
                      val course_name: String="",
                      val curriculum: String="",
                      val enrolledStudents: HashMap<String, String> = HashMap(),
                      val id: String="",
                      val language: String="",
                      val lectures: Int=0,
                      val mentor_name: String="",
                      val price: Int=0
                      )