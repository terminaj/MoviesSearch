package com.sivakumarc.moviessearch.di.component

import android.app.Application
import com.sivakumarc.moviesearch.MoviesSearchApplication
import com.sivakumarc.moviessearch.di.modules.ActivitiesModule
import com.sivakumarc.moviessearch.di.modules.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AndroidInjectionModule::class, AppModule::class, ActivitiesModule::class))
interface AppComponent {

  fun inject(application: MoviesSearchApplication)

  @Component.Builder
  interface Builder {
    @BindsInstance
    fun application(application: Application): Builder

    fun appModule(appModule: AppModule): Builder
    fun build(): AppComponent
  }
}

