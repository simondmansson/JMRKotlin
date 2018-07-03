package com.kalk.jmr.ui.history

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kalk.jmr.R
import com.kalk.jmr.db.playlist.Playlist
import kotlinx.android.synthetic.main.playlist_card_view.view.*

/**
 * Maps playlists elements navigateTo card views in the UI
 * @param playlists List of @class Playlist
 * @param listener
 */
class PlaylistAdapter(private var playlists: List<Playlist>, val listener: (type:Int, Playlist) -> Unit) :
        RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val playlist = inflater.inflate(R.layout.playlist_card_view, parent, false)
        return ViewHolder(playlist)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(playlists[position], listener)

    override fun getItemCount() = playlists.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(playlist: Playlist, listener: (type:Int, Playlist) -> Unit) = with(itemView) {
            _playlist_card_title.text = playlist.title
            playlist_button_card.setOnClickListener {
                view ->
                listener.invoke(0,playlist)
            }

            tracklist_button_card.setOnClickListener {
                view ->
                listener.invoke(1, playlist)
            }

        }
    }

    fun removeAt(position: Int, listener: (Playlist) -> Unit) {
        val pl = playlists.get(index = position)
        listener.invoke(pl)
        playlists = playlists.filterIndexed { index, historyPlaylist -> index != position  }
        notifyItemRemoved(position)
    }
    fun updatePlayList(newlists: List<Playlist>) {
        playlists = newlists
        notifyDataSetChanged()
    }
}

