package com.kalk.jmr.ui.recommendations

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.widget.toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.kalk.jmr.R
import kotlinx.android.synthetic.main.genres_fragment.*
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
        activity?.toolbar_main_text?.text = resources.getString(R.string.toolbar_genres)

        //Read genres from assets
        val json = context?.assets?.open(
                "spotify-genres.json")
                ?.bufferedReader()?.use {
                    it.readText()
        }

        val list = Gson().fromJson<GenresList>(json , GenresList::class.java)
        genres = list.genres.map { genre -> genre.capitalize() }

        return inflater.inflate(R.layout.genres_fragment, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recommendations = ViewModelProviders.of(activity!!).get(RecommendationsViewModel::class.java)

        adapter = GenresAdapter(genres) {
            context?.toast("Genre set to ${it}", Toast.LENGTH_SHORT)
            recommendations.setGenre(it)
        }

        genres_recycler.layoutManager = LinearLayoutManager(view?.context)
        genres_recycler.adapter = adapter

    }

    private fun filter(text: String) {
        val filtered = genres.filter { it.toLowerCase().contains(text.toLowerCase()) }
        adapter.filterGenres(filtered)
    }
    
}
