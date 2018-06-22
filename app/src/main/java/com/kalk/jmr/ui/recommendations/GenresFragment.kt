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
import com.kalk.jmr.db.genre.Genre
import com.kalk.jmr.getGenreRepository
import kotlinx.android.synthetic.main.genres_fragment.*
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.toast

class GenresFragment : Fragment() {
    private lateinit var genres:List<Genre>
    private lateinit var adapter: GenresAdapter
    private lateinit var genreViewModel: GenresViewModel

    companion object { fun newInstance() = GenresFragment() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        toolbar_main_text?.text = resources.getString(R.string.toolbar_genres)
        return inflater.inflate(R.layout.genres_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val factory = GenresViewModelFactory(getGenreRepository(activity!!.applicationContext))
        genreViewModel = ViewModelProviders.of(activity!!, factory).get(GenresViewModel::class.java)

        adapter = GenresAdapter(listOf()) { genreViewModel.setGenre(it.toInt()) }

        genreViewModel.genres.observe( this, Observer { adapter.updateGenres(it ?: listOf()) })
        genreViewModel.genreText.observe( this, Observer {
            genre_current.text = resources.getString(R.string.chosen_genre, it)
            context?.toast("$it chosen")
        })

        genres_recycler.layoutManager = LinearLayoutManager(view?.context)
        genres_recycler.adapter = adapter
        genre_current.text = resources.getString(R.string.chosen_genre, genreViewModel.genreText.value ?: "none")

    }
}
