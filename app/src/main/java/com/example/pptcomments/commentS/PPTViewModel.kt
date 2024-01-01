package com.example.pptcomments.commentS

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pptcomments.commentS.Comment
import com.example.pptcomments.uploadAndShare.PPT
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PPTViewModel : ViewModel() {
    private val databaseReference = FirebaseDatabase.getInstance().reference
    var ppt: PPT? = null
    var comments: List<Comment> = emptyList()

    fun loadPPTAndComments(pptId: String) {
        viewModelScope.launch {
            ppt = withContext(Dispatchers.IO) {
                val snapshot = databaseReference.child("ppts").child(pptId)
                    .get().await()
                snapshot.getValue(PPT::class.java)
            }
            comments = withContext(Dispatchers.IO) {
                val snapshot = databaseReference.child("comments").orderByChild("pptId").equalTo(pptId)
                    .get().await()
                snapshot.children.mapNotNull { it.getValue(Comment::class.java) }
            }
        }
    }

    fun postComment(comment: Comment) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseReference.child("comments").push().setValue(comment).await()
        }
    }
}