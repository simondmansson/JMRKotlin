package com.kalk.jmr.ui.history

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kalk.jmr.PlayCommands
import com.kalk.jmr.R
import kotlinx.android.synthetic.main.history_fragment.*
import kotlinx.android.synthetic.main.main_activity.*


class HistoryFragment : Fragment() {
    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var playCommands: PlayCommands
    private lateinit var history:HistoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        activity?.toolbar_main_text?.text = resources.getString(R.string.toolbar_history)

        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        playCommands = context as PlayCommands
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        history = ViewModelProviders.of(this).get(HistoryViewModel::class.java)

        val playLists: List<Playlist> = history.playlists.value ?: listOf()
        val adapter =  PlaylistAdapter(playLists) {
            context?.toast("${it.title} Clicked", Toast.LENGTH_SHORT)
            val playlist = playLists.filter { plist -> plist.id == it.id }
            playCommands.play(playlist[0].songs)
        }

        history_recycler.layoutManager = LinearLayoutManager(context)
        history_recycler.adapter = adapter

        history.getPlaylists().observe(this, Observer(function = {
            adapter.updatePlayList(if (it != null) it else playLists)
        }))

    }
}
