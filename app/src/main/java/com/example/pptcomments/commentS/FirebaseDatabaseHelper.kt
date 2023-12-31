package com.example.pptcomments.commentS
import com.example.pptcomments.commentS.Comment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseDatabaseHelper {

    private val database = FirebaseDatabase.getInstance()
    private val commentsRef = database.getReference("comments")

    fun postComment(comment: Comment) {
        commentsRef.push().setValue(comment)
    }

    fun getCommentsForPPT(pptId: String, callback: (List<Comment>) -> Unit) {
        commentsRef.orderByChild("pptId").equalTo(pptId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val comments = dataSnapshot.children.mapNotNull { it.getValue(Comment::class.java) }
                    callback(comments)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors.
                }
            })
    }
}