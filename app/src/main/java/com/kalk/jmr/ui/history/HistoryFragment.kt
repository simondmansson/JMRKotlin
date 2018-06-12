package com.kalk.jmr.ui.history

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kalk.jmr.PlayCommands
import com.kalk.jmr.R
import kotlinx.android.synthetic.main.history_fragment.*
import kotlinx.android.synthetic.main.history_fragment.view.*
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_activity.view.*


class HistoryFragment : Fragment() {
    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var playCommands: PlayCommands

    //private lateinit var viewModel:HistoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.history_fragment, container, false)

        activity?.toolbar_main_text?.text = "History"

        val list = arrayListOf<Playlist>(
                Playlist(1,"Malmö-morning-run", arrayListOf("1iGXvUsVVkYBas0Cniw6NB", "3IDsegNBHC4pjGCOMTQYlU", "2NJ3P3vXdOBk9cjPIFS3uH")),
                Playlist(2, "Malmö-afternoon-walk", arrayListOf("50LgxH3t8vWy5xMFljdUbF", "43G2Go5tmms3DjC2qdKCD0")),
                Playlist(3, "Malmö-morning-still", arrayListOf("4rXEQ0gVrddOY4LWUknZt3"))
        )

        view.apply {
            history_recycler.layoutManager = LinearLayoutManager(view.context)
            history_recycler.adapter = PlaylistAdapter(list) {
                context.toast("${it.title} Clicked", Toast.LENGTH_SHORT)
                val playlist = list.filter { plist ->  plist.id == it.id }
                playCommands?.play(playlist[0].songs)
            }
        }

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        playCommands = context as PlayCommands
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)
        // TODO: Use the ViewModel
    }
}
