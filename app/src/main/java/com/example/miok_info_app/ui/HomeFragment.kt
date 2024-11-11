package com.example.miok_info_app.ui

import HomeViewModelFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.miok_info_app.R
import com.example.miok_info_app.data.InformationRepository
import com.example.miok_info_app.databinding.FragmentHomeBinding
import com.example.miok_info_app.viewmodel.HomeViewModel
import com.example.miok_info_app.viewmodel.SharedViewModel
import com.google.android.material.navigation.NavigationView

// Fragment representing the home screen of the application
class HomeFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(InformationRepository(), sharedViewModel)
    }

    private var _binding: FragmentHomeBinding? = null // Binding object for accessing views
    private val binding get() = _binding!! // Safe access to the binding object

    // Initialize Firestore Repository
    private val repository = InformationRepository() // Repository instance for accessing Firestore data

    // Inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false) // Inflate the layout
        return binding.root // Return the root view
    }


    // Called after the view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup button listeners
        binding.buttonOne.setOnClickListener { navigateToInfoFragment("info_button_1") }
        binding.buttonTwo.setOnClickListener { navigateToInfoFragment("info_button_2") }
        binding.buttonThree.setOnClickListener { navigateToInfoFragment("info_button_3") }
        binding.buttonFour.setOnClickListener { navigateToInfoFragment("info_button_4") }

        // Access the NavigationView and find the toggle switch and text view
        val navView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        val toggleSwitch = navView.findViewById<Switch>(R.id.language_toggle)
        val languageText = navView.findViewById<TextView>(R.id.language_text)

        // Observe currentLanguage LiveData for updates
        sharedViewModel.currentLanguage.observe(viewLifecycleOwner) { language ->
            toggleSwitch.isChecked = (language == "English")
            languageText.text = language
            updateStrings(language)
        }

        // Set up the toggle listener for language preference
        toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
            val language = if (isChecked) {
                "English"
            } else {
                "Māori"
            }
            languageText.text = language // Set text based on checked state
            // Update the language in the shared view model
            sharedViewModel.updateLanguage(language)
        }

        // Set up button listeners
        setupButtonListeners()

        // Access the custom ActionBar and show the MIOK title
        (activity as? AppCompatActivity)?.supportActionBar?.customView?.findViewById<View>(R.id.action_bar_title)?.visibility = View.VISIBLE

        // Observe LiveData from the ViewModel for document data
        homeViewModel.documentData.observe(viewLifecycleOwner, Observer { document ->
            if (document != null) {
                val documentId = document.id // Get the ID of the fetched document
                findNavController().navigate(
                    R.id.informationFragment,
                    bundleOf("documentIds" to arrayListOf(documentId)) // Pass a list even if it's a single item
                )
            } else {
                Log.e("HomeFragment", "Document not found or error occurred") // Log an error if document is null
            }
        })
    }


    private fun navigateToInfoFragment(documentId: String) {
        // Update the document ID in the shared view model
        sharedViewModel.updateCurrentDocumentId(documentId)
        // Navigate to InformationFragment and pass the document ID
        val bundle = bundleOf("documentIds" to arrayListOf(documentId))
        findNavController().navigate(R.id.informationFragment, bundle)
    }

    // Fetch document data on button click
    private fun setupButtonListeners() {
        // Set up click listeners for each button to fetch corresponding document
        binding.buttonOne.setOnClickListener {
            homeViewModel.fetchDocument("info_button_1") // Fetch document for button 1
        }
        binding.buttonTwo.setOnClickListener {
            homeViewModel.fetchDocument("info_button_2") // Fetch document for button 2
        }
        binding.buttonThree.setOnClickListener {
            homeViewModel.fetchDocument("info_button_3") // Fetch document for button 3
        }
        binding.buttonFour.setOnClickListener {
            homeViewModel.fetchDocument("info_button_4") // Fetch document for button 4
        }
        binding.quizButton.setOnClickListener {
            findNavController().navigate(R.id.quizFragment) // Navigate to the quiz fragment
        }
    }

    // Called when the fragment is visible to the user
    override fun onResume() {
        super.onResume()
        // Hide the back arrow when on the home fragment
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    // Clean up the binding reference when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding reference to avoid memory leaks
    }

    // Function change textView language
    private fun updateStrings(language: String) {
        val context = requireContext()
        val isMaori = language == "Māori"

        binding.homeTextView.text = context.getString(
            context.resources.getIdentifier(
                if (isMaori) "homeTextView_mr" else "homeTextView",
                "string",
                context.packageName
            )
        )

        binding.buttonOne.text = context.getString(
            context.resources.getIdentifier(
                if (isMaori) "button_one_mr" else "button_one",
                "string",
                context.packageName
            )
        )

        binding.buttonTwo.text = context.getString(
            context.resources.getIdentifier(
                if (isMaori) "button_two_mr" else "button_two",
                "string",
                context.packageName
            )
        )

        binding.buttonThree.text = context.getString(
            context.resources.getIdentifier(
                if (isMaori) "button_three_mr" else "button_three",
                "string",
                context.packageName
            )
        )

        binding.buttonFour.text = context.getString(
            context.resources.getIdentifier(
                if (isMaori) "button_four_mr" else "button_four",
                "string",
                context.packageName
            )
        )

        binding.quizButton.text = context.getString(
            context.resources.getIdentifier(
                if (isMaori) "quizButton_mr" else "quizButton",
                "string",
                context.packageName
            )
        )
    }
}
