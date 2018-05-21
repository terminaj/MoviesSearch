package com.sivakumarc.moviesearch.view

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import com.sivakumarc.moviesearch.BR

class GenericViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root){
    fun setData(model: Any?, onClick : (Any?) -> Unit) {
        if (binding != null) {
            binding!!.setVariable(BR.model, model)
            binding!!.executePendingBindings()
        }
        itemView.setOnClickListener {
            onClick(model)
        }
    }
}