package com.example.miok_info_app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miok_info_app.R
import com.example.miok_info_app.data.Document
import com.example.miok_info_app.data.InformationRepository
import com.example.miok_info_app.databinding.FragmentInformationBinding
import com.example.miok_info_app.viewmodel.InformationViewModel
import com.example.miok_info_app.viewmodel.InformationViewModelFactory

// Fragment for displaying information documents in a RecyclerView
class InformationFragment : Fragment() {
    private var _binding: FragmentInformationBinding? = null // Binding object for accessing views
    private val binding get() = _binding!! // Safe access to the binding object

    private lateinit var adapter: DocumentAdapter // Adapter for the RecyclerView

    // Set up InformationViewModel with the factory
    private val viewModel: InformationViewModel by viewModels {
        InformationViewModelFactory(InformationRepository()) // Provide the repository to the ViewModel
    }

    // Inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInformationBinding.inflate(inflater, container, false) // Inflate the layout
        return binding.root // Return the root view
    }

    // Called after the view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the RecyclerView with a linear layout manager
        adapter = DocumentAdapter() // Initialize the DocumentAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Set layout manager
        binding.recyclerView.adapter = adapter // Set the adapter to the RecyclerView

        // Get the document IDs from arguments (expecting a list)
        val documentIds = arguments?.getStringArrayList("documentIds") ?: return // Retrieve document IDs

        // Observe the documents LiveData from the ViewModel
        viewModel.documents.observe(viewLifecycleOwner, Observer { documents ->
            adapter.submitList(documents) // Submit the list of documents to the adapter
        })

        // Fetch documents based on the list of document IDs
        viewModel.fetchDocuments(documentIds) // Call the ViewModel to fetch documents
    }

    // Clean up the binding reference when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding reference to avoid memory leaks
    }
}
