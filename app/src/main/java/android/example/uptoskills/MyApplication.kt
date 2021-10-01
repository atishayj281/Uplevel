package android.example.uptoskills

import android.app.Application
import com.onesignal.OneSignal




class MyApplication: Application() {
    private val ONESIGNAL_APP_ID = "54cbe99a-8f86-42a9-9693-d9adbea98428"

    override fun onCreate() {
        super.onCreate()

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }
}