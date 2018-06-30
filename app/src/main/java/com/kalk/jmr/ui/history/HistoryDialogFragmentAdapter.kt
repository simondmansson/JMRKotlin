package com.kalk.jmr.ui.history

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kalk.jmr.R
import com.kalk.jmr.db.track.Track
import kotlinx.android.synthetic.main.track_list.view.*
import org.jetbrains.anko.toast

class HistoryDialogFragmentAdapter(private val tracks: ArrayList<Track>, val listener: (type:Int, Track) -> Unit):
        RecyclerView.Adapter<HistoryDialogFragmentAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val tracklist = inflater.inflate(R.layout.track_list, parent, false)
        return VH(tracklist)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(tracks[position], listener)

    override fun getItemCount() = tracks.size

    class VH(itemView: View): RecyclerView.ViewHolder(itemView)  {
        fun bind(track:Track, listener: (type:Int, Track) -> Unit)  = with(itemView) {
            track_list_text.text = track.name
            tracklist_delete_button.setOnClickListener {
                context?.toast("Deleting track ${track.name}")
                listener.invoke(0, track)
            }
            tracklist_play_button.setOnClickListener {
                listener.invoke(1, track)
                context?.toast("Playing track ${track.name}")
            }
        }
    }
}