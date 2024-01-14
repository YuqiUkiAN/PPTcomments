package com.example.pptcomments.commentS

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import com.example.pptcomments.commentS.Comment
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.navigation.NavController

@Composable
fun PPTViewerScreen(pptId: String, viewModel: PPTViewModel, navController: NavController) {
    // 状态
    var commentText by remember { mutableStateOf("") }
    var isAnonymous by remember { mutableStateOf(false) }
    var isPosting by remember { mutableStateOf(false) }

    LaunchedEffect(pptId) {
        viewModel.loadPPTAndComments(pptId)
    }

    val ppt by viewModel.ppt.collectAsState()
    val comments by viewModel.comments.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ppt?.name ?: "PPT Viewer") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                ppt?.let {
                    PPTWebView(pptUrl = it.link, modifier = Modifier.weight(1f))
                    CommentList(comments = comments, modifier = Modifier.weight(1f))

                    // 评论输入区域
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        TextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text("Enter your comment") },
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = isAnonymous,
                            onCheckedChange = { isAnonymous = it }
                        )
                    }

                    // 提交按钮
                    Button(
                        onClick = {
                            if (commentText.isNotEmpty()) {
                                viewModel.postComment(commentText, pptId, isAnonymous)
                                commentText = "" // Clear the text field after posting
                                isAnonymous = false // Reset the switch after posting
                            }
                        },
                        enabled = commentText.isNotEmpty() && !isPosting,
                        modifier = Modifier.align(Alignment.End).padding(8.dp)
                    ) {
                        Text("Post")
                    }
                } ?: Text("Loading PPT...", style = MaterialTheme.typography.subtitle1)
            }
        }
    )



//    LaunchedEffect(pptId) {
//        viewModel.loadPPTAndComments(pptId)
//    }
//
//    val ppt by viewModel.ppt.collectAsState()
//
//    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        Text("PPT ID: $pptId", style = MaterialTheme.typography.h6)
//
//        // 仅用于测试，显示 PPT 链接
//        ppt?.let {
//            Text("PPT 链接: ${it.link}", style = MaterialTheme.typography.subtitle1)
//        } ?: Text("正在加载 PPT...", style = MaterialTheme.typography.subtitle1)
//    }
}

@Composable
fun PPTWebView(pptUrl: String, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true  // Enable JavaScript
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        Log.d("WebView", "Page loading started: $url")
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.d("WebView", "Page loading finished: $url")
                    }

                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                        super.onReceivedError(view, request, error)
                        error?.let {
                            Log.e("WebView", "Error loading page: ${error.description}")
                        }
                    }
                }
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
            Text(
                text = "User: ${if (comment.userId.isNullOrEmpty()) "Anonymous" else comment.userId}",
                style = MaterialTheme.typography.subtitle2
            )
            //Text(text = "User: ${comment.username ?: "Anonymous"}", style = MaterialTheme.typography.subtitle2)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = comment.content, style = MaterialTheme.typography.body1)
        }
    }
}