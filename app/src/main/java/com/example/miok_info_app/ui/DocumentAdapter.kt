package com.example.miok_info_app.ui

import android.text.method.ScrollingMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.miok_info_app.data.Document
import com.example.miok_info_app.R

// Adapter class for displaying a list of Document items in a RecyclerView
class DocumentAdapter : ListAdapter<Document, DocumentAdapter.DocumentViewHolder>(DocumentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_document, parent, false)
        return DocumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = getItem(position)
        holder.bind(document)
    }

    class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)

        // Binds the document data to the views
        fun bind(document: Document) {
            titleTextView.text = document.title
            contentTextView.text = formatContent(document.content)

            // Enable scrolling and handle long formatted text
            contentTextView.movementMethod = ScrollingMovementMethod()

            // Enable link detection for the content
            Linkify.addLinks(contentTextView, Linkify.WEB_URLS)
        }

        // Function to format the content for line breaks
        private fun formatContent(content: String): String {
            return content.replace("\\n".toRegex(), "\n") // Replace escaped newline with actual newline
        }
    }

    class DocumentDiffCallback : DiffUtil.ItemCallback<Document>() {
        override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
            return oldItem == newItem
        }
    }
}
