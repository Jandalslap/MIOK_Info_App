package com.example.miok_info_app.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.miok_info_app.data.InformationRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: InformationRepository) : ViewModel() {

    private val _questions = MutableLiveData<List<DocumentSnapshot>>()
    val questions: LiveData<List<DocumentSnapshot>> get() = _questions

    // List to store each question and whether the answer was correct
    private val _results = MutableLiveData<List<Pair<DocumentSnapshot, Boolean>>>()
    val results: LiveData<List<Pair<DocumentSnapshot, Boolean>>> get() = _results

    private val _currentQuestionIndex = MutableLiveData<Int>().apply { value = 0 }

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

            // Update the results with the current question and whether the answer was correct
            val currentResults = _results.value?.toMutableList() ?: mutableListOf()
            currentResults.add(Pair(question, isCorrect))
            _results.value = currentResults

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
    }
}
