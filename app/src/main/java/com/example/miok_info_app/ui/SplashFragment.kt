package com.example.miok_info_app.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.miok_info_app.R

// Fragment for displaying a splash screen with a delay before navigating to the disclaimer screen
class SplashFragment : Fragment() {

    // Inflate the splash screen layout when the fragment's view is created
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false) // Inflate layout
    }

    // Set up navigation from splash screen to disclaimer screen after a delay
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use a handler to delay navigation by 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_splashFragment_to_disclaimerFragment) // Navigate to disclaimer
        }, 3000) // 3-second delay
    }
}
