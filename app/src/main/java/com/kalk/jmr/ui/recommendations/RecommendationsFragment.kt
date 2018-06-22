package com.kalk.jmr.ui.recommendations

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kalk.jmr.PlayCommands
import com.kalk.jmr.R
import com.kalk.jmr.db.genre.GenreDao
import com.kalk.jmr.db.genre.GenreRepository
import com.kalk.jmr.ui.settings.SettingsViewModel
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.recommendations_fragment.*
import java.util.*


class RecommendationsFragment() : Fragment() {
    companion object {
        fun newInstance() = RecommendationsFragment()
        val TAG = RecommendationsFragment::class.java.simpleName
    }

    private lateinit var playCommands: PlayCommands
    private lateinit var recommendations: RecommendationsViewModel
    private lateinit var settings: SettingsViewModel
    private lateinit var songList:List<String>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

       toolbar_main_text?.text = resources.getString(R.string.toolbar_recommendations)

        return inflater.inflate(R.layout.recommendations_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        playCommands = context as PlayCommands
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /**
         * SETTINGS
         */
        settings = ViewModelProviders.of(activity!!).get(SettingsViewModel::class.java)

        settings.activity.observe(this, Observer {
            recommendations_activity.visibility = if(it!!) View.VISIBLE else View.GONE })
        settings.location.observe(this, Observer {
            recommendations_location.visibility = if(it!!) View.VISIBLE else View.GONE })
        settings.time.observe(this, Observer {
            recommendations_time.visibility = if(it!!) View.VISIBLE else View.GONE })
        /**
         * RECOMMENDATIONS
         */
        recommendations = ViewModelProviders.of(activity!!).get(RecommendationsViewModel::class.java)

        recommendations.getGenre().observe(this, Observer {
            recommendations_chosen_genre.text = resources.getString(R.string.chosen_genre, it) })

        recommendations.getCurrentActivity().observe(this, Observer {
            recommendations_activity.text = resources.getString(R.string.current_activity, it)
        })

        recommendations.getCurrentLocation().observe(this, Observer {
            recommendations_location.text = resources.getString(R.string.current_location, it)
        })

        recommendations_time.text = resources.getString(R.string.current_time, Date().toString())

        recommendations.getSongs().observe(this, Observer {
            songList = it.orEmpty()
        })

        button_recommend?.setOnClickListener() {


        }

    }
}
