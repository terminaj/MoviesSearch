package com.sivakumarc.moviesearch

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sivakumarc.moviesearch.databinding.LayoutMovieDetailBinding
import com.sivakumarc.moviesearch.model.Movie
import com.sivakumarc.moviesearch.view.BaseFragment
import com.sivakumarc.moviessearch.di.Injectable

class MovieDetailFragment : BaseFragment(), Injectable {

    private lateinit var binding: LayoutMovieDetailBinding

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_movie_detail, container,false)
        binding.model = arguments?.get(MOVIE) as Movie
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        val MOVIE = "MOVIE"
        fun newInstance(movie: Movie): MovieDetailFragment {
            val args = Bundle()
            val fragment = MovieDetailFragment()
            args.putParcelable(MOVIE, movie)
            fragment.arguments = args
            return fragment
        }
    }
}
