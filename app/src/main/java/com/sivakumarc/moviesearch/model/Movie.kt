package com.sivakumarc.moviesearch.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.os.Parcel
import android.os.Parcelable
import com.sivakumarc.moviesearch.view.ViewConstants
import com.sivakumarc.moviesearch.view.ViewType

@Entity(tableName = "movies", primaryKeys = arrayOf("id"))
data class Movie(
        var id : Int = 0,
        var title: String = "",
        var overview: String = "",
        var poster_path: String = "",
        var backdrop_path: String = "",
        var release_date: String = "",
        var vote_average: Double = 0.0,
        var favorite: Int = 0
): ViewType, Parcelable {
    override fun getViewType(): Int {
        return ViewConstants.MOVIES
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Movie> = object : Parcelable.Creator<Movie> {
            override fun createFromParcel(source: Parcel): Movie = Movie(source.readInt(),
                source.readString(),
                source.readString(),
                source.readString(),
                source.readString(),
                source.readString(),
                source.readDouble(),
                source.readInt())
            override fun newArray(size: Int): Array<Movie?> = arrayOfNulls(size)
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(title)
        dest.writeString(overview)
        dest.writeString(poster_path)
        dest.writeString(backdrop_path)
        dest.writeString(release_date)
        dest.writeDouble(vote_average)
        dest.writeInt(favorite)
    }

    fun isFavorite() = favorite == 1
}

data class MovieResponse(
    var page: Int,
    var results: List<Movie>,
    var total_results: Int,
    var total_pages: Int
)