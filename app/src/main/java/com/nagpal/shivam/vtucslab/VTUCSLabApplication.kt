package com.nagpal.shivam.vtucslab

import androidx.multidex.MultiDexApplication
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nagpal.shivam.vtucslab.repositories.VtuCsLabRepository
import com.nagpal.shivam.vtucslab.repositories.VtuCsLabRepositoryImpl
import com.nagpal.shivam.vtucslab.services.VtuCsLabService

class VTUCSLabApplication : MultiDexApplication() {
    val vtuCsLabRepository: VtuCsLabRepository =
        VtuCsLabRepositoryImpl(this, VtuCsLabService.instance)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }
    }
}
