package android.example.uptoskills.models

/*
* selectionStatus: 0 - Pending, -1: Rejected, 1: Selected
* jobType: Job / Internship
*/

data class UserJobDetails(val jobType: String = "Job",
                          val selectionStatus: Int = 0)


