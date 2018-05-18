package com.sivakumarc.moviesearch

import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import com.sivakumarc.moviesearch.model.Movie
import com.sivakumarc.moviesearch.view.BaseActivity
import com.sivakumarc.moviesearch.view.GenericAdapter
import com.sivakumarc.moviesearch.view.ScrollListener
import com.sivakumarc.moviesearch.viewmodel.MoviesListViewModel
import com.sivakumarc.moviesearch.viewmodel.SearchData
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

    private val SEARCH_QUERY_DELAY_MILLIS = 400L

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MoviesListViewModel
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var scrollListener: ScrollListener
    private var adapter: GenericAdapter?= null
    private lateinit var searchData: LiveItemData
    private lateinit var searchView: SearchView
    private lateinit var searchViewMenuItem: MenuItem

    private val paginator = PublishProcessor.create<Int>()

    private var pageNumber = 1
    private var twoPane: Boolean = false
    private var searchQuery = "a" //initial search

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        if (movie_detail_container != null) {
            twoPane = true
        }
        viewModel =
                ViewModelProviders.of(this, viewModelFactory).get(MoviesListViewModel::class.java)

        setSearchData()
        searchData.observe(viewModel)

        recycler_view.post {
            setupRecyclerView()
            disposable.add(searchData.subscribe(paginator))
            subscribe()
        }
        progress_bar.visibility = View.VISIBLE

        layoutManager = recycler_view.layoutManager as GridLayoutManager
        scrollListener = ScrollListener(layoutManager) {
            paginator.onNext(pageNumber++)
        }
        recycler_view.itemAnimator = DefaultItemAnimator()
    }

    private fun setupRecyclerView() {
        recycler_view.adapter = null
        adapter = null

        adapter = GenericAdapter {
            if(it != null) {
                addNextView(it as Movie)
            }
        }
        recycler_view.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.movies_menu, menu)

        searchViewMenuItem = menu.findItem(R.id.action_search)

        setupSearchView()

        return super.onCreateOptionsMenu(menu)
    }

    private fun setupSearchView() {
        searchView = searchViewMenuItem.actionView as SearchView

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        RxSearchView.queryTextChanges(searchView)
            .filter { t: CharSequence -> t.isNotEmpty() }
            .debounce(SEARCH_QUERY_DELAY_MILLIS, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(Schedulers.io())
            .doOnError {
                Timber.e(it.message)
            }.subscribe{query ->
                Timber.d("search", query)
                viewModel.searchMovies(query.toString(), 1)
            }
    }

    private fun subscribe() {
        val d1 = RxRecyclerView
            .scrollEvents(recycler_view)
            .subscribe({
                scrollListener.loadMore()
            })
        disposable.add(d1)
    }

    private fun setSearchData() {
        val observeSearch = { viewModel: MoviesListViewModel ->
            viewModel.searchMoviesLiveData.observe(this,
                Observer<SearchData> { searchData ->
                    searchData?.let {
                        setData(searchData)
                    }
                })
        }

        val subscribeSearch = { processor: PublishProcessor<Int> ->
            processor.onBackpressureDrop().subscribe { page ->
                viewModel.searchMovies(searchQuery, page)
            }
        }

        searchData = LiveItemData(observeSearch, subscribeSearch)
    }

    private fun setData(searchData: SearchData) {
        progress_bar.visibility = View.GONE
        text_query.text = String.format(getString(R.string.showing_results_for), searchData.query)
        no_movies.visibility = if (searchData.movies.isEmpty()) View.VISIBLE else View.GONE
        if (searchQuery == searchData.query) {
            adapter?.addItems(searchData.movies)
        } else {
            setupRecyclerView()
            adapter?.setItems(searchData.movies)
        }

        this.searchQuery = searchData.query
    }

    class LiveItemData(val observer: (MoviesListViewModel) -> Unit, private val subscriber: (PublishProcessor<Int>) -> Disposable){
        fun observe(viewModel: MoviesListViewModel) {
            observer.invoke(viewModel)
        }

        fun subscribe(processor: PublishProcessor<Int>) = subscriber.invoke(processor)

    }

    private fun addNextView(movie: Movie) {
        val fragment = MovieDetailFragment.newInstance(movie)
        if(twoPane){
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.movie_detail_container, fragment)
                .commit()
        }else {
            val intent = Intent(this, MovieDetailActivity::class.java).apply {
                putExtra(MovieDetailFragment.MOVIE, movie)
            }
            startActivity(intent)
        }
    }
}
