package com.sivakumarc.moviesearch

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.sivakumarc.moviesearch.databinding.ActivityMovieDetailBinding
import com.sivakumarc.moviesearch.model.Movie
import com.sivakumarc.moviesearch.view.BaseActivity
import com.sivakumarc.moviesearch.viewmodel.MovieViewModel
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_movie_detail.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import javax.inject.Inject

class MovieDetailActivity : BaseActivity() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    internal lateinit var anySubject: PublishSubject<Any>

    private lateinit var binding: ActivityMovieDetailBinding
    private lateinit var viewModel: MovieViewModel
    private lateinit var entity: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail)

        //toolbar
        setSupportActionBar(detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieViewModel::class.java)

        if (savedInstanceState == null) {
            entity = intent.getParcelableExtra(MovieDetailFragment.args_Movie) as Movie
            val fragment = MovieDetailFragment.newInstance(entity)

            supportFragmentManager.beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit()
        }
        binding.model = entity
        subscribe()
    }

    private fun subscribe() {
        fab.onClick {
            val isSelected = !fab.isSelected
            fab.isSelected = isSelected
            if (isSelected) {
                viewModel.addFavorite(entity)
            } else {
                viewModel.removeFavorite(entity)
            }
        }

        val d1 = anySubject.subscribe { any ->
            val added = "Movie added to the favorites"
            val removed = "Movie removed from favorites"
            val string = if ((any as Movie).isFavorite()) added else removed
            toast(string)
        }

        disposable.add(d1)
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
