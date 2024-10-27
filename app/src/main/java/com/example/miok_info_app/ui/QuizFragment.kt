package com.example.miok_info_app.ui

import ResultsAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.mikhaellopez.circularprogressbar.CircularProgressBar

// Fragment for managing the quiz interface and logic
class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null // Binding object for accessing views
    private val binding get() = _binding!! // Safe access to the binding object

    // Set up QuizViewModel with the factory
    private val viewModel: QuizViewModel by viewModels {
        QuizViewModelFactory(InformationRepository()) // Provide the repository to the ViewModel
    }

    private lateinit var resultsAdapter: ResultsAdapter // Declare results adapter

    // Inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false) // Inflate the layout
        return binding.root // Return the root view
    }

    // Called after the view has been created
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
            binding.resultsMessageText.text = "Quiz complete!\n\nYou answered\n $correctCount of $totalCount questions correctly."
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

        // Button click listeners for Yes, No and Next buttons
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

        // Set up the button click listener for See Questions button
        binding.seeQuestionsButton.setOnClickListener {
            // Hide the CircularProgressBar container
            binding.circularProgressBarContainer.visibility = View.GONE
            binding.seeQuestionsButton.visibility = View.GONE
            binding.resultsRecyclerView.visibility = View.VISIBLE

            // Update results in the adapter
            val results = viewModel.results.value ?: emptyList()
            resultsAdapter = ResultsAdapter(results)
            binding.resultsRecyclerView.adapter = resultsAdapter

            // Optionally, scroll to the top of the RecyclerView
            binding.resultsRecyclerView.smoothScrollToPosition(0)

            if (viewModel.latestCorrectCount == viewModel.totalQuestionsCount.value) {
                binding.findOutMoreButton.visibility = View.GONE
            } else
            // Show the "Find Out More" button
                binding.findOutMoreButton.visibility = View.VISIBLE

            // Set up click listener for "Find Out More" button
            binding.findOutMoreButton.setOnClickListener {
                navigateToInformationFragment()
            }
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

    // Display the current question and possible answers
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

    // Display the quiz results at the end
    @SuppressLint("SuspiciousIndentation")
    private fun displayResults() {
        binding.yesButton.visibility = View.GONE // Hide the answer buttons
        binding.noButton.visibility = View.GONE
        binding.questionText.visibility = View.GONE // Hide the question
        binding.answerContainer.visibility = View.GONE // Hide the answer container
        binding.dividerLine.visibility = View.GONE // Hide the divider line
        binding.nextButton.visibility = View.GONE // Hide the next button
        binding.resultsContainer.visibility = View.VISIBLE // Show results container


        //  Circular progress bar results graph
        val circularProgressBar = binding.circularProgressBar
        val progressText = binding.progressText

        // Assuming you have the latest correct count and total questions count from your ViewModel
        val latestCorrectCount = viewModel.latestCorrectCount
        val totalQuestionsCount = viewModel.totalQuestionsCount.value

        // Calculate the percentage with a check to prevent division by zero
        val progress = if (totalQuestionsCount != null && totalQuestionsCount != 0) {
            (latestCorrectCount.toFloat() / totalQuestionsCount * 100).toInt()
        } else {
            0
        }
        circularProgressBar.progress = progress.toFloat()
        progressText.text = "$progress%"

        // Retry Quiz button
        binding.retryQuizButton.setOnClickListener {
            viewModel.resetQuiz() // Call reset function in ViewModel
            resetUIForNewQuiz() // Reset UI elements to initial state
            binding.circularProgressBarContainer.visibility = View.VISIBLE
            binding.seeQuestionsButton.visibility = View.VISIBLE
            binding.findOutMoreButton.visibility = View.GONE
            binding.resultsRecyclerView.visibility = View.GONE
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
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.inactive_color)) // Inactive color

            if (i <= currentQuestionIndex) {
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.question_color)) // Active color
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

    // Clear the binding reference to avoid memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Set binding to null
    }
}
