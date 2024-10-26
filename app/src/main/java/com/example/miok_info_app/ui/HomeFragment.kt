package com.example.miok_info_app.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Initialize Firestore Repository
    private val repository = InformationRepository()

    // Set up HomeViewModel with the factory
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up button listeners
        setupButtonListeners()

        // Observe LiveData from the ViewModel
        viewModel.documentData.observe(viewLifecycleOwner, Observer { document ->
            if (document != null) {
                val documentId = document.id
                findNavController().navigate(
                    R.id.informationFragment,
                    bundleOf("documentIds" to arrayListOf(documentId)) // Pass a list even if it's a single item
                )
            } else {
                Log.e("HomeFragment", "Document not found or error occurred")
            }
        })
    }

    // Fetch document data on button click
    private fun setupButtonListeners() {
        binding.buttonOne.setOnClickListener {
            viewModel.fetchDocument("info_button_1")
        }
        binding.buttonTwo.setOnClickListener {
            viewModel.fetchDocument("info_button_2")
        }
        binding.buttonThree.setOnClickListener {
            viewModel.fetchDocument("info_button_3")
        }
        binding.buttonFour.setOnClickListener {
            viewModel.fetchDocument("info_button_4")
        }
        binding.quizButton.setOnClickListener {
            findNavController().navigate(R.id.quizFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding reference to avoid memory leaks
    }
}
