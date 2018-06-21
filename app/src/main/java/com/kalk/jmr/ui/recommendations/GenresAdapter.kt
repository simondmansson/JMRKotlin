package com.kalk.jmr.ui.recommendations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kalk.jmr.R.layout.genre_card_view

class GenresAdapter(private var genres: List<String>, private val listener: (String) -> Unit):
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
        fun bind(genre: String, listener: (String) -> Unit) = with(itemView) {
            genre_card_title.text = genre
            genre_card_title.setOnClickListener {
                view ->  listener.invoke(genre)
            }
        }
    }

    fun filterGenres(filtered:List<String>) {
        genres = filtered
        notifyDataSetChanged()
    }
}