package android.example.uptoskills.models

data class Internship (val applied: HashMap<String, String> = HashMap(),
                       var candidate_required_location: String="",
                       var category:String="",
                       var company_logo_url: String="",
                       var company_name: String="",
                       var description: String="",
                       var id: String="",
                       var job_type:String="",
                       var publication_date: String="",
                       val basic_requirements: String="",
                       val preferred_requirements: String="",
                       var title: String="")