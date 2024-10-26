package com.example.miok_info_app.viewmodel

import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.example.miok_info_app.R
import com.example.miok_info_app.data.InformationRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: InformationRepository) : ViewModel() {

    private val _questions = MutableLiveData<List<DocumentSnapshot>>()
    val questions: LiveData<List<DocumentSnapshot>> get() = _questions

    private val _correctAnswersCount = MutableLiveData(0)
    val correctAnswersCount: LiveData<Int> get() = _correctAnswersCount

    // Public variable to store the latest correct count, updated each time `correctAnswersCount` changes
    var latestCorrectCount: Int = 0
        private set

    init {
        // Observe correctAnswersCount to update latestCorrectCount each time it changes
        correctAnswersCount.observeForever { newCorrectCount ->
            latestCorrectCount = newCorrectCount
        }
    }

    private val _totalQuestionsCount = MutableLiveData(1)
    val totalQuestionsCount: LiveData<Int> get() = _totalQuestionsCount

    // List to store each question and whether the answer was correct
    private val _results = MutableLiveData<List<Pair<DocumentSnapshot, Boolean>>>()
    val results: LiveData<List<Pair<DocumentSnapshot, Boolean>>> get() = _results

    private val _currentQuestionIndex = MutableLiveData<Int>(0)
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    private val _feedbackStatus = MutableLiveData<Boolean?>() // Use Boolean to indicate correct/incorrect or null for no feedback
    val feedbackStatus: LiveData<Boolean?> get() = _feedbackStatus

    // LiveData that will hold the current question based on the current index
    val currentQuestion: LiveData<DocumentSnapshot?> = MediatorLiveData<DocumentSnapshot?>().apply {
        addSource(_questions) { questions ->
            value = questions.getOrNull(_currentQuestionIndex.value ?: 0)
        }
        addSource(_currentQuestionIndex) { index ->
            value = _questions.value?.getOrNull(index)
        }
    }

    private val _quizCompleted = MutableLiveData(false)
    val isQuizCompleted: LiveData<Boolean> get() = _quizCompleted

    init {
        loadQuestions()
    }

    fun loadQuestions() {
        viewModelScope.launch {
            // Fetch questions from the repository
            _questions.value = repository.getQuizQuestions() // Ensure this method fetches the questions correctly
            Log.d("QuizViewModel", "Questions loaded: ${_questions.value?.size} questions retrieved.")

            // Initialize the current question index to 0
            _currentQuestionIndex.value = 0 // Start from the first question
        }
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

            // Update total questions count
            _totalQuestionsCount.value = (_totalQuestionsCount.value ?: 0) + 1

        }
    }

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

    fun resetQuiz() {
        _totalQuestionsCount.value = 1
        _currentQuestionIndex.value = 0
        _correctAnswersCount.value = 0
        _results.value = emptyList() // Clear any existing results
        _quizCompleted.value = false
        loadQuestions() // Reload questions if they need to be fetched from the repository again
    }


}
