package com.example.pptcomments.commentS

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pptcomments.commentS.Comment
import com.example.pptcomments.uploadAndShare.PPT
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PPTViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
//    var ppt: PPT? = null
//    var comments: List<Comment> = emptyList()
    private val _ppt = MutableStateFlow<PPT?>(null)
    val ppt: StateFlow<PPT?> = _ppt

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    fun loadPPTAndComments(pptId: String) {
        viewModelScope.launch {
            // 加载 PPT 数据
            firestore.collection("ppts").document(pptId).get().addOnSuccessListener { documentSnapshot ->
                _ppt.value = documentSnapshot.toObject(PPT::class.java)?.also {
                    Log.d("PPTViewModel", "Loaded PPT: ${it.link}")
                }
            }.addOnFailureListener {
                Log.e("PPTViewModel", "Error loading PPT: ${it.message}")
            }

            // 加载评论数据
            firestore.collection("comments")
                .whereEqualTo("pptId", pptId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    _comments.value = querySnapshot.documents.mapNotNull { it.toObject(Comment::class.java) }
                        .also {
                            Log.d("PPTViewModel", "Loaded ${it.size} comments")
                        }
                }.addOnFailureListener {
                    Log.e("PPTViewModel", "Error loading comments: ${it.message}")
                }
        }
    }

    fun postComment(commentText: String, pptId: String, isAnonymous: Boolean) {
        // 检查用户是否登录
        val user = Firebase.auth.currentUser
        if (user == null) {
            Log.e("PPTViewModel", "User must be logged in to post a comment.")
            return
        }

        // 创建评论对象
        val newComment = Comment(
            id = "", // Firestore will generate the ID
            userId = if (isAnonymous) null else user.uid,
            //username = if (isAnonymous) "Anonymous" else user.displayName ?: "Unknown",
            content = commentText,
            pptId = pptId,
            pageNumber = 1, // Assume page number 1 for simplicity
            timestamp = System.currentTimeMillis()
        )

        // 提交评论到 Firestore
        viewModelScope.launch {
            try {
                firestore.collection("comments").add(newComment).await()
                Log.d("PPTViewModel", "Comment posted successfully.")
                loadPPTAndComments(pptId)
            } catch (e: Exception) {
                Log.e("PPTViewModel", "Error posting comment: ${e.message}")
            }
        }
    }
}