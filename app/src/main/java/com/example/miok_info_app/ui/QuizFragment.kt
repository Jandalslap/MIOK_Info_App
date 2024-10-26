package com.example.miok_info_app.ui

import ResultsAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miok_info_app.R
import com.example.miok_info_app.data.InformationRepository
import com.example.miok_info_app.databinding.FragmentQuizBinding
import com.example.miok_info_app.viewmodel.QuizViewModel
import com.example.miok_info_app.viewmodel.QuizViewModelFactory
import com.google.firebase.firestore.DocumentSnapshot

class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizViewModel by viewModels {
        QuizViewModelFactory(InformationRepository())
    }

    private lateinit var resultsAdapter: ResultsAdapter // Declare results adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe feedback status
        viewModel.feedbackStatus.observe(viewLifecycleOwner, Observer { isCorrect ->
            isCorrect?.let {
                displayFeedback(it) // Call displayFeedback with the result
            }
        })

        viewModel.correctAnswersCount.observe(viewLifecycleOwner, Observer { correctCount ->
            val totalCount = viewModel.totalQuestionsCount.value ?: 0

            // Check if the correctCount is 0 and set the message accordingly
            if (correctCount == 0) {
                binding.resultsMessageText.text = "You got 0/$totalCount. Try Again?"
            } else {
                binding.resultsMessageText.text = "Well done! You got $correctCount/$totalCount correct!"
            }
        })


        // Set up the results RecyclerView
        resultsAdapter = ResultsAdapter(emptyList())
        binding.resultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.resultsRecyclerView.adapter = resultsAdapter

        // Initially hide the answer container
        binding.answerContainer.visibility = View.GONE
        // Initially hide the feedback text
        binding.feedbackText.visibility = View.GONE

        binding.nextButton.visibility = View.GONE // Hide the next button

        viewModel.currentQuestion.observe(viewLifecycleOwner, Observer { question ->
            if (question != null) {
                Log.d("QuizFragment", "Current question: ${question.getString("title")}")
                displayQuestion(question)
            } else {
                Log.d("QuizFragment", "Current question is null")
            }
        })

        // Button click listeners for Yes and No
        binding.yesButton.setOnClickListener {
            disableAnswerButtons()
            viewModel.answerQuestion(true)
            binding.answerContainer.visibility = View.VISIBLE // Show the answer container
            binding.nextButton.visibility = View.VISIBLE // Show the next button
            Log.d("QuizFragment", "Yes button clicked")
        }
        binding.noButton.setOnClickListener {
            disableAnswerButtons()
            viewModel.answerQuestion(false)
            binding.answerContainer.visibility = View.VISIBLE // Show the answer container
            binding.nextButton.visibility = View.VISIBLE // Show the next button
            Log.d("QuizFragment", "No button clicked")
        }
        binding.nextButton.setOnClickListener {
            viewModel.goToNextQuestion()
            binding.answerContainer.visibility = View.GONE // Show the answer container
            binding.nextButton.visibility = View.GONE // Show the next button
            binding.feedbackText.visibility = View.GONE // Hide feedback text
            Log.d("QuizFragment", "Next button clicked")
        }

        // Observe quiz completion
        viewModel.isQuizCompleted.observe(viewLifecycleOwner, Observer { completed ->
            if (completed) {
                displayResults()
            }
        })

        // Load questions from the database
        viewModel.loadQuestions()
    }

    private fun disableAnswerButtons() {
        binding.yesButton.isEnabled = false
        binding.noButton.isEnabled = false
    }

    private fun enableAnswerButtons() {
        binding.yesButton.isEnabled = true
        binding.noButton.isEnabled = true
    }

    private fun displayQuestion(question: DocumentSnapshot?) {
        question?.let {
            binding.questionText.text = it.getString("title") ?: "Question not available"

            // Get the content from the question and bind it to answerContentText
            val answerContent = it.getString("content") ?: "Content not available"
            binding.answerContentText.text = answerContent

            // Update progress bar
            val currentQuestionIndex = viewModel.currentQuestionIndex.value ?: 0
            updateProgressBar(currentQuestionIndex)

            enableAnswerButtons()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun displayResults() {
        binding.yesButton.visibility = View.GONE // Hide the answer buttons
        binding.noButton.visibility = View.GONE
        binding.questionText.visibility = View.GONE // Hide the question
        binding.answerContainer.visibility = View.GONE // Hide the answer container
        binding.dividerLine.visibility = View.GONE // Hide the divider line
        binding.nextButton.visibility = View.GONE // Hide the next button
        binding.resultsContainer.visibility = View.VISIBLE // Show results container

        // Update results in the adapter
        resultsAdapter = ResultsAdapter(viewModel.results.value ?: emptyList())
        binding.resultsRecyclerView.adapter = resultsAdapter

        binding.retryQuizButton.setOnClickListener {
            viewModel.resetQuiz() // Call reset function in ViewModel
            resetUIForNewQuiz() // Reset UI elements to initial state
        }

        if (viewModel.latestCorrectCount == 5) {
            binding.findOutMoreButton.visibility = View.GONE
        } else
            // Show the "Find Out More" button
            binding.findOutMoreButton.visibility = View.VISIBLE

            // Set up click listener for "Find Out More" button
            binding.findOutMoreButton.setOnClickListener {
            navigateToInformationFragment()
            }
    }

    // Helper function to reset UI elements for a new quiz
    private fun resetUIForNewQuiz() {
        binding.resultsContainer.visibility = View.GONE // Hide results
        binding.yesButton.visibility = View.VISIBLE
        binding.noButton.visibility = View.VISIBLE
        binding.questionText.visibility = View.VISIBLE
    }
    private fun updateProgressBar(currentQuestionIndex: Int) {
        Log.d("QuizFragment", "Updating progress bar for question index: $currentQuestionIndex")
        for (i in 0 until binding.progressBar.childCount) {
            val view = binding.progressBar.getChildAt(i)
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.your_inactive_color)) // Inactive color

            if (i <= currentQuestionIndex) {
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.your_question_color)) // Active color
                Log.d("QuizFragment", "Progress bar updated at index $i to active color.")
            }
        }
    }

    private fun displayFeedback(isCorrect: Boolean) {
        val feedbackMessage = if (isCorrect) "Correct!" else "Incorrect"
        binding.feedbackText.text = feedbackMessage
        binding.feedbackText.visibility = View.VISIBLE // Make sure the feedback text is visible
        Log.d("QuizFragment", "Feedback displayed: $feedbackMessage")
    }

    private fun navigateToInformationFragment() {
        val incorrectQuestions = viewModel.results.value?.filter { !it.second }?.map { it.first }

        // If there are incorrect questions, navigate to InformationFragment
        if (!incorrectQuestions.isNullOrEmpty()) {
            val bundle = Bundle().apply {
                putStringArrayList("documentIds", ArrayList(incorrectQuestions.map { it.id }))
            }
            // Navigate to InformationFragment with the bundle
            findNavController().navigate(R.id.action_quizFragment_to_informationFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding reference to avoid memory leaks
    }
}
