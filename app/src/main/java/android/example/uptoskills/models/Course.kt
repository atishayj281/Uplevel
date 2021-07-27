package android.example.uptoskills.models

data class Course(val course_name: String="", val course_description: String="", val course_duration: String="",
                  val course_image:String="", val mentor_name: String="", val category: String="",
                  val language: String="", val lectures: Int=0, val certificate: Boolean=false,
                  val url: String="", val curriculum: String="", val price: String="")