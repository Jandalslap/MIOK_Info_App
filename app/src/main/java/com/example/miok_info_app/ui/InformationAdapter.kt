package com.example.miok_info_app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.miok_info_app.data.Document
import com.example.miok_info_app.R

// Adapter class for displaying a list of Document items in a RecyclerView
class InformationAdapter(private val documents: List<Document>) :
    RecyclerView.Adapter<InformationAdapter.InformationViewHolder>() {

    // ViewHolder class for holding document item views
    class InformationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView) // Title TextView
        val contentTextView: TextView = view.findViewById(R.id.contentTextView) // Content TextView
    }

    // Creates new ViewHolder instances for the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformationViewHolder {
        // Inflate the item layout for each document
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document, parent, false) // Use the layout you will create for each item
        return InformationViewHolder(view) // Return a new instance of InformationViewHolder
    }

    // Binds data to the ViewHolder for a specific position
    override fun onBindViewHolder(holder: InformationViewHolder, position: Int) {
        val document = documents[position] // Get the document at the current position
        holder.titleTextView.text = document.title // Set the title text
        holder.contentTextView.text = document.content // Set the content text
    }

    // Return the total number of documents in the list
    override fun getItemCount(): Int {
        return documents.size // Return the size of the documents list
    }
}
