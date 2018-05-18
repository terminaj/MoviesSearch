package com.sivakumarc.moviesearch.data

import com.sivakumarc.moviesearch.data.remote.MovieAPI
import com.sivakumarc.moviesearch.model.Movie
import com.sivakumarc.moviesearch.model.MovieResponse
import com.sivakumarc.moviessearch.data.local.MovieDB
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieRepository @Inject
constructor(factory: RepositoryFactory) {
    private val movieAPI: MovieAPI = factory.createMovieAPI()
    private val movieDB: MovieDB = factory.createMovieDB()

    fun getPopMovies(page: Int): Single<List<Movie>> {
        return getMovies(movieAPI.getPopularMovies(page))
    }

    fun searchMovies(query: String, page: Int): Single<List<Movie>> {
        return getMovies(movieAPI.searchMovies(query, page))
    }

    fun getFavMovies(): Single<List<Movie>> {
        return movieDB.getFavoriteMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
    }

    fun saveMovie(movie: Movie): Completable {
        return movieDB.saveMovie(movie)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
    }

    fun deleteMovie(movie: Movie): Completable {
        return movieDB.deleteMovie(movie)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
    }

    private fun getMovies(movieResponseSingle: Single<MovieResponse>): Single<List<Movie>> {
        return movieResponseSingle.flatMap { movieResponse -> Single.just<List<Movie>>(movieResponse.results) }
            .subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io())
    }
}
