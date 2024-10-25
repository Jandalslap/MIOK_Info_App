package com.example.miok_info_app.viewmodel

import androidx.lifecycle.*
import com.example.miok_info_app.data.InformationRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: InformationRepository) : ViewModel() {

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
                // Handle any errors (e.g., log or notify)
                _documentData.value = null // Set to null if an error occurs
                e.printStackTrace() // Optionally log the error
            }
        }
    }
}

// Factory for creating HomeViewModel with the repository as a dependency
class HomeViewModelFactory(private val repository: InformationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
