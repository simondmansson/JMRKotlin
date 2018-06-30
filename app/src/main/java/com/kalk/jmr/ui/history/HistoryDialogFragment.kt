package com.kalk.jmr.ui.history

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kalk.jmr.PlayCommands
import com.kalk.jmr.R
import com.kalk.jmr.db.track.Track
import com.kalk.jmr.getPlaylistRepository
import kotlinx.android.synthetic.main.history_dialog_fragment.*
import org.jetbrains.anko.doAsync

class HistoryDialogFragment: DialogFragment() {


    private lateinit var playCommands: PlayCommands
    private lateinit var historyViewModel:HistoryViewModel

    companion object {
        val TAG = HistoryDialogFragment::class.java.simpleName
        fun newInstance(list: ArrayList<Track>) = HistoryDialogFragment().apply {
            val args = Bundle().apply {
                putParcelableArrayList("list", list)
            }
            this.arguments = args
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        playCommands = context as PlayCommands
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        historyViewModel = ViewModelProviders.of(activity!!, HistoryViewModelFactory(
                getPlaylistRepository(activity!!.applicationContext)))
                .get(HistoryViewModel::class.java)

        return inflater.inflate(R.layout.history_dialog_fragment, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val tracks = arguments?.get("list")
        val adapter = HistoryDialogFragmentAdapter(tracks as ArrayList<Track>) { type, track ->
            when(type) {
                0 -> {
                    doAsync {
                        //historyViewModel remove track from pl, we also need to bundle playlist title and id
                    }
                }
                1 -> {
                    doAsync {
                        playCommands.play(listOf(track.uri))
                    }
                }
            }
        }

        history_dialog_recycler.layoutManager = LinearLayoutManager(parentFragment?.context)
        history_dialog_recycler.adapter = adapter
    }
}