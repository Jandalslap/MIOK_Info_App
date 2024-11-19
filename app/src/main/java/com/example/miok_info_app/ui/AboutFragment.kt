package com.example.miok_info_app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.miok_info_app.R
import com.example.miok_info_app.databinding.FragmentAboutBinding
import com.example.miok_info_app.viewmodel.SharedViewModel

// Fragment that displays an about page
class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null // Backing property for safe nullability
    private val binding get() = _binding!! // Non-nullable reference to binding

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using view binding
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe currentLanguage LiveData for updates
        sharedViewModel.currentLanguage.observe(viewLifecycleOwner) { language ->
            updateStrings(language)  // Update the UI based on the current language
        }

        // Access the custom ActionBar and hide the MIOK title
        (activity as? AppCompatActivity)?.supportActionBar?.customView?.findViewById<View>(R.id.action_bar_title)?.visibility = View.GONE

        // Show the back arrow when navigating to the About fragment
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Function to translate text
    fun updateStrings(language: String) {
        when (language) {
            "English" -> {
                binding.aboutTitle.text = getString(R.string.aboutTitle)
                binding.aboutText.text = getString(R.string.aboutText)
                binding.aboutClient.text = getString(R.string.about_client)
                binding.aboutClientLabel.text = getString(R.string.about_client_label)
                binding.aboutGroup.text = getString(R.string.about_group)
                binding.aboutGroupLabel.text = getString(R.string.about_group_label)
                binding.aboutAppSpecification.text = getString(R.string.about_app_specification)
                binding.aboutAppQR.text = getString(R.string.about_app_QR)
            }
            "Māori" -> {
                binding.aboutTitle.text = getString(R.string.aboutTitle_mr)
                binding.aboutText.text = getString(R.string.aboutText_mr)
                binding.aboutClient.text = getString(R.string.about_client_mr)
                binding.aboutClientLabel.text = getString(R.string.about_client_label_mr)
                binding.aboutGroup.text = getString(R.string.about_group_mr)
                binding.aboutGroupLabel.text = getString(R.string.about_group_label_mr)
                binding.aboutAppSpecification.text = getString(R.string.about_app_specification_mr)
                binding.aboutAppQR.text = getString(R.string.about_app_QR_mr)
            }
        }
    }


}
