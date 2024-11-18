package com.example.miok_info_app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miok_info_app.R
import com.example.miok_info_app.data.InformationRepository
import com.example.miok_info_app.databinding.FragmentInformationBinding
import com.example.miok_info_app.viewmodel.InformationViewModel
import com.example.miok_info_app.viewmodel.InformationViewModelFactory
import com.example.miok_info_app.viewmodel.SharedViewModel

class InformationFragment : Fragment() {
    // ViewModel shared across fragments
    private val sharedViewModel: SharedViewModel by activityViewModels()
    // ViewModel for managing information data, initialized with a repository and shared view model
    private val informationViewModel: InformationViewModel by viewModels {
        InformationViewModelFactory(InformationRepository(), sharedViewModel)
    }

    // Binding object to access UI elements in the fragment
    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    // Adapter for displaying documents in a RecyclerView
    private lateinit var adapter: DocumentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment and return the root view
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the custom ActionBar and hide the MIOK title
        (activity as? AppCompatActivity)?.supportActionBar?.customView?.findViewById<View>(R.id.action_bar_title)?.visibility = View.GONE

        // Show the back arrow when navigating to the Information fragment
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize the adapter for the RecyclerView
        adapter = DocumentAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Set layout manager for the RecyclerView
        binding.recyclerView.adapter = adapter // Set the adapter to the RecyclerView

        // Retrieve document IDs passed as arguments to this fragment
        val documentIds = arguments?.getStringArrayList("documentIds") ?: return

        // Observe the documents LiveData from the ViewModel and update the adapter when data changes
        informationViewModel.documents.observe(viewLifecycleOwner, Observer { documents ->
            adapter.submitList(documents) // Update the adapter with the new list of documents
        })

        // Fetch documents using the retrieved document IDs
        informationViewModel.fetchDocuments(documentIds)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up the binding reference to prevent memory leaks
        _binding = null
    }
}
