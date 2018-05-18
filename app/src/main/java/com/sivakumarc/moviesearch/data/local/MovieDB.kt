package com.sivakumarc.moviessearch.data.local

import com.sivakumarc.moviesearch.model.Movie
import io.reactivex.Completable
import io.reactivex.Single

class MovieDB(moviesDB: MoviesDB) {

  private var movieDao: MovieDao = moviesDB.movieDao()

  fun saveMovie(movie: Movie): Completable {
    return Completable.create { completableEmitter ->
      movieDao.insert(movie)
      completableEmitter.onComplete()
    }
  }

  fun deleteMovie(movie: Movie): Completable {
    return Completable.create { completableEmitter ->
      movieDao.delete(movie)
      completableEmitter.onComplete()
    }
  }

  fun getFavoriteMovies(): Single<List<Movie>> = movieDao.getFavoriteMovies().onBackpressureBuffer().firstOrError()

}
