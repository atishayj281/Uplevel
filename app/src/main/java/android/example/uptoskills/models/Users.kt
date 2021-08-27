package android.example.uptoskills.models

data class Users(
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
    val appliedJobs: HashMap<String, String>? = HashMap()
)