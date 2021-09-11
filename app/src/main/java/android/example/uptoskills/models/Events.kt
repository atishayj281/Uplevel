package android.example.uptoskills.models

data class Events(
    val description: String="",
    val id: String="",
    val image: String="",
    val title: String="",
    val enrolled: ArrayList<String> = arrayListOf()
)