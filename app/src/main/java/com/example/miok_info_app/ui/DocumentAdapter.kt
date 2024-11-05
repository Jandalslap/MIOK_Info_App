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

    // Creates new ViewHolder instances for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val inflater = LayoutInflater.from(parent.context) // Inflate the layout for each item
        val view = inflater.inflate(R.layout.item_document, parent, false)
        return DocumentViewHolder(view) // Return a new ViewHolder instance
    }

    // Binds the data to the ViewHolder
    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = getItem(position) // Get the document at the specified position
        holder.bind(document) // Bind the document to the ViewHolder
    }

    // ViewHolder class to hold the views for each document item
    class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView) // TextView for the document title
        private val contentTextView: TextView = itemView.findViewById(R.id.contentTextView) // TextView for the document content

        // Binds the document data to the views
        fun bind(document: Document) {
            titleTextView.text = document.title // Set the title of the document
            contentTextView.text = formatContent(document.content) // Format and set the content

            // Enable scrolling for the content TextView and handle long text
            contentTextView.movementMethod = ScrollingMovementMethod()

            // Enable link detection for web URLs in the content
            Linkify.addLinks(contentTextView, Linkify.WEB_URLS)
        }

        // Function to format the content for line breaks
        private fun formatContent(content: String): String {
            return content.replace("\\n".toRegex(), "\n") // Replace escaped newline with actual newline
        }
    }

    // DiffCallback class for efficiently updating the RecyclerView
    class DocumentDiffCallback : DiffUtil.ItemCallback<Document>() {
        // Checks if two items represent the same document
        override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
            return oldItem.title == newItem.title // Compare titles to determine if they are the same
        }

        // Checks if the contents of two items are the same
        override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
            return oldItem == newItem // Compare the entire document for equality
        }
    }
}
