package com.example.pptcomments.commentS

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.example.pptcomments.R

class PPTViewerActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ppt_viewer)

        webView = findViewById(R.id.webView)
        val pptLink = intent.getStringExtra("PPT_LINK")
        if (pptLink != null) {
            webView.loadUrl(pptLink)
        }
    }
}