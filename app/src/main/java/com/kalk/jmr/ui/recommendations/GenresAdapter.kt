package com.kalk.jmr.ui.recommendations

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kalk.jmr.R.layout.genre_card_view
import com.kalk.jmr.db.genre.Genre
import kotlinx.android.synthetic.main.genre_card_view.view.*

class GenresAdapter(var genres: List<Genre>, private val listener: (String) -> Unit):
        RecyclerView.Adapter<GenresAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val playlist = inflater.inflate(genre_card_view, parent, false)
        return ViewHolder(playlist)
    }

    override fun getItemCount(): Int = genres.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(genres[position], listener)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(genre: Genre, listener: (String) -> Unit) = with(itemView) {
            genre_card_title.text = genre.genre
            genre_card_title.setOnClickListener {
                view ->  listener.invoke(genre.id.toString())
            }
        }
    }
}