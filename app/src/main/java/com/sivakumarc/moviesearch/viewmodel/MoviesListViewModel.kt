package com.sivakumarc.moviesearch.viewmodel

import android.arch.lifecycle.MutableLiveData
import com.sivakumarc.moviesearch.data.MovieRepository
import com.sivakumarc.moviesearch.model.Movie
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoviesListViewModel @Inject
constructor(
        private val repository: MovieRepository,
        private val publishSubject: PublishSubject<Any>
) : BaseViewModel() {

    val listMoviesLiveData = MutableLiveData<List<Movie>>()
    val favMoviesLiveData = MutableLiveData<List<Movie>>()

    fun loadFavoriteMovies() {
        val d2 = repository.getFavMovies().subscribe(Consumer<List<Movie>> { favMoviesLiveData.postValue(it) })
        disposable.add(d2)
    }

    fun getMovies(query: String, page: Int) {
        val d4 = repository.getMovies(query, page).subscribe(Consumer<List<Movie>> {
            listMoviesLiveData.postValue(it)
        })
        disposable.add(d4)
    }

    fun addFavorite(entity: Movie) {
        entity.favorite = 1
        repository.saveMovie(entity).subscribe(MovieCompletableObserver(entity))
    }

    fun removeFavorite(entity: Movie) {
        entity.favorite = 0
        repository.deleteMovie(entity).subscribe(MovieCompletableObserver(entity))
    }

    private inner class MovieCompletableObserver
    internal constructor(private val entity: Movie) : CompletableObserver {

        override fun onSubscribe(d: Disposable) {
            disposable.add(d)
        }

        override fun onComplete() {
            publishSubject.onNext(entity)
        }

        override fun onError(e: Throwable) {
            Timber.e(e)
        }
    }
}