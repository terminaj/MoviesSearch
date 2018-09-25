package com.sivakumarc.moviesearch

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

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_movie_detail, container, false)
        binding.model = arguments?.get(args_Movie) as Movie
        return binding.root
    }

    companion object {
        val args_Movie = "Movie"
        fun newInstance(entity: Movie): MovieDetailFragment {
            val args = Bundle()
            val fragment = MovieDetailFragment()
            args.putParcelable(args_Movie, entity)
            fragment.arguments = args
            return fragment
        }
    }
}
