package android.example.uptoskills.models
/*
* selectionStatus: 0 - Pending, -1: Rejected, 1: Selected
*/
data class JobDetails(val email: String = "", var selectionStatus: Int=0, val candidate_name: String="",
                      val phone: String="")