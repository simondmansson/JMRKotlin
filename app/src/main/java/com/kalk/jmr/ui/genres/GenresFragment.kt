package com.kalk.jmr.ui.genres

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kalk.jmr.R
import com.kalk.jmr.getGenreRepository
import kotlinx.android.synthetic.main.genres_fragment.*
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.toast

class GenresFragment : Fragment() {
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

        adapter = GenresAdapter(listOf()) {
            genreViewModel.chosenGenre.value = it.toInt()
            context?.toast("${genreViewModel.genreText.value} chosen")
        }

        genreViewModel.genres.observe(viewLifecycleOwner, Observer { adapter.updateGenres(it ?: listOf()) })

        genre_current.text = resources.getString(R.string.chosen_genre, "pick one")
        genreViewModel.genreText.observe(viewLifecycleOwner, Observer {
            if(it != null)
                genre_current.text = resources.getString(R.string.chosen_genre, it)
        })

        genres_recycler.layoutManager = LinearLayoutManager(view?.context)
        genres_recycler.adapter = adapter
    }
}
