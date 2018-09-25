package com.sivakumarc.moviesearch.data

import com.sivakumarc.moviesearch.data.remote.MovieAPI
import com.sivakumarc.moviesearch.model.Movie
import com.sivakumarc.moviesearch.model.MovieResponse
import com.sivakumarc.moviessearch.data.local.MovieTable
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieRepository @Inject
constructor(factory: RepositoryFactory) {
    private val entityAPI: MovieAPI = factory.createMovieAPI()
    private val entityDB: MovieTable = factory.createMovieDB()

    fun getMovies(query: String, page: Int): Single<List<Movie>> {
        return getMovies(entityAPI.getMovies(query, page))
    }

    fun getFavMovies(): Single<List<Movie>> {
        return entityDB.getFavoriteMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
    }

    fun saveMovie(entity: Movie): Completable {
        return entityDB.insertMovie(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
    }

    fun deleteMovie(entity: Movie): Completable {
        return entityDB.deleteMovie(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
    }

    private fun getMovies(entityResponseSingle: Single<MovieResponse>): Single<List<Movie>> {
        return entityResponseSingle.flatMap { response ->
            Single.just<List<Movie>>(response.results)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
    }
}