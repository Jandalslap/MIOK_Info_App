package com.example.miok_info_app.viewmodel

import androidx.lifecycle.*
import com.example.miok_info_app.data.InformationRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: InformationRepository,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {

    // Observe currentLanguage LiveData for updates and handle language changes
    init {
        sharedViewModel.currentLanguage.observeForever { language ->
            onLanguageChanged(language)
        }
    }

    private fun onLanguageChanged(language: String) {
        // Perform actions based on the updated language if required
    }

    private val _documentData = MutableLiveData<DocumentSnapshot?>()
    val documentData: LiveData<DocumentSnapshot?> get() = _documentData


    // Function to fetch a document by ID
    fun fetchDocument(documentId: String) {
        viewModelScope.launch {
            try {
                // Attempt to fetch the document
                val document = repository.getDocumentById(documentId)
                _documentData.value = document
            } catch (e: Exception) {
                // Handle any errors
                _documentData.value = null // Set to null if an error occurs
                e.printStackTrace() // Optionally log the error
            }
        }
    }
}

