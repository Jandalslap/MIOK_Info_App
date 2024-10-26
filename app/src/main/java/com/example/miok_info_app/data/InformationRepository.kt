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

    // Function to get all quiz questions (documents with IDs like "quiz_question_1", "quiz_question_2", etc.)
    suspend fun getQuizQuestions(): List<DocumentSnapshot> {
        return try {
            // Query for documents starting with "quiz_question_"
            val querySnapshot = collectionRef
                .whereGreaterThanOrEqualTo("__name__", "quiz_question_")
                .whereLessThanOrEqualTo("__name__", "quiz_question_\uf8ff") // Unicode trick for string range
                .get()
                .await()
            querySnapshot.documents
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

}
