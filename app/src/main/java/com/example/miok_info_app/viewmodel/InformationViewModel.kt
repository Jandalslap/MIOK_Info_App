package com.example.miok_info_app.ui

import androidx.lifecycle.*
import com.example.miok_info_app.data.Document
import com.example.miok_info_app.data.InformationRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class InformationViewModel(private val repository: InformationRepository) : ViewModel() {
    private val _documents = MutableLiveData<List<Document>>()
    val documents: LiveData<List<Document>> get() = _documents

    fun fetchDocuments(documentIds: List<String>) {
        viewModelScope.launch {
            val documentsList = mutableListOf<Document>()
            for (documentId in documentIds) {
                val document = repository.getDocumentById(documentId) // This is a suspend function
                if (document != null && document.exists()) {
                    val title = document.getString("title") ?: "No Title"
                    val content = document.getString("content") ?: "No Content"
                    documentsList.add(Document(title, content))
                }
            }
            _documents.value = documentsList // Update LiveData with the list of documents
        }
    }
}
