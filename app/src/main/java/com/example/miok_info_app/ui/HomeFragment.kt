package com.example.miok_info_app.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.miok_info_app.R
import com.example.miok_info_app.data.InformationRepository
import com.example.miok_info_app.databinding.FragmentHomeBinding
import com.example.miok_info_app.viewmodel.HomeViewModel
import com.example.miok_info_app.viewmodel.HomeViewModelFactory

// Fragment representing the home screen of the application
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null // Binding object for accessing views
    private val binding get() = _binding!! // Safe access to the binding object

    // Initialize Firestore Repository
    private val repository = InformationRepository() // Repository instance for accessing Firestore data

    // Set up HomeViewModel with the factory
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(repository) // Provide the repository to the ViewModel
    }

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

        // Set up button listeners
        setupButtonListeners()

        // Access the custom ActionBar and show the MIOK title
        (activity as? AppCompatActivity)?.supportActionBar?.customView?.findViewById<View>(R.id.action_bar_title)?.visibility = View.VISIBLE


        // Observe LiveData from the ViewModel for document data
        viewModel.documentData.observe(viewLifecycleOwner, Observer { document ->
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

    // Fetch document data on button click
    private fun setupButtonListeners() {
        // Set up click listeners for each button to fetch corresponding document
        binding.buttonOne.setOnClickListener {
            viewModel.fetchDocument("info_button_1") // Fetch document for button 1
        }
        binding.buttonTwo.setOnClickListener {
            viewModel.fetchDocument("info_button_2") // Fetch document for button 2
        }
        binding.buttonThree.setOnClickListener {
            viewModel.fetchDocument("info_button_3") // Fetch document for button 3
        }
        binding.buttonFour.setOnClickListener {
            viewModel.fetchDocument("info_button_4") // Fetch document for button 4
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
}
