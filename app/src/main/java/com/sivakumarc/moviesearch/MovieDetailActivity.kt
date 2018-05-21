package com.sivakumarc.moviesearch

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.sivakumarc.moviesearch.databinding.ActivityMovieDetailBinding
import com.sivakumarc.moviesearch.model.Movie
import com.sivakumarc.moviesearch.view.BaseActivity
import com.sivakumarc.moviesearch.viewmodel.MovieDetailsViewModel
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_movie_detail.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class MovieDetailActivity : BaseActivity() {
    @Inject
    lateinit internal var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit internal var movieSubject: PublishSubject<Movie>

    private lateinit var binding: ActivityMovieDetailBinding
    private lateinit var viewModel: MovieDetailsViewModel
    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        setSupportActionBar(detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieDetailsViewModel::class.java)

        if (savedInstanceState == null) {
            movie = intent.getParcelableExtra(MovieDetailFragment.MOVIE) as Movie
            val fragment = MovieDetailFragment.newInstance(movie)

            supportFragmentManager.beginTransaction()
                .add(R.id.movie_detail_container, fragment)
                .commit()
        }
        binding.model = movie
        subscribe()
    }

    private fun subscribe() {
        val d1 = RxView.clicks(fab).subscribe { _ ->
            val isSelected = !fab.isSelected
            fab.isSelected = isSelected
            if (isSelected) {
                viewModel.addFavorite(movie)
            } else {
                viewModel.removeFavorite(movie)
            }
        }

        val d2 = movieSubject.subscribe { movie ->
            val added = getString(R.string.movie_added)
            val removed = getString(R.string.movie_removed)
            val string = if (movie.isFavorite()) added else removed
            toast(string)
        }

        disposable.add(d1)
        disposable.add(d2)
    }

    fun onFavoriteClicked(view: View) {
        val isSelected = !fab.isSelected
            fab.isSelected = isSelected
            if (isSelected) {
                viewModel.addFavorite(movie)
            } else {
                viewModel.removeFavorite(movie)
            }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                navigateUpTo(Intent(this, MovieListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
