package com.sivakumarc.moviesearch.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sivakumarc.moviesearch.data.MovieRepository
import com.sivakumarc.moviesearch.model.Movie
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class MoviesListViewModel @Inject
constructor(
    private val movieRepository: MovieRepository,
    private val movieSubject: PublishSubject<Movie>
) : ViewModel() {
    val searchMoviesLiveData = MutableLiveData<SearchData>()
    val favMoviesLiveData = MutableLiveData<List<Movie>>()
    private val disposable = CompositeDisposable()

    fun loadFavoriteMovies() {
        val d2 = movieRepository.getFavMovies().subscribe(Consumer<List<Movie>> { favMoviesLiveData.postValue(it) })
        disposable.add(d2)
    }

    fun searchMovies(query: String, page: Int){
        val d4 = movieRepository.searchMovies(query, page).subscribe(Consumer<List<Movie>> {
            searchMoviesLiveData.postValue(SearchData(query, it))
        })
        disposable.add(d4)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

class SearchData(val query: String, val movies: List<Movie>)
