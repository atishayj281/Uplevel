package android.example.uptoskills.models

data class FreeCourse (
    val category: String="",
    val certificate: Boolean=false,
    val course_description: String="",
    val course_duration: String="",
    val course_image:String="",
    val course_name: String="",
    val curriculum: String="",
    val enrolledStudents: ArrayList<String> = ArrayList(),
    val id: String="",
    val language: String="",
    val lectures: Int=0,
    val mentor_name: String="",
    val price: Int=0,
    val videos: ArrayList<Video> = ArrayList(),
    val rating: Float = 4.6f,
    val other_details: String = ""
)