package com.sivakumarc.moviessearch.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.v4.util.ArrayMap
import com.sivakumarc.moviesearch.viewmodel.MovieViewModel
import com.sivakumarc.moviessearch.di.component.ViewModelSubComponent
import java.util.concurrent.Callable
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class ViewModelFactory @Inject
constructor(viewModelSubComponent: ViewModelSubComponent) : ViewModelProvider.Factory {
  private val creators: ArrayMap<Class<*>, Callable<out ViewModel>> = ArrayMap()

  init {
    creators[MovieViewModel::class.java] = Callable<ViewModel> { viewModelSubComponent.movieViewModel() }
  }

  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    var creator: Callable<out ViewModel>? = creators[modelClass]
    if (creator == null) {
      for ((key, value) in creators) {
        if (modelClass.isAssignableFrom(key)) {
          creator = value
          break
        }
      }
    }
    if (creator == null) {
      throw IllegalArgumentException("unknown model class " + modelClass)
    }
    try {
      return creator.call() as T
    } catch (e: Exception) {
      throw RuntimeException(e)
    }

  }
}