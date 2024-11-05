package com.example.miok_info_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.miok_info_app.data.InformationRepository

class QuizViewModelFactory(
    private val repository: InformationRepository,
    private val sharedViewModel: SharedViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            return QuizViewModel(repository, sharedViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}