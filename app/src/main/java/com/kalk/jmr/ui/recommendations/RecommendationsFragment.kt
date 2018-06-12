package com.kalk.jmr.ui.recommendations

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.toast
import com.kalk.jmr.PlayCommands
import com.kalk.jmr.R
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.recommendations_fragment.view.*

class RecommendationsFragment() : Fragment() {
    companion object {
        fun newInstance() = RecommendationsFragment()
    }

    private lateinit var playCommands: PlayCommands

    private lateinit var viewModel: RecommendationsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        activity?.toolbar_main_text?.text = "Recommendations"

        val view = inflater.inflate(R.layout.recommendations_fragment, container, false)

        view.button_recommend?.setOnClickListener() {
            view -> view.context.toast("Recommendation with...", Toast.LENGTH_SHORT)
            val list = listOf<String>("1iGXvUsVVkYBas0Cniw6NB", "3IDsegNBHC4pjGCOMTQYlU", "2NJ3P3vXdOBk9cjPIFS3uH")
            playCommands?.play(list)

        }
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        playCommands = context as PlayCommands
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RecommendationsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
