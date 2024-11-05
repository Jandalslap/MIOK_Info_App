package com.example.miok_info_app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

// Repository class for managing information-related data from Firestore
class InformationRepository {
    private val firestore = FirebaseFirestore.getInstance() // Instance of Firestore
    private val collectionRef = firestore.collection("information") // Reference to the "information" collection

    // Function to get a single document by its ID
    suspend fun getDocumentById(documentId: String): DocumentSnapshot? {
        return try {
            collectionRef.document(documentId).get().await() // Fetch the document and wait for the result
        } catch (e: Exception) {
            e.printStackTrace() // Print the stack trace in case of an exception
            null // Return null if there's an error
        }
    }

    // Function to get all documents in the collection
    suspend fun getAllDocuments(): QuerySnapshot? {
        return try {
            collectionRef.get().await() // Fetch all documents and wait for the result
        } catch (e: Exception) {
            e.printStackTrace() // Print the stack trace in case of an exception
            null // Return null if there's an error
        }
    }

    // Function to get all quiz questions (documents with IDs like "quiz_question_1", "quiz_question_2", etc.)
    suspend fun getQuizQuestions(): List<DocumentSnapshot> {
        return try {
            // Query for documents whose IDs start with "quiz_question_"
            val querySnapshot = collectionRef
                .whereGreaterThanOrEqualTo("__name__", "quiz_question_") // Start of the document ID
                .whereLessThanOrEqualTo("__name__", "quiz_question_\uf8ff") // End of the document ID using Unicode trick
                .get()
                .await() // Wait for the result
            querySnapshot.documents // Return the list of documents
        } catch (e: Exception) {
            e.printStackTrace() // Print the stack trace in case of an exception
            emptyList() // Return an empty list if there's an error
        }
    }


}
