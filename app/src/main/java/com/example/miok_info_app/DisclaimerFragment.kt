package com.example.miok_info_app.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.miok_info_app.R
import com.example.miok_info_app.databinding.FragmentDisclaimerBinding

class DisclaimerFragment : Fragment() {

    private var _binding: FragmentDisclaimerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDisclaimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.acceptButton.setOnClickListener {
            findNavController().navigate(R.id.action_disclaimerFragment_to_homeFragment)
        }

        binding.declineButton.setOnClickListener {
            requireActivity().finish() // Close the app
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
