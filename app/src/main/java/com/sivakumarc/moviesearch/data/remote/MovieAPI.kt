package com.sivakumarc.moviesearch.data.remote

import com.sivakumarc.moviesearch.model.MovieResponse
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

class MovieAPI(retrofit: Retrofit) {
  private val service: MovieService

  init {
    service = retrofit.create(MovieService::class.java)
  }

  fun getMovies(query: String): Single<MovieResponse> {
    return service.getMovies(query)
  }

  fun getMovies(query: String, page: Int): Single<MovieResponse> {
    return service.getMovies(query, page)
  }

  internal interface MovieService {
    @GET("search/movie")
    fun getMovies(@Query("query") query: String): Single<MovieResponse>

    @GET("search/movie")
    fun getMovies(@Query("query") query: String, @Query("page") page: Int?): Single<MovieResponse>
  }
}