package com.sivakumarc.moviessearch.di.modules

import com.sivakumarc.moviesearch.MovieDetailFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class FragmentBuildersModule {
  @ContributesAndroidInjector
  internal abstract fun detailFragment(): MovieDetailFragment

//  @ContributesAndroidInjector
//  internal abstract fun moviesFragment(): MoviesListFragment
}
