package com.nagpal.shivam.vtucslab

import androidx.multidex.MultiDexApplication
import androidx.room.Room
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nagpal.shivam.vtucslab.data.local.AppDatabase
import com.nagpal.shivam.vtucslab.repositories.VtuCsLabRepository
import com.nagpal.shivam.vtucslab.repositories.VtuCsLabRepositoryImpl
import com.nagpal.shivam.vtucslab.services.VtuCsLabService
import com.nagpal.shivam.vtucslab.utilities.Constants.VTU_CS_LAB
import com.nagpal.shivam.vtucslab.utilities.StaticMethods

class VTUCSLabApplication : MultiDexApplication() {
    private lateinit var _db: AppDatabase
    val db: AppDatabase
        get() = _db

    private lateinit var _vtuCsLabRepository: VtuCsLabRepository
    val vtuCsLabRepository: VtuCsLabRepository
        get() = _vtuCsLabRepository

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }

        _db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, VTU_CS_LAB).build()

        val vtuCsLabService = VtuCsLabService.instance
        _vtuCsLabRepository =
            VtuCsLabRepositoryImpl(
                this,
                vtuCsLabService,
                _db.labResponseDao(),
                StaticMethods.moshi,
            )
    }
}
