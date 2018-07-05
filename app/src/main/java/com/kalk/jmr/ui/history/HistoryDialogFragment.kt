package com.kalk.jmr.ui.history

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kalk.jmr.R
import com.kalk.jmr.SpotifyCommands
import com.kalk.jmr.db.track.Track
import com.kalk.jmr.getPlaylistRepository
import kotlinx.android.synthetic.main.history_dialog_fragment.*
import org.jetbrains.anko.doAsync

class HistoryDialogFragment: DialogFragment() {


    private lateinit var playCommands: SpotifyCommands
    private lateinit var historyViewModel:HistoryViewModel

    companion object {
        val TAG = HistoryDialogFragment::class.java.simpleName
        fun newInstance(playlistTitle:String, playlistId:String, list: ArrayList<Track>) = HistoryDialogFragment().apply {
            val args = Bundle().apply {
                putString("playlistTitle", playlistTitle)
                putString("playlistId", playlistId)
                putParcelableArrayList("list", list)
            }
            this.arguments = args
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        playCommands = context as SpotifyCommands
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        historyViewModel = ViewModelProviders.of(activity!!, HistoryViewModelFactory(
                getPlaylistRepository(activity!!.applicationContext)))
                .get(HistoryViewModel::class.java)

        return inflater.inflate(R.layout.history_dialog_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val tracks = arguments?.getParcelableArrayList<Track>("list") as ArrayList<Track>
        val playlistId = arguments!!.getString("playlistId")
        val adapter = HistoryDialogFragmentAdapter(tracks) { type, track ->
            when(type) {
                0 -> {
                    doAsync {
                        historyViewModel.removeTrackFromPlaylist(playlistId, track.uri)
                    }
                }
                1 -> {
                    doAsync {
                        playCommands.play(listOf(track.uri))
                    }
                }
            }
        }

        history_dialog_title.text =  arguments?.getString("playlistTitle") ?: "Tracks"
        history_dialog_recycler.layoutManager = LinearLayoutManager(parentFragment?.context)
        history_dialog_recycler.adapter = adapter
    }
}