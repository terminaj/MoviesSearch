package com.sivakumarc.moviesearch.model

import android.arch.persistence.room.Entity
import android.os.Parcelable
import com.sivakumarc.moviesearch.view.ViewConstants
import com.sivakumarc.moviesearch.view.ViewType
import kotlinx.android.parcel.Parcelize


data class MovieResponse(
    var page: Int = 0,
    var total_results: Int = 0,
    var total_pages: Int = 0,
    var results: List<Movie> = listOf()
)

@Parcelize
@Entity(tableName = "movies", primaryKeys = arrayOf("id"))
data class Movie(
    var vote_count: Int = 0,
    var id: Int = 0,
    var video: Boolean = false,
    var vote_average: Double = 0.0,
    var title: String = "",
    var popularity: Double = 0.0,
    var poster_path: String = "",
    var original_language: String = "",
    var original_title: String = "",
    var backdrop_path: String = "",
    var adult: Boolean = false,
    var overview: String = "",
    var release_date: String = "",
    var favorite: Int = 0
) : Parcelable, ViewType {
    override fun getViewType(): Int {
        return ViewConstants.MOVIES
    }

    fun isFavorite() = favorite == 1
}