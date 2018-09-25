package com.sivakumarc.moviessearch.data.local

import android.arch.persistence.room.*
import com.sivakumarc.moviesearch.model.Movie
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


class MovieTable(database: MoviesDB) {

  private var entityDao: MovieDao = database.movieDao()

  fun insertMovie(entity: Movie): Completable {
    return Completable.create { completableEmitter ->
      entityDao.insert(entity)
      completableEmitter.onComplete()
    }
  }

  fun deleteMovie(entity: Movie): Completable {
    return Completable.create { completableEmitter ->
      entityDao.delete(entity)
      completableEmitter.onComplete()
    }
  }

  fun getFavoriteMovies(): Single<List<Movie>> = entityDao.getFavoriteMovies().onBackpressureBuffer().firstOrError()
}

@Dao
interface MovieDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(entity: Movie)

  @Query("SELECT * FROM movies WHERE favorite = 1")
  fun getFavoriteMovies(): Flowable<List<Movie>>

  @Delete
  fun delete(entity: Movie)

}