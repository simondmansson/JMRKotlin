package com.kalk.jmr.ui.recommendations

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kalk.jmr.R
import com.kalk.jmr.db.AppDatabase
import com.kalk.jmr.db.genre.Genre
import com.kalk.jmr.ioThread
import kotlinx.android.synthetic.main.genres_fragment.*
import kotlinx.android.synthetic.main.main_activity.*

class GenresFragment : Fragment() {
    lateinit var genres:List<Genre>
    lateinit var adapter: GenresAdapter

    companion object {
        fun newInstance() = GenresFragment()
    }

    private lateinit var recommendations:RecommendationsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        toolbar_main_text?.text = resources.getString(R.string.toolbar_genres)

        return inflater.inflate(R.layout.genres_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recommendations = ViewModelProviders.of(this).get(RecommendationsViewModel::class.java)
        if(recommendations.genres.value == null) {
            ioThread {
                recommendations.genres.postValue(AppDatabase.getInstance(activity!!.applicationContext).genreDao().getAllGenres())
            }
        }

        genres = recommendations.genres.value ?: listOf()

        adapter = GenresAdapter(genres) {
            recommendations.setGenre(it)
        }

        genres_recycler.layoutManager = LinearLayoutManager(view?.context)
        genres_recycler.adapter = adapter

        genre_current.text = resources.getString(R.string.chosen_genre, recommendations.getGenre().value)
        recommendations.getGenre().observe(this, Observer {
            genre_current.text = resources.getString(R.string.chosen_genre, it)
        })

    }
}
