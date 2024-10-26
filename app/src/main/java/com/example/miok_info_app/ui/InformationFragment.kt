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

class InformationFragment : Fragment() {
    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DocumentAdapter

    private val viewModel: InformationViewModel by viewModels {
        InformationViewModelFactory(InformationRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the RecyclerView
        adapter = DocumentAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Get the document IDs from arguments (expecting a list)
        val documentIds = arguments?.getStringArrayList("documentIds") ?: return

        // Observe the documents LiveData from the ViewModel
        viewModel.documents.observe(viewLifecycleOwner, Observer { documents ->
            adapter.submitList(documents)
        })

        // Fetch documents based on the list of document IDs
        viewModel.fetchDocuments(documentIds)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding reference to avoid memory leaks
    }
}
