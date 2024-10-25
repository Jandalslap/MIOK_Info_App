package com.example.miok_info_app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class InformationRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("information")

    // Function to get a single document by ID
    suspend fun getDocumentById(documentId: String): DocumentSnapshot? {
        return try {
            collectionRef.document(documentId).get().await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Function to get all documents in the collection
    suspend fun getAllDocuments(): QuerySnapshot? {
        return try {
            collectionRef.get().await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
