package com.example.miok_info_app.ui

import ResultsAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        // Set up the results RecyclerView
        resultsAdapter = ResultsAdapter(emptyList())
        binding.resultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.resultsRecyclerView.adapter = resultsAdapter

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
            viewModel.answerQuestion(true)
            Log.d("QuizFragment", "Yes button clicked")
        }
        binding.noButton.setOnClickListener {
            viewModel.answerQuestion(false)
            Log.d("QuizFragment", "No button clicked")
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

    private fun displayQuestion(question: DocumentSnapshot?) {
        question?.let {
            binding.questionText.text = it.getString("title") ?: "Question not available"
        }
    }

    private fun displayResults() {
        binding.yesButton.visibility = View.GONE // Hide the answer buttons
        binding.noButton.visibility = View.GONE
        binding.questionText.visibility = View.GONE // Hide the question
        binding.resultsContainer.visibility = View.VISIBLE // Show results container

        // Update results in the adapter
        resultsAdapter = ResultsAdapter(viewModel.results.value ?: emptyList())
        binding.resultsRecyclerView.adapter = resultsAdapter

        // Show the "Find Out More" button
        binding.findOutMoreButton.visibility = View.VISIBLE

        // Set up click listener for "Find Out More" button
        binding.findOutMoreButton.setOnClickListener {
            navigateToInformationFragment()
        }
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
