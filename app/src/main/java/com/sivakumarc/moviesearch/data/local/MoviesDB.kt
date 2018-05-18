package com.sivakumarc.moviessearch.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.sivakumarc.moviesearch.model.Movie

@Database(entities = arrayOf(Movie::class), version = 1)
abstract class MoviesDB : RoomDatabase() {
  abstract fun movieDao(): MovieDao
}
