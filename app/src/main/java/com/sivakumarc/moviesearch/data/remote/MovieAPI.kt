package com.sivakumarc.moviesearch.data.remote

import com.sivakumarc.moviesearch.model.MovieResponse
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

class MovieAPI(retrofit: Retrofit) {
  private val movieService: MovieService

  init {
    movieService = retrofit.create(MovieService::class.java)
  }

  fun getPopularMovies(page: Int): Single<MovieResponse> {
    return movieService.getPopularMovies(page)
  }

  fun searchMovies(query: String, page: Int): Single<MovieResponse>{
    return movieService.searchMovies(query, page)
  }

  internal interface MovieService {
    @GET("movie/popular") fun getPopularMovies(@Query("page") page: Int): Single<MovieResponse>

    @GET("search/movie") fun searchMovies(@Query("query") query: String, @Query("page") page: Int?): Single<MovieResponse>
  }
}
