import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.miok_info_app.R
import com.google.firebase.firestore.DocumentSnapshot

class ResultsAdapter(
    private val results: List<Pair<DocumentSnapshot, Boolean>>
) : RecyclerView.Adapter<ResultsAdapter.ResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val (question, isCorrect) = results[position]
        holder.bind(question, isCorrect)
    }

    override fun getItemCount(): Int = results.size

    inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.questionText)
        private val resultIcon: ImageView = itemView.findViewById(R.id.resultIcon)

        fun bind(question: DocumentSnapshot, isCorrect: Boolean) {
            questionText.text = question.getString("title")
            resultIcon.setImageResource(if (isCorrect) R.drawable.ic_quiz_correct else R.drawable.ic_quiz_incorrect)
        }
    }
}
