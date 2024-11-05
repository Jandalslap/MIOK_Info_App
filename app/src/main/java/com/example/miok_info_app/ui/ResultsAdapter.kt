import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.miok_info_app.R
import com.google.firebase.firestore.DocumentSnapshot

// Adapter for displaying quiz results in a RecyclerView
class ResultsAdapter(
    private val results: List<Pair<DocumentSnapshot, Boolean>>, // List of questions with result status (correct/incorrect)
    private val language: String // Language for displaying results
) : RecyclerView.Adapter<ResultsAdapter.ResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        // Inflate the item layout for the RecyclerView
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        return ResultViewHolder(view) // Return a new ViewHolder instance
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val (question, isCorrect) = results[position] // Get the question and its correctness status
        holder.bind(question, isCorrect, language) // Bind the data to the ViewHolder
    }

    override fun getItemCount(): Int = results.size // Return the total number of items in the list

    // ViewHolder class to hold the view elements for each result item
    inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.questionText) // TextView for displaying the question
        private val resultIcon: ImageView = itemView.findViewById(R.id.resultIcon) // ImageView for displaying the result icon (correct/incorrect)

        // Function to bind the question and result data to the view
        fun bind(question: DocumentSnapshot, isCorrect: Boolean, language: String) {
            // Get the title based on the selected language
            val titleField = if (language == "Māori") "title_mr" else "title"
            questionText.text = question.getString(titleField) ?: "No Title" // Set question title, defaulting to "No Title" if null

            // Set the appropriate icon based on whether the answer was correct or incorrect
            resultIcon.setImageResource(if (isCorrect) R.drawable.ic_quiz_correct else R.drawable.ic_quiz_incorrect)
        }
    }
}
