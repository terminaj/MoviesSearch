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
import com.sivakumarc.moviesearch.view.ViewConstants
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
    var searchQuery = "a" //initial search

    val observeFav = { viewModel: MoviesListViewModel ->
        viewModel.favMoviesLiveData.observe(this,
            Observer<List<Movie>> { favResult ->
                favResult?.let {
                    text_query.visibility = View.GONE
                    no_movies.visibility = if (favResult.isEmpty()) View.VISIBLE else View.GONE
                    adapter?.setItems(favResult)
                }
            }
        )
    }

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
        observeFav.invoke(viewModel)
        recycler_view.post {
            setupRecyclerView()
            disposable.add(searchData.subscribe(paginator))
            subscribe()
        }

        layoutManager = recycler_view.layoutManager as GridLayoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter?.getItemViewType(position) == ViewConstants.LOADING) 2 else 1
            }
        }
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

        searchViewMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                toolbar.title = title
                menu.findItem(R.id.action_favorites).isVisible = false
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                invalidateOptionsMenu()
                menu.findItem(R.id.action_favorites).isVisible = true
                return true
            }
        })

        setupSearchView()

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when {
            item.itemId == R.id.action_favorites -> {
                toolbar.title = getString(R.string.favorites)
                viewModel.loadFavoriteMovies()
            }
        }
        return super.onOptionsItemSelected(item)
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

    private fun setData(searchResult: SearchData) {
        if(toolbar.title == getString(R.string.favorites))
            return

        text_query.visibility = View.VISIBLE
        text_query.text = String.format(getString(R.string.showing_results_for), searchResult.query)
        no_movies.visibility = if (searchResult.movies.isEmpty()) View.VISIBLE else View.GONE
        if (searchQuery == searchResult.query) {
            adapter?.addItems(searchResult.movies)
        } else {
            setupRecyclerView()
            adapter?.setItems(searchResult.movies)
        }

        this.searchQuery = searchResult.query
    }

    class LiveItemData(private val observer: (MoviesListViewModel) -> Unit, private val subscriber: (PublishProcessor<Int>) -> Disposable){
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
