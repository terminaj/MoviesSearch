package com.sivakumarc.moviesearch.viewmodel

import com.sivakumarc.moviesearch.data.MovieRepository
import com.sivakumarc.moviesearch.model.Movie
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieDetailsViewModel @Inject
constructor(
        private val repository: MovieRepository,
        private val publishSubject: PublishSubject<Any>
) : BaseViewModel() {

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