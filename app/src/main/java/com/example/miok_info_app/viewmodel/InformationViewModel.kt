package com.example.miok_info_app.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.miok_info_app.data.Document
import com.example.miok_info_app.data.InformationRepository
import kotlinx.coroutines.launch

class InformationViewModel(
    private val repository: InformationRepository,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {

    // Observe language changes to update documents when the language changes
    init {
        sharedViewModel.currentLanguage.observeForever { language ->
            onLanguageChanged(language)
        }
    }

    // Refetches documents in the new language if IDs are available
    private fun onLanguageChanged(language: String) {
        // Check if documentIds is not null and not empty before fetching documents
        if (!documentIds.isNullOrEmpty()) {
            fetchDocuments(documentIds, language)
        } else {
            // Handle the case where documentIds is null or empty
            Log.w("InformationViewModel", "documentIds is null or empty. Unable to fetch documents.")
        }
    }


    private val _documents = MutableLiveData<List<Document>>() // Holds fetched documents
    val documents: LiveData<List<Document>> = _documents // Publicly accessible LiveData
    private var documentIds: List<String> = emptyList() // Stores document IDs for language switching


    // Function to fetch and format documents based on IDs and language
    fun fetchDocuments(documentIds: List<String>, language: String = sharedViewModel.currentLanguage.value ?: "English") {
        this.documentIds = documentIds // Store IDs for future re-fetch on language change
        viewModelScope.launch {
            val formattedDocuments = documentIds.mapNotNull { id ->
                val documentSnapshot = repository.getDocumentById(id)
                documentSnapshot?.let { snapshot ->
                    // Select fields based on language
                    val titleField = if (language == "Māori") "title_mr" else "title"
                    val contentField = if (language == "Māori") "content_mr" else "content"
                    val title = snapshot.getString(titleField) ?: ""
                    val content = snapshot.getString(contentField) ?: ""
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
