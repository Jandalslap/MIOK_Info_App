// SharedViewModel.kt
package com.example.miok_info_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _currentLanguage = MutableLiveData<String>("English") // Holds the current language
    val currentLanguage: LiveData<String> get() = _currentLanguage // Publicly accessible current language

    private val _currentDocumentId = MutableLiveData<String>() // Holds the ID of the current document
    val currentDocumentId: LiveData<String> get() = _currentDocumentId // Publicly accessible document ID

    private val _currentQuizQuestionId = MutableLiveData<String>() // Holds the ID of the current quiz question
    val currentQuizQuestionId: LiveData<String> get() = _currentQuizQuestionId // Publicly accessible quiz question ID

    // Updates the current language
    fun updateLanguage(language: String) {
        _currentLanguage.value = language
    }

    // Updates the current document ID
    fun updateCurrentDocumentId(documentId: String) {
        _currentDocumentId.value = documentId
    }

    // Updates the current quiz question ID
    fun updateCurrentQuizQuestionId(questionId: String) {
        _currentQuizQuestionId.value = questionId
    }
}
