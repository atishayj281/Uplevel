package android.example.uptoskills

import android.app.Application
import com.onesignal.OneSignal




class MyApplication: Application() {
    private val ONESIGNAL_APP_ID = "09035203-d312-4d9f-8f64-d3a8e1e55e7b" // 09035203-d312-4d9f-8f64-d3a8e1e55e7b

    override fun onCreate() {
        super.onCreate()

        // Enable verbose OneSignal logging to debug issues if needed.
//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
//
//        // OneSignal Initialization
//        OneSignal.initWithContext(this)
//        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }
}