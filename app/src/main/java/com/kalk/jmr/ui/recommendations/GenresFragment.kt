package com.kalk.jmr.ui.genres

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.widget.toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.kalk.jmr.R
import kotlinx.android.synthetic.main.genre_card_view.view.*
import kotlinx.android.synthetic.main.genres_fragment.*
import kotlinx.android.synthetic.main.genres_fragment.view.*
import kotlinx.android.synthetic.main.main_activity.*


class GenresFragment : Fragment() {
    lateinit var genres:List<String>
    lateinit var adapter:GenresAdapter

    companion object {
        fun newInstance() = GenresFragment()
    }

    //private lateinit var viewModel:HistoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        activity?.toolbar_main_text?.text = "Genres"
        val view = inflater.inflate(R.layout.genres_fragment, container, false)

        //Read genres from assets
        val json = context?.assets?.open(
                "spotify-genres.json")
                ?.bufferedReader()?.use {
                    it.readText()
        }

        val list = Gson().fromJson<GenresList>(json , GenresList::class.java)
        genres = list.genres.map { genre -> genre.capitalize() }

        adapter = GenresAdapter(genres) { view.context.toast("${it} Clicked", Toast.LENGTH_SHORT) }

        view.apply {
            genres_recycler.layoutManager = LinearLayoutManager(view.context)
            genres_recycler.adapter = adapter
        }

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun filter(text: String) {
        val filtered = genres.filter { it.toLowerCase().contains(text.toLowerCase()) }
        adapter?.filterGenres(filtered)
    }
    
}
