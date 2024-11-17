package com.example.miok_info_app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.miok_info_app.R
import com.example.miok_info_app.viewmodel.SharedViewModel

class AboutFragment : Fragment(R.layout.fragment_about) {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe currentLanguage LiveData for updates
        sharedViewModel.currentLanguage.observe(viewLifecycleOwner) { language ->
            updateStrings(language)  // Update the UI based on the current language
        }
    }

    private fun updateStrings(language: String) {
        // Update your UI based on the selected language
        // For example, set text on TextViews according to the language:
        // textView.text = getString(R.string.some_string_resource_name)
    }
}
