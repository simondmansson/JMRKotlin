package com.kalk.jmr.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.kalk.jmr.R
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.settings_fragment.*


class SettingsFragment : Fragment() {
    companion object {
        fun newInstance() = SettingsFragment()
        private val TAG = SettingsFragment::class.java.simpleName
    }

    private lateinit var settings:SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        activity?.toolbar_main_text?.text = resources.getString(R.string.toolbar_settings)

        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        settings = ViewModelProviders.of(activity!!).get(SettingsViewModel::class.java)
        with(settings) {
            switch_activity.isChecked = activity.value ?: true
            switch_location.isChecked = location.value ?: true
            switch_time.isChecked = time.value ?:true
            switch_activity.setOnCheckedChangeListener { _, isChecked -> activity.value = isChecked }
            switch_location.setOnCheckedChangeListener { _, isChecked -> location.value = isChecked }
            switch_time.setOnCheckedChangeListener { _, isChecked -> time.value = isChecked }
        }


    }
}
