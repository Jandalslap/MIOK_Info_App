package com.example.miok_info_app.viewmodel

import androidx.lifecycle.*
import com.example.miok_info_app.data.Document
import com.example.miok_info_app.data.InformationRepository
import kotlinx.coroutines.launch

class InformationViewModel(private val repository: InformationRepository) : ViewModel() {
    private val _documents = MutableLiveData<List<Document>>()
    val documents: LiveData<List<Document>> = _documents

    // Fetch and format documents by their IDs
    fun fetchDocuments(documentIds: List<String>) {
        viewModelScope.launch {
            val formattedDocuments = documentIds.mapNotNull { id ->
                val documentSnapshot = repository.getDocumentById(id)
                documentSnapshot?.let { snapshot ->
                    val title = snapshot.getString("title") ?: ""
                    val content = snapshot.getString("content") ?: ""
                    val formattedContent = formatContent(content)
                    Document(title = title, content = formattedContent)
                }
            }
            _documents.value = formattedDocuments
        }
    }

    // Formatting function to handle subtitles, line breaks, etc.
    private fun formatContent(content: String): String {
        return content
            .replace("\\n", "\n") // Replace encoded line breaks with actual ones
    }
}
