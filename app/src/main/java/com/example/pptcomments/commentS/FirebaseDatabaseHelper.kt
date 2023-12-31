package com.example.pptcomments.commentS
import com.google.firebase.database.*
import com.example.pptcomments.uploadAndShare.PPT
import com.example.pptcomments.commentS.Comment

class FirebaseDatabaseHelper {

    private val databaseReference = FirebaseDatabase.getInstance().reference

    fun loadPPT(pptId: String, onResult: (PPT?) -> Unit) {
        databaseReference.child("ppts").child(pptId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val ppt = dataSnapshot.getValue(PPT::class.java)
                    onResult(ppt)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors
                    onResult(null)
                }
            })
    }

    fun loadComments(pptId: String, onResult: (List<Comment>) -> Unit) {
        databaseReference.child("comments").orderByChild("pptId").equalTo(pptId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val comments = dataSnapshot.children.mapNotNull { it.getValue(Comment::class.java) }
                    onResult(comments)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors
                    onResult(emptyList())
                }
            })
    }

    fun postComment(comment: Comment, onComplete: (Boolean) -> Unit) {
        databaseReference.child("comments").push().setValue(comment)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    // Add other methods as needed
}