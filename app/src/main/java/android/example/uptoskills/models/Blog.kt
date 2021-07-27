package android.example.uptoskills.models


data class Blog (
    val text: String = "",
    val title: String="",
    val createdBy: Users = Users(),
    val createdAt: Long = 0L)