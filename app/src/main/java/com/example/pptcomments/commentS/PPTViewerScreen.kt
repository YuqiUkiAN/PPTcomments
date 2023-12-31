package com.example.pptcomments.commentS

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import android.webkit.WebView
import android.webkit.WebViewClient

@Composable
fun PPTViewerScreen(pptUrl: String, comments: List<Comment>) {
    Column(modifier = Modifier.fillMaxSize()) {
        PPTWebView(pptUrl = pptUrl, modifier = Modifier.weight(1f))
        CommentList(comments = comments, modifier = Modifier.weight(1f))
    }
}

@Composable
fun PPTWebView(pptUrl: String, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                loadUrl(pptUrl)
            }
        },
        modifier = modifier
    )
}

@Composable
fun CommentList(comments: List<Comment>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(comments) { comment ->
            CommentItem(comment = comment)
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()) {
            Text(text = "Page: ${comment.pageNumber}", style = MaterialTheme.typography.subtitle1)
            Text(text = "User: ${comment.username ?: "Anonymous"}", style = MaterialTheme.typography.subtitle2)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = comment.content, style = MaterialTheme.typography.body1)
        }
    }
}