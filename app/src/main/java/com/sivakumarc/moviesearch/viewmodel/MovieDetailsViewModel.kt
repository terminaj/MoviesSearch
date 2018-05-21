package com.sivakumarc.moviesearch.viewmodel

import android.arch.lifecycle.ViewModel
import com.sivakumarc.moviesearch.data.MovieRepository
import com.sivakumarc.moviesearch.model.Movie
import io.reactivex.CompletableObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieDetailsViewModel @Inject
constructor(
    private val movieRepository: MovieRepository,
    private val movieSubject: PublishSubject<Movie>
) : ViewModel() {

    private val disposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun addFavorite(movie: Movie) {
        movie.favorite = 1
        movieRepository.saveMovie(movie).subscribe(MovieCompletableObserver(movie))
    }

    fun removeFavorite(movie: Movie) {
        movie.favorite = 0
        movieRepository.deleteMovie(movie).subscribe(MovieCompletableObserver(movie))
    }

    private inner class MovieCompletableObserver
    internal constructor(private val movie: Movie) : CompletableObserver {

        override fun onSubscribe(d: Disposable) {
            disposable.add(d)
        }

        override fun onComplete() {
            movieSubject.onNext(movie)
        }

        override fun onError(e: Throwable) {
            Timber.e(e)
        }
    }
}