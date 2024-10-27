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
    private val results: List<Pair<DocumentSnapshot, Boolean>> // List of questions with result status
) : RecyclerView.Adapter<ResultsAdapter.ResultViewHolder>() {

    // Inflates the item view layout and returns the ViewHolder for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        return ResultViewHolder(view)
    }

    // Binds data to each item view based on its position in the list
    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val (question, isCorrect) = results[position] // Get question and result status
        holder.bind(question, isCorrect) // Bind data to the view holder
    }

    // Returns the total number of items in the data set
    override fun getItemCount(): Int = results.size

    // ViewHolder class to hold and bind data for each result item in the list
    inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.questionText) // Displays question text
        private val resultIcon: ImageView = itemView.findViewById(R.id.resultIcon) // Icon showing result status

        // Bind function to set the question text and result icon based on correctness
        fun bind(question: DocumentSnapshot, isCorrect: Boolean) {
            questionText.text = question.getString("title") // Set question title
            resultIcon.setImageResource(if (isCorrect) R.drawable.ic_quiz_correct else R.drawable.ic_quiz_incorrect) // Show correct/incorrect icon
        }
    }
}
