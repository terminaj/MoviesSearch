package com.sivakumarc.moviessearch.di.modules

import android.graphics.Movie
import com.sivakumarc.moviesearch.MovieDetailFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FragmentBuildersModule {
  @ContributesAndroidInjector
  fun detailFragment(): MovieDetailFragment

//  @ContributesAndroidInjector
//  internal abstract fun MoviesFragment(): MoviesListFragment
}
