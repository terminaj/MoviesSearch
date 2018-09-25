package com.sivakumarc.moviessearch.di.modules

import com.sivakumarc.moviesearch.MovieDetailActivity
import com.sivakumarc.moviesearch.MovieListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivitiesModule {

  @ContributesAndroidInjector(modules = arrayOf(FragmentBuildersModule::class))
  fun contributeMovieListActivity(): MovieListActivity

  @ContributesAndroidInjector(modules = arrayOf(FragmentBuildersModule::class))
  fun contributeMovieDetailActivity(): MovieDetailActivity
}
