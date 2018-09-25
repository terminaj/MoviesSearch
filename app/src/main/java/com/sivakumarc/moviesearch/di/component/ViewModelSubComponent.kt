package com.sivakumarc.moviessearch.di.component

import android.arch.lifecycle.ViewModel
import com.sivakumarc.moviesearch.viewmodel.MovieViewModel
import dagger.Subcomponent

@Subcomponent
interface ViewModelSubComponent {

  fun movieViewModel(): MovieViewModel

  @Subcomponent.Builder
  interface Builder {
    fun build(): ViewModelSubComponent
  }
}
