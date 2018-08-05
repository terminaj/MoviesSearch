package com.sivakumarc.moviesearch.view

import android.databinding.BindingAdapter
import android.support.design.widget.FloatingActionButton
import android.widget.ImageView
import com.bumptech.glide.Glide

@BindingAdapter("imageUrl")
fun setImageUrl(view: ImageView, url: String) {
    Glide.with(view.context).load(url).into(view)
}

@BindingAdapter("selected")
fun setSelection(view: FloatingActionButton, isSelected: Int) {
    view.isSelected = isSelected == 1
}
