package com.example.miok_info_app.ui

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

    // Creates new ViewHolder instances for the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val inflater = LayoutInflater.from(parent.context) // Get LayoutInflater from the parent context
        val view = inflater.inflate(R.layout.item_document, parent, false) // Inflate the item layout
        return DocumentViewHolder(view) // Return a new instance of DocumentViewHolder
    }

    // Binds data to the ViewHolder for a specific position
    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = getItem(position) // Get the document at the current position
        holder.bind(document) // Bind the document data to the ViewHolder
    }

    // ViewHolder class for holding document item views
    class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView) // Title TextView
        private val contentTextView: TextView = itemView.findViewById(R.id.contentTextView) // Content TextView

        // Binds the document data to the views
        fun bind(document: Document) {
            titleTextView.text = document.title // Set the title text
            contentTextView.text = document.content // Set the content text
        }
    }

    // DiffUtil callback class for calculating differences between old and new lists
    class DocumentDiffCallback : DiffUtil.ItemCallback<Document>() {
        // Check if two items represent the same document
        override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
            return oldItem.title == newItem.title // Assuming title is unique for documents
        }

        // Check if the contents of two documents are the same
        override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
            return oldItem == newItem // Compare the entire document for equality
        }
    }
}
