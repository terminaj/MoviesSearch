package com.sivakumarc.moviessearch.di.modules

import com.sivakumarc.moviesearch.MovieDetailActivity
import com.sivakumarc.moviesearch.MovieListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module abstract class ActivitiesModule {

  @ContributesAndroidInjector(modules = arrayOf(FragmentBuildersModule::class))
  internal abstract fun contributeMovieListActivity(): MovieListActivity

  @ContributesAndroidInjector(modules = arrayOf(FragmentBuildersModule::class))
  internal abstract fun contributeMovieDetailActivity(): MovieDetailActivity
}
