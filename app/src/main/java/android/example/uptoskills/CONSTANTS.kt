package android.example.uptoskills

import android.content.Context
import android.content.SharedPreferences

object CONSTANTS {
    private var SHARED_PREFS = "uptoskills_prefs"
    private var sharedPreferences: SharedPreferences? = null

    fun getInstance(context: Context): SharedPreferences? {
        if(sharedPreferences == null)
            sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences
    }

    fun getEmail(): String {
        return sharedPreferences?.getString("email", "").toString();
    }
    fun getUsername(): String {
        return sharedPreferences?.getString("username", "").toString();
    }
    fun setEmail(email: String) {
        sharedPreferences?.edit()?.putString("email", email)?.apply()
    }
    fun setUsername(username: String) {
        sharedPreferences?.edit()?.putString("username", username)?.apply()
    }
}