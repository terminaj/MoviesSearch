package com.sivakumarc.moviesearch.data

import com.sivakumarc.moviesearch.data.remote.MovieAPI
import com.sivakumarc.moviessearch.data.local.MovieTable
import com.sivakumarc.moviessearch.data.local.MoviesDB
import retrofit2.Retrofit
import javax.inject.Inject

class RepositoryFactory @Inject
constructor(private val retrofit: Retrofit, private val database: MoviesDB) {

  fun createMovieAPI(): MovieAPI {
    return MovieAPI(retrofit)
  }

  fun createMovieDB(): MovieTable {
    return MovieTable(database)
  }

}
