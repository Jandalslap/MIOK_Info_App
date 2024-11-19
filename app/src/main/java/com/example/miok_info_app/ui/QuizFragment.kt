package com.example.miok_info_app.ui

import ResultsAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miok_info_app.R
import com.example.miok_info_app.data.InformationRepository
import com.example.miok_info_app.databinding.FragmentQuizBinding
import com.example.miok_info_app.viewmodel.QuizViewModel
import com.example.miok_info_app.viewmodel.QuizViewModelFactory
import com.example.miok_info_app.viewmodel.SharedViewModel
import com.google.firebase.firestore.DocumentSnapshot

// Fragment for managing the quiz interface and logic
class QuizFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val quizViewModel: QuizViewModel by viewModels {
        QuizViewModelFactory(InformationRepository(), sharedViewModel)
    }

    private var _binding: FragmentQuizBinding? = null // Binding object for accessing views
    private val binding get() = _binding!! // Safe access to the binding object

    private lateinit var resultsAdapter: ResultsAdapter // Declare results adapter

    private var lastQuizCompletionMessage: String? = null

    // Store the last feedback status to re-display when language changes
    private var lastFeedbackStatus: Boolean? = null

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

        // Set up the results RecyclerView with the current language
        val currentLanguage = sharedViewModel.currentLanguage.value ?: "English"
        resultsAdapter = ResultsAdapter(emptyList(), currentLanguage)
        binding.resultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.resultsRecyclerView.adapter = resultsAdapter

        // Access the custom ActionBar and hide the MIOK title
        (activity as? AppCompatActivity)?.supportActionBar?.customView?.findViewById<View>(R.id.action_bar_title)?.visibility = View.GONE


        // Observe feedback status
        quizViewModel.feedbackStatus.observe(viewLifecycleOwner, Observer { isCorrect ->
            isCorrect?.let {
                lastFeedbackStatus = it // Store the last feedback status
                displayFeedback(it) // Call displayFeedback with the result
            }
        })

        // Observe currentLanguage LiveData for updates
        sharedViewModel.currentLanguage.observe(viewLifecycleOwner) { language ->
            updateStrings(language) // Update the UI strings based on the new language

            // Update feedback based on the last feedback status
            lastFeedbackStatus?.let {
                displayFeedback(it) // Re-display the last feedback message
            }

            // Get current quiz statistics
            val correctCount = quizViewModel.correctAnswersCount.value ?: 0
            val totalCount = quizViewModel.totalQuestionsCount.value ?: 0

            // Update the quiz completion message and hide feedback
            updateQuizCompletionMessage(correctCount, totalCount)
            binding.feedbackText.visibility = View.GONE

            // Re-fetch results based on the current language
            quizViewModel.results.value?.let { results ->
                updateResultsAdapter(language, results) // Update the results adapter with the new language
            }
        }


        // Observe correct answers count to update quiz completion message
        quizViewModel.correctAnswersCount.observe(viewLifecycleOwner, Observer { correctCount ->
            val totalCount = quizViewModel.totalQuestionsCount.value ?: 0

            // Create message based on the current language
            updateQuizCompletionMessage(correctCount, totalCount)
        })


        // Observe results from QuizViewModel
        quizViewModel.results.observe(viewLifecycleOwner) { results ->
            updateResultsAdapter(sharedViewModel.currentLanguage.value ?: "English", results)
        }


        // Initially hide the answer container
        binding.answerContainer.visibility = View.GONE
        // Initially hide the feedback text
        binding.feedbackText.visibility = View.GONE
        // Hide the next button
        binding.nextButton.visibility = View.GONE

        quizViewModel.currentQuestion.observe(viewLifecycleOwner, Observer { question ->
            if (question != null) {
                displayQuestion(question) // Pass the DocumentSnapshot directly
            } else {
                Log.d("QuizFragment", "Current question is null")
            }
        })


        // Button click listeners for Yes, No and Next buttons
        binding.yesButton.setOnClickListener {
            disableAnswerButtons()
            quizViewModel.answerQuestion(true)
            binding.answerContainer.visibility = View.VISIBLE // Show the answer container
            binding.nextButton.visibility = View.VISIBLE // Show the next button
            Log.d("QuizFragment", "Yes button clicked")
        }
        binding.noButton.setOnClickListener {
            disableAnswerButtons()
            quizViewModel.answerQuestion(false)
            binding.answerContainer.visibility = View.VISIBLE // Show the answer container
            binding.nextButton.visibility = View.VISIBLE // Show the next button
            Log.d("QuizFragment", "No button clicked")
        }
        binding.nextButton.setOnClickListener {
            quizViewModel.goToNextQuestion()
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

            // Optionally, scroll to the top of the RecyclerView
            binding.resultsRecyclerView.smoothScrollToPosition(0)

            if (quizViewModel.latestCorrectCount == quizViewModel.totalQuestionsCount.value) {
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
        quizViewModel.isQuizCompleted.observe(viewLifecycleOwner, Observer { completed ->
            if (completed) {
                displayResults()
            }
        })

        // Load questions from the database
        quizViewModel.loadQuestions()
    }

    // Helper function to update quiz completion message
    private fun updateQuizCompletionMessage(correctCount: Int, totalCount: Int) {
        val message = if (sharedViewModel.currentLanguage.value == "Māori") {
            "Kua oti te whakamātautau!\n\nI whakautu koe\n $correctCount o $totalCount pātai i te tika."
        } else {
            "Quiz complete!\n\nYou answered\n $correctCount of $totalCount questions correctly."
        }

        lastQuizCompletionMessage = message // Store the last message
        binding.resultsMessageText.text = message // Update the message on the screen
    }

    // Function to update the results adapter
    private fun updateResultsAdapter(language: String, results: List<Pair<DocumentSnapshot, Boolean>>) {
        resultsAdapter = ResultsAdapter(results, language)
        binding.resultsRecyclerView.adapter = resultsAdapter
        resultsAdapter.notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    // Helper function to disable yes no buttons
    private fun disableAnswerButtons() {
        binding.yesButton.isEnabled = false
        binding.noButton.isEnabled = false
    }

    // Helper function to enable yes no buttons
    private fun enableAnswerButtons() {
        binding.yesButton.isEnabled = true
        binding.noButton.isEnabled = true
    }

    // Display the current question and possible answers
    private fun displayQuestion(question: DocumentSnapshot?) {
        question?.let {
            // Get the language-specific title and content using helper methods from QuizViewModel
            val title = quizViewModel.getQuestionTitle(it) ?: "Question not available"
            val content = quizViewModel.getQuestionContent(it) ?: "Content not available"

            // Set the title and content to the UI elements
            binding.questionText.text = title
            binding.answerContentText.text = content

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

        // Get the latest correct count and total questions count
        val latestCorrectCount = quizViewModel.latestCorrectCount
        val totalQuestionsCount = quizViewModel.totalQuestionsCount.value

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
            quizViewModel.resetQuiz() // Call reset function in ViewModel
            resetUIForNewQuiz() // Reset UI elements to initial state
            binding.circularProgressBarContainer.visibility = View.VISIBLE
            binding.seeQuestionsButton.visibility = View.VISIBLE
            binding.findOutMoreButton.visibility = View.GONE
            binding.resultsRecyclerView.visibility = View.GONE
        }

    }

    // Helper function to reset UI elements for a new quiz
    private fun resetUIForNewQuiz() {
        // Reset visibility of quiz UI elements
        binding.resultsContainer.visibility = View.GONE // Hide results
        binding.yesButton.visibility = View.VISIBLE
        binding.noButton.visibility = View.VISIBLE
        binding.questionText.visibility = View.VISIBLE

        // Reset the progress bar
        for (i in 0 until binding.progressBar.childCount) {
            val view = binding.progressBar.getChildAt(i)
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.inactive_color)) // Reset to inactive color
        }

    }

    // Function to update the progress bar after each question is answered
    private fun updateProgressBar(currentQuestionIndex: Int) {
        for (i in 0 until binding.progressBar.childCount) {
            val view = binding.progressBar.getChildAt(i)
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.inactive_color)) // Inactive color

            if (i <= currentQuestionIndex) {
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.question_color)) // Active color
            }
        }
    }

    // Function to display feedback
    private fun displayFeedback(isCorrect: Boolean) {
        val currentLanguage = sharedViewModel.currentLanguage.value ?: "English"
        val feedbackMessage = when {
            isCorrect && currentLanguage == "Māori" -> "Tika!" // Correct in Māori
            isCorrect -> "Correct!" // Correct in English
            !isCorrect && currentLanguage == "Māori" -> "Hē!" // Incorrect in Māori
            else -> "Incorrect" // Incorrect in English
        }

        binding.feedbackText.text = feedbackMessage // Set the feedback message
        binding.feedbackText.visibility = View.VISIBLE // Make sure the feedback text is visible
        Log.d("QuizFragment", "Feedback displayed: $feedbackMessage") // Log the feedback

        // Update the progress bar
        val currentQuestionIndex = quizViewModel.currentQuestionIndex.value ?: 0
        updateProgressBar(currentQuestionIndex)
    }

    // Function to navigate to the Information Fragment to view quiz questions and answers
    private fun navigateToInformationFragment() {
        val incorrectQuestions = quizViewModel.results.value?.filter { !it.second }?.map { it.first }

        // If there are incorrect questions, navigate to InformationFragment
        if (!incorrectQuestions.isNullOrEmpty()) {
            val bundle = Bundle().apply {
                putStringArrayList("documentIds", ArrayList(incorrectQuestions.map { it.id }))
            }
            // Navigate to InformationFragment with the bundle
            findNavController().navigate(R.id.action_quizFragment_to_informationFragment, bundle)
        }
    }

    override fun onResume() {
        super.onResume()
        // Show the back arrow when navigating to the Information fragment
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    // Clear the binding reference to avoid memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Set binding to null
    }

    // Function to change language on buttons
    private fun updateStrings(language: String) {
        val context = requireContext()
        val isMaori = language == "Māori"

        binding.yesButton.text = context.getString(
            context.resources.getIdentifier(
                if (isMaori) "yesButton_mr" else "yesButton",
                "string",
                context.packageName
            )
        )

        binding.noButton.text = context.getString(
            context.resources.getIdentifier(
                if (isMaori) "noButton_mr" else "noButton",
                "string",
                context.packageName
            )
        )

        binding.nextButton.text = context.getString(
            context.resources.getIdentifier(
                if (isMaori) "nextButton_mr" else "nextButton",
                "string",
                context.packageName
            )
        )

        binding.seeQuestionsButton.text = context.getString(
            context.resources.getIdentifier(
                if (isMaori) "seeQuestionsButton_mr" else "seeQuestionsButton",
                "string",
                context.packageName
            )
        )

        binding.findOutMoreButton.text = context.getString(
            context.resources.getIdentifier(
                if (isMaori) "findOutMoreButton_mr" else "findOutMoreButton",
                "string",
                context.packageName
            )
        )

        binding.retryQuizButton.text = context.getString(
            context.resources.getIdentifier(
                if (isMaori) "retryQuizButton_mr" else "retryQuizButton",
                "string",
                context.packageName
            )
        )
    }
}
