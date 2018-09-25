package com.sivakumarc.moviesearch

import android.annotation.SuppressLint
import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.sivakumarc.moviesearch.model.Movie
import com.sivakumarc.moviesearch.view.BaseActivity
import com.sivakumarc.moviesearch.view.GenericAdapter
import com.sivakumarc.moviesearch.view.ScrollListener
import com.sivakumarc.moviesearch.view.ViewConstants
import com.sivakumarc.moviesearch.viewmodel.MovieViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_movie_list.*
import kotlinx.android.synthetic.main.movie_list.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MovieListActivity : BaseActivity(){

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MovieViewModel
    private lateinit var scrollListener: ScrollListener
    private var adapter: GenericAdapter?= null

    private val paginator = PublishProcessor.create<Int>()
    private var pageNumber = 1

    private var twoPane: Boolean = false
    var searchQuery = "a" //initial search

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        if (movie_detail_container != null) {
            twoPane = true
        }
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieViewModel::class.java)

        viewModel.listMoviesLiveData.observe(this,
                Observer<List<Movie>> { data ->
                    data?.let {
                        setData(it)
                    }
                })

        viewModel.favMoviesLiveData.observe(this,
                Observer<List<Movie>> { result ->
                    result?.let {
                        setData(it)
                    }
                }
        )

        recycler_view.post {
            setupRecyclerView()
            disposable.add(getMoreData(paginator))
            subscribe()
        }

        val layoutManager = recycler_view.layoutManager as GridLayoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter?.getItemViewType(position) == ViewConstants.LOADING) ViewConstants.LOADING else ViewConstants.MOVIES
            }
        }

        scrollListener = ScrollListener(layoutManager) {
            paginator.onNext(pageNumber++)
        }

        searchQuery = "a"
    }

    private fun setupRecyclerView() {
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = null
        adapter = null
        adapter = GenericAdapter {
            //inline fn for click event inside view holder
            if (it != null) {
                addNextView(it as Movie)
            }
        }
        recycler_view.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.movies_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favorites -> {
                toolbar.title = getString(R.string.favorites)
                viewModel.loadFavoriteMovies()
            }
            R.id.action_search -> {
                toolbar.title = getString(R.string.app_name)
                subscribeSearch.invoke(paginator)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun subscribe() {
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                scrollListener.loadMore()
            }
        })
    }

    private val subscribeSearch = { processor: PublishProcessor<Int> ->
        //backpressure drops subsequent event untill current is completed
        processor.onBackpressureDrop().subscribe { page ->
            viewModel.getMovies(searchQuery, page)
        }
    }


    private fun getMoreData(processor: PublishProcessor<Int>) : Disposable {
        return  subscribeSearch.invoke(processor)
    }

    private fun setData(searchResult: List<Movie>) {

        if(toolbar.title == getString(R.string.favorites)){
            text_query.visibility = View.VISIBLE
            adapter?.setItems(searchResult)
            return
        }

        text_query.visibility = View.VISIBLE
        text_query.text = String.format(getString(R.string.showing_results_for), searchQuery)
        no_movies.visibility = if (searchResult.isEmpty()) View.VISIBLE else View.GONE

        adapter?.addItems(searchResult)
    }

    private fun addNextView(movie: Movie) {
        val fragment = MovieDetailFragment.newInstance(movie)
        if(twoPane){
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit()
        }else {
            val intent = Intent(this, MovieDetailActivity::class.java).apply {
                putExtra(MovieDetailFragment.args_Movie, movie)
            }
            startActivity(intent)
        }
    }
}
