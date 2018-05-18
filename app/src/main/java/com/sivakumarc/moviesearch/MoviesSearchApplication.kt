package com.sivakumarc.moviesearch

import android.app.Activity
import android.app.Application
import com.sivakumarc.moviessearch.di.AppInjector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

class MoviesSearchApplication : Application(), HasActivityInjector {

  @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

  override fun onCreate() {
    super.onCreate()
    AppInjector.init(this)

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }

  override fun activityInjector(): AndroidInjector<Activity> {
    return dispatchingAndroidInjector
  }
}
