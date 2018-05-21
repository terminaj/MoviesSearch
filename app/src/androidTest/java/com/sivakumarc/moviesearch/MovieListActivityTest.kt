package com.sivakumarc.moviesearch

import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.runner.AndroidJUnit4
import com.sivakumarc.moviesearch.screen.MovieListScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieListActivityTest {
    @Rule
    @JvmField
    val activityRule = IntentsTestRule(MovieListActivity::class.java)
    
    private lateinit var activity: MovieListActivity
    private val screen = MovieListScreen()

    @Before
    fun init() {
        activity = activityRule.activity
    }

    @Test
    fun checkRecyclerView() {

        screen {
            idle (1000)

            searchQuery {
                isVisible()
                containsText(activity.searchQuery)
            }

            recycler {
                firstChild<MovieListScreen.Item> {
                    isVisible()
                    imageView {
                        isVisible()
                    }
                }
            }
        }

    }
}