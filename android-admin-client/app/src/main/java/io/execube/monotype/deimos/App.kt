package io.execube.monotype.deimos

import android.app.Application
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.google.firebase.messaging.FirebaseMessaging
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class App: Application() {

  override fun onCreate() {
    super.onCreate()
    val crashlyticsKit = Crashlytics.Builder()
        .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
        .build()
    Fabric.with(this, crashlyticsKit)

    if (BuildConfig.DEBUG)
    // Timber.plant(Timber.DebugTree())
      Timber.plant((CrashReportingTree()))
    else {
      Timber.plant(CrashReportingTree())
    }
  }

  private class CrashReportingTree : Timber.Tree() {
    override fun log(
      priority: Int,
      tag: String?,
      message: String,
      throwable: Throwable?
    ) {

      if (priority == Log.VERBOSE || priority == Log.DEBUG) {
        return
      }

      Crashlytics.log(priority, tag, message)
      if (throwable != null) {
        if (priority == Log.ERROR) {
          Crashlytics.log(throwable.message)
        } else if (priority == Log.WARN) {
          Crashlytics.log(throwable.message)
        }
      }
    }

  }
}