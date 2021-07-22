package android.example.UptoSkills.models


data class Blog (
    val text: String = "",
    val title: String="",
    val createdBy: GoogleUser = GoogleUser(),
    val createdAt: Long = 0L)