package com.sivakumarc.moviessearch.di.modules

import android.app.Application
import android.arch.lifecycle.ViewModelProvider
import android.arch.persistence.room.Room
import android.content.Context
import com.sivakumarc.moviesearch.BuildConfig
import com.sivakumarc.moviesearch.util.Constants.BASE_URL
import com.sivakumarc.moviessearch.data.local.MoviesDB
import com.sivakumarc.moviessearch.di.ViewModelFactory
import com.sivakumarc.moviessearch.di.component.ViewModelSubComponent
import dagger.Module
import dagger.Provides
import io.reactivex.subjects.PublishSubject
import okhttp3.Cache
import okhttp3.Interceptor.Chain
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(subcomponents = arrayOf(ViewModelSubComponent::class))
class AppModule(internal val application: Application) {

  @Provides
  @Singleton
  internal fun provideApplication(): Application {
    return application
  }

  @Provides
  @Singleton
  internal fun provideApplicationContext(): Context {
    return application.applicationContext
  }

  @Provides
  @Singleton
  internal fun provideOkHttpClient(): OkHttpClient {
    val networkInterceptor = { chain: Chain ->
      val original = chain.request()
      val originalHttpUrl = original.url()

      val url = originalHttpUrl.newBuilder()
              .addQueryParameter("api_key", BuildConfig.API_KEY)
              .build()

      val requestBuilder = original.newBuilder().url(url)

      val request = requestBuilder.build()
      chain.proceed(request)
    }
    val loggingInterceptor = HttpLoggingInterceptor { message -> Timber.tag("OkHttp").d(message) }
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

    //cache to provide offline support
    //server might need to implement last changed time to sync data only when there's a change
    //okHttpClient automatically implements last changed time stamp for client
    val cacheSize = 10 * 1024 * 1024
    val cache = Cache(application.cacheDir, cacheSize.toLong())

    return OkHttpClient.Builder()
            .addInterceptor(networkInterceptor)
            .addInterceptor(loggingInterceptor)
            .cache(cache)
            .connectTimeout(40, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.SECONDS)
            .build()
  }

  @Singleton
  @Provides
  internal fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {

    return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

            .build()
  }

  @Singleton
  @Provides
  internal fun providesGlobalSubject(): PublishSubject<Any> {
    return PublishSubject.create<Any>()
  }

  @Singleton
  @Provides
  internal fun provideDb(): MoviesDB {
    return Room.databaseBuilder(application, MoviesDB::class.java, "Movies.db").build()
  }

  @Singleton
  @Provides
  internal fun provideViewModelFactory(
          viewModelSubComponent: ViewModelSubComponent.Builder): ViewModelProvider.Factory {
    return ViewModelFactory(viewModelSubComponent.build())
  }
}