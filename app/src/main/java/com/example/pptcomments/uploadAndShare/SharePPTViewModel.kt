package com.example.pptcomments.uploadAndShare

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SharePPTViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    fun sharePPT(link: String, groupId: String, onResult: (Boolean, String?) -> Unit) {
        if (link.isBlank() || groupId.isBlank()) {
            onResult(false, "Link and Group cannot be empty")
            return
        }

        val userId = auth.currentUser?.uid
        if (userId == null) {
            onResult(false, "User is not logged in")
            return
        }

        val newPPT = PPT(
            id = db.collection("ppts").document().id,
            link = link,
            uploaderId = userId,
            groupId = groupId,
            uploadedAt = System.currentTimeMillis()
        )

        db.collection("ppts").document(newPPT.id).set(newPPT)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.localizedMessage ?: "Failed to share PPT") }
    }
}