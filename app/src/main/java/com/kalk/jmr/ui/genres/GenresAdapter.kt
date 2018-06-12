package com.kalk.jmr.ui.genres

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.kalk.jmr.R
import kotlinx.android.synthetic.main.genre_card_view.view.*
import kotlinx.android.synthetic.main.genres_fragment.view.*

class GenresAdapter(var genres: List<String>, val listener: (String) -> Unit):
        RecyclerView.Adapter<GenresAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val playlist = inflater.inflate(R.layout.genre_card_view, parent, false)
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