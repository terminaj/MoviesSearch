package com.sivakumarc.moviessearch.data.local

import android.arch.persistence.room.*
import com.sivakumarc.moviesearch.model.Movie
import io.reactivex.Flowable

@Dao
interface MovieDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(movieDetail: Movie)

  @Query("SELECT * FROM movies WHERE favorite = 1")
  fun getFavoriteMovies() : Flowable<List<Movie>>

  @Delete
  fun delete(movieDetailDetail: Movie)

}
