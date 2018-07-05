package com.kalk.jmr.ui.history

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kalk.jmr.R
import com.kalk.jmr.SpotifyCommands
import com.kalk.jmr.getPlaylistRepository
import com.kalk.jmr.ui.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.history_fragment.*
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.doAsync

class HistoryFragment : Fragment() {
    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var playCommands: SpotifyCommands
    private lateinit var historyViewModel:HistoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        toolbar_main_text?.text = resources.getString(R.string.toolbar_history)

        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        playCommands = context as SpotifyCommands
        playCommands.requestAuthToken()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        historyViewModel = ViewModelProviders.of(activity!!, HistoryViewModelFactory(
                getPlaylistRepository(activity!!.applicationContext)))
                .get(HistoryViewModel::class.java)

        val adapter = PlaylistAdapter(historyViewModel.playlists.value ?: listOf()) { type, playlist ->
            when(type) {
                0 -> {
                    doAsync {
                       val tracks = historyViewModel.getTracksFromPlaylistId(playlist.id)
                        val uris = tracks.map { it.uri }
                        playCommands.play(uris)
                    }
                }

                1-> {
                    doAsync {
                       val playlistTracks =  historyViewModel.getTracksFromPlaylistId(playlist.id)
                        val arrayList = ArrayList(playlistTracks)
                        val fm = fragmentManager
                        val hdf= HistoryDialogFragment
                                .newInstance(playlist.title, playlist.id, arrayList)
                        hdf.show(fm, "history_dialog_fragment")
                    }
                }
            }
        }

        history_recycler.layoutManager = LinearLayoutManager(context)
        history_recycler.adapter = adapter

        val swipeHandler = object: SwipeToDeleteCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = history_recycler.adapter as PlaylistAdapter
                adapter.removeAt(viewHolder.adapterPosition ) {
                    historyViewModel.removePlaylist(it.id)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(history_recycler)

        historyViewModel.playlists.observe(viewLifecycleOwner, Observer {
            adapter.updatePlayList(if (it != null) it else historyViewModel.playlists.value ?: listOf())
        })

    }
}
