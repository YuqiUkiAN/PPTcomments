package com.example.pptcomments.commentS

import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pptcomments.R
import com.example.pptcomments.commentS.CommentsAdapter
import com.example.pptcomments.commentS.Comment
import com.example.pptcomments.commentS.FirebaseDatabaseHelper
import com.google.firebase.database.FirebaseDatabase

class PPTViewerActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentInput: EditText
    private lateinit var sendButton: Button
    private var commentsAdapter: CommentsAdapter? = null
    private val databaseHelper = FirebaseDatabaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ppt_viewer)

        webView = findViewById(R.id.webView)
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView)
        commentInput = findViewById(R.id.commentInput)
        sendButton = findViewById(R.id.sendButton)

        // 假设你已经有一个PPT链接
        val pptLink = "http://example.com/ppt" // 你需要根据实际情况获取PPT链接
        webView.loadUrl(pptLink)

        setupCommentsRecyclerView()
        setupSendButton()
    }

    private fun setupCommentsRecyclerView() {
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)

        // 初始化适配器，初始为空列表
        commentsAdapter = CommentsAdapter(listOf())
        commentsRecyclerView.adapter = commentsAdapter

        // 假设pptId是你从Intent或其他地方获取的
        val pptId = "some_ppt_id"
        databaseHelper.getCommentsForPPT(pptId) { comments ->
            commentsAdapter?.let {
                it.comments = comments
                it.notifyDataSetChanged()
            }
        }
    }

    private fun setupSendButton() {
        sendButton.setOnClickListener {
            val commentText = commentInput.text.toString()
            if (commentText.isNotEmpty()) {
                // 创建Comment对象并上传
                val newComment = Comment(
                    id = FirebaseDatabase.getInstance().getReference("comments").push().key ?: "",
                    userId = null,  // 从用户认证系统获取
                    username = null, // 从用户认证系统获取
                    content = commentText,
                    pptId = "some_ppt_id", // 从Intent或其他地方获取
                    pageNumber = 1, // 如果需要，从WebView或其他逻辑获取
                    timestamp = System.currentTimeMillis()
                )
                databaseHelper.postComment(newComment)

                // 清空输入框
                commentInput.setText("")
            } else {
                Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show()
            }
        }
    }
}