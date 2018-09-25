package com.sivakumarc.moviesearch.screen

import android.view.View
import com.agoda.kakao.*
import com.sivakumarc.moviesearch.R
import org.hamcrest.Matcher

class MovieListScreen : Screen<MovieListScreen>() {

    val searchQuery: KTextView = KTextView { withId(R.id.text_query) }

    val recycler: KRecyclerView = KRecyclerView({
        withId(R.id.recycler_view)
    }, itemTypeBuilder = {
        itemType(::Item)
    })

    class Item(parent: Matcher<View>) : KRecyclerItem<Item>(parent) {
        val imageView: KImageView = KImageView(parent) { withId(R.id.poster) }
    }
}