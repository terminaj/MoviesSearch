package com.sivakumarc.moviessearch.di.component

import com.sivakumarc.moviesearch.viewmodel.MovieDetailsViewModel
import com.sivakumarc.moviesearch.viewmodel.MoviesListViewModel
import dagger.Subcomponent

@Subcomponent interface ViewModelSubComponent {

  fun moviesViewModel(): MoviesListViewModel

  fun movieDetailsViewModel(): MovieDetailsViewModel

  @Subcomponent.Builder interface Builder {
    fun build(): ViewModelSubComponent
  }
}
