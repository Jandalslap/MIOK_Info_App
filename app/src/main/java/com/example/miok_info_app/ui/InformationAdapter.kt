package com.example.miok_info_app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.miok_info_app.data.Document
import com.example.miok_info_app.R

class InformationAdapter(private val documents: List<Document>) :
    RecyclerView.Adapter<InformationAdapter.InformationViewHolder>() {

    class InformationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val contentTextView: TextView = view.findViewById(R.id.contentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document, parent, false) // Use the layout you will create for each item
        return InformationViewHolder(view)
    }

    override fun onBindViewHolder(holder: InformationViewHolder, position: Int) {
        val document = documents[position]
        holder.titleTextView.text = document.title
        holder.contentTextView.text = document.content
    }

    override fun getItemCount(): Int {
        return documents.size
    }
}
