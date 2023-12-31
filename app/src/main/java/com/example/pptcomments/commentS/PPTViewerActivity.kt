package com.example.pptcomments.commentS

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.pptcomments.commentS.Comment
import com.example.pptcomments.commentS.PPTViewerScreen
import com.example.pptcomments.uploadAndShare.PPT

class PPTViewerActivity : ComponentActivity() {

    private val databaseHelper = FirebaseDatabaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 假设有一种方式来获取当前PPT的ID，例如通过Intent
        val pptId = intent.getStringExtra("PPT_ID") ?: return

        setContent {
            var ppt by remember { mutableStateOf<PPT?>(null) }
            var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }

            LaunchedEffect(pptId) {
                databaseHelper.loadPPT(pptId) { loadedPpt ->
                    ppt = loadedPpt
                }
                databaseHelper.loadComments(pptId) { loadedComments ->
                    comments = loadedComments
                }
            }

            ppt?.let {
                PPTViewerScreen(pptUrl = it.link, comments = comments)
            }
        }
    }
}