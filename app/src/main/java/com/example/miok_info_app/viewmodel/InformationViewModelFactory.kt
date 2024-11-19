package com.example.miok_info_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.miok_info_app.data.InformationRepository
import com.example.miok_info_app.viewmodel.InformationViewModel

// Factory for creating instances of InformationViewModel with required dependencies: InformationRepository and SharedViewModel.
class InformationViewModelFactory(
    private val repository: InformationRepository,
    private val sharedViewModel: SharedViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InformationViewModel::class.java)) {
            return InformationViewModel(repository, sharedViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

