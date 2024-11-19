package com.example.miok_info_app.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.miok_info_app.data.InformationRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

// ViewModel for managing quiz-related data and sharing state with SharedViewModel
class QuizViewModel(
    private val repository: InformationRepository,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {

    init {
        sharedViewModel.currentLanguage.observeForever { language ->
            onLanguageChanged(language)
        }
        loadQuestions() // Load questions on initialization
    }

    // Handle changes to the selected language
    private fun onLanguageChanged(language: String) {
        loadQuestions() // Reload questions when the language changes
    }

    private val _questions = MutableLiveData<List<DocumentSnapshot>>() // LiveData to hold the list of questions
    val questions: LiveData<List<DocumentSnapshot>> get() = _questions  // Expose questions as LiveData

    private val _correctAnswersCount = MutableLiveData(0) // LiveData to track correct answers count
    val correctAnswersCount: LiveData<Int> get() = _correctAnswersCount // Expose correct answers count as LiveData

    // Public variable to store the latest correct count, updated each time `correctAnswersCount` changes
    var latestCorrectCount: Int = 0
        private set // Set the latest correct count, accessible but not modifiable externally

    init {
        // Observe correctAnswersCount to update latestCorrectCount each time it changes
        correctAnswersCount.observeForever { newCorrectCount ->
            latestCorrectCount = newCorrectCount
        }
    }

    private val _totalQuestionsCount = MutableLiveData(0) // LiveData to hold total number of questions
    val totalQuestionsCount: LiveData<Int> get() = _totalQuestionsCount // Expose total questions count as LiveData

    // List to store each question and whether the answer was correct
    private val _results = MutableLiveData<List<Pair<DocumentSnapshot, Boolean>>>() // LiveData for storing results
    val results: LiveData<List<Pair<DocumentSnapshot, Boolean>>> get() = _results // Expose results as LiveData

    private val _currentQuestionIndex = MutableLiveData<Int>(0) // LiveData for tracking the current question index
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex // Expose current question index as LiveData

    private val _feedbackStatus = MutableLiveData<Boolean?>() // Use Boolean to indicate correct/incorrect or null for no feedback
    val feedbackStatus: LiveData<Boolean?> get() = _feedbackStatus // Expose feedback status as LiveData

    // LiveData that will hold the current question based on the current index
    val currentQuestion: LiveData<DocumentSnapshot?> = MediatorLiveData<DocumentSnapshot?>().apply {
        addSource(_questions) { questions ->
            value = questions.getOrNull(_currentQuestionIndex.value ?: 0)
        }
        addSource(_currentQuestionIndex) { index ->
            value = _questions.value?.getOrNull(index)
        }
    }

    private val _quizCompleted = MutableLiveData(false) // LiveData to track quiz completion status
    val isQuizCompleted: LiveData<Boolean> get() = _quizCompleted // Expose quiz completion status as LiveData


    // Function to load questions based on the current language
    fun loadQuestions() {
        viewModelScope.launch {
            val fetchedQuestions = repository.getQuizQuestions()
            _questions.value = fetchedQuestions
            _totalQuestionsCount.value = fetchedQuestions.size
        }
    }

    // Helper method to get the title based on the current language
    fun getQuestionTitle(question: DocumentSnapshot): String? {
        val language = sharedViewModel.currentLanguage.value ?: "English"
        val titleField = if (language == "Māori") "title_mr" else "title"
        return question.getString(titleField)
    }

    // Helper method to get the content based on the current language
    fun getQuestionContent(question: DocumentSnapshot): String? {
        val language = sharedViewModel.currentLanguage.value ?: "English"
        val contentField = if (language == "Māori") "content_mr" else "content"
        return question.getString(contentField)
    }

    // Answer the current question and track correctness
    fun answerQuestion(userAnswer: Boolean) {
        val currentQuestion = currentQuestion.value
        currentQuestion?.let { question ->
            val correctAnswer = question.getString("answer") == "yes"
            val isCorrect = (userAnswer == correctAnswer)

            // Update feedback status
            _feedbackStatus.value = isCorrect // Set feedback status

            // Update the results with the current question and whether the answer was correct
            val currentResults = _results.value?.toMutableList() ?: mutableListOf()
            currentResults.add(Pair(question, isCorrect))
            _results.value = currentResults

            // Update correct answers count
            if (isCorrect) {
                _correctAnswersCount.value = (_correctAnswersCount.value ?: 0) + 1
            }

        }
    }

    // Function to move to the next question or mark the quiz as complete
    fun goToNextQuestion() {
        // Move to the next question or mark the quiz as complete
        val nextIndex = (_currentQuestionIndex.value ?: 0) + 1
        if (nextIndex < (_questions.value?.size ?: 0)) {
            _currentQuestionIndex.value = nextIndex
            Log.d("QuizViewModel", "Moved to next question, new index: ${_currentQuestionIndex.value}")
        } else {
            _quizCompleted.value = true
            Log.d("QuizViewModel", "Quiz completed")
        }
    }

    // Function to reset quiz
    fun resetQuiz() {
        _currentQuestionIndex.value = 0
        _correctAnswersCount.value = 0
        _results.value = emptyList() // Clear any existing results
        _quizCompleted.value = false
        loadQuestions() // Reload questions if they need to be fetched from the repository again
    }


}
