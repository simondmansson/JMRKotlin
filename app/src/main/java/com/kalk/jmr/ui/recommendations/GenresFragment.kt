package com.kalk.jmr.ui.recommendations

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kalk.jmr.R
import kotlinx.android.synthetic.main.main_activity.*

class GenresFragment : Fragment() {
    lateinit var genres:List<String>
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
    }
        /*
         recommendations = ViewModelProviders.of(this).get(RecommendationsViewModel::class.java)

        adapter = GenresAdapter(genres) {
            recommendations.setGenre(it)
        }


        genres_recycler.layoutManager = LinearLayoutManager(view?.context)
        genres_recycler.adapter = adapter

        recommendations.getGenre().observe(this, Observer {
            genre_current.text = resources.getString(R.string.chosen_genre, it)
        })

    private fun filter(text: String) {
        val filtered = genres.filter { it.toLowerCase().contains(text.toLowerCase()) }
        adapter.filterGenres(filtered)
    }

    }
         */


}
