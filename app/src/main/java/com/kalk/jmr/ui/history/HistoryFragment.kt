package com.kalk.jmr.ui.history

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.toast
import com.kalk.jmr.PlayCommands
import com.kalk.jmr.R
import com.kalk.jmr.getPlaylistRepository
import kotlinx.android.synthetic.main.history_fragment.*
import kotlinx.android.synthetic.main.main_activity.*


class HistoryFragment : Fragment() {
    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var playCommands: PlayCommands
    private lateinit var historyViewModel:HistoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        toolbar_main_text?.text = resources.getString(R.string.toolbar_history)

        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        playCommands = context as PlayCommands
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        historyViewModel = ViewModelProviders.of(this, HistoryViewModelFactory(
                getPlaylistRepository(activity!!.applicationContext)))
                .get(HistoryViewModel::class.java)

        val adapter =  PlaylistAdapter(historyViewModel.playlists.value ?: listOf()) {
            context?.toast("${it.title} Clicked", Toast.LENGTH_SHORT)
            val songs = it.uri.map { it.uri }
            playCommands.play(songs)
        }

        history_recycler.layoutManager = LinearLayoutManager(context)
        history_recycler.adapter = adapter

        historyViewModel.playlists.observe(this, Observer(function = {
            adapter.updatePlayList(if (it != null) it else historyViewModel.playlists.value ?: listOf())
        }))

    }
}
