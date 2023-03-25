package android.example.uptoskills.models
/*
* paidCourses: HashMap<CourseID, CourseStatus> i.e. if != no then certificate link
* */
data class Users(
    val achievements:String = "",
    val projectTitle: String = "",
    val projectDesc: String = "",
    val skills: String="",
    val address: String = "",
    val experiencetitle: String = "",
    val experienceDesc: String = "",
    var freecourses: HashMap<String, String>? = HashMap(),
    var paidcourses: HashMap<String, String>? = HashMap(),
    var full_name: String="",
    var displayName: String? ="",
    var email: String="",
    var college_name: String="",
    var education: String="",
    var job: String="",
    var userImage: String="",
    var mobileNo: String="",
    val id: String="",
    var resume: String="",
    var referCode: String="",
    var coins: Int = 0,
    val bookmarks: HashMap<String, String>? = HashMap(),
    val appliedJobs: HashMap<String, UserJobDetails>? = HashMap(),
    val events: ArrayList<String> = ArrayList(),
    var summary: String = ""
    )
