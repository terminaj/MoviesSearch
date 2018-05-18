package com.sivakumarc.moviesearch.view

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sivakumarc.moviesearch.databinding.ViewLoaderBinding
import com.sivakumarc.moviesearch.databinding.ViewMovieBinding
import com.sivakumarc.moviesearch.view.ViewConstants.LOADING
import com.sivakumarc.moviesearch.view.ViewConstants.MOVIES

class GenericAdapter(private val onClick : (Any?) -> Unit) : RecyclerView.Adapter<GenericViewHolder>() {

    private var items: ArrayList<ViewType> = ArrayList()
//    private var viewTypeAdapters = SparseArrayCompat<ViewTypeAdapter>()
    private val loadingItem = object : ViewType {
        override fun getViewType() = ViewConstants.LOADING
    }

    init {
        items.add(loadingItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        var binding: ViewDataBinding? = null
        when (viewType) {
            MOVIES -> binding =
                    ViewMovieBinding.inflate(inflater, parent, false)
            LOADING -> binding =
                    ViewLoaderBinding.inflate(inflater, parent, false)
        }
        return GenericViewHolder(binding!!)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.setData(items[position], onClick)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {

        //idea here is to use different layout types as guided by server
        //server response object contains which layout to show
        //by this way we can change the design dynamically
        //modular code and very useful for AB testing
        //mocked layout id

        return this.items.get(position).getViewType()
    }

    fun addItems(newItems: List<ViewType>) {
        val initPosition = items.size - 1
        if(initPosition > -1) {
            items.removeAt(initPosition)
            notifyItemRemoved(initPosition)
        }
        items.addAll(newItems)
        items.add(loadingItem)
        notifyItemRangeChanged(initPosition, items.size + 1)
    }

    fun setItems(newItems: List<ViewType>) {
        cleanItems()
        items.addAll(newItems)
    }

    fun cleanItems() {
        items.clear()
        notifyDataSetChanged()
    }
}
