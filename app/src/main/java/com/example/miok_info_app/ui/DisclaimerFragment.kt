package com.example.miok_info_app.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.miok_info_app.R
import com.example.miok_info_app.databinding.FragmentDisclaimerBinding

// Fragment that displays a disclaimer message to the user
class DisclaimerFragment : Fragment() {

    private var _binding: FragmentDisclaimerBinding? = null // Binding object for accessing views
    private val binding get() = _binding!! // Safe access to the binding object

    // Inflates the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDisclaimerBinding.inflate(inflater, container, false) // Inflate the layout
        return binding.root // Return the root view
    }

    // Called after the view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up click listener for the accept button
        binding.acceptButton.setOnClickListener {
            findNavController().navigate(R.id.action_disclaimerFragment_to_homeFragment) // Navigate to the home fragment
        }

        // Set up click listener for the decline button
        binding.declineButton.setOnClickListener {
            requireActivity().finish() // Close the app if declined
        }
    }

    // Clean up the binding reference when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks by setting binding to null
    }
}
