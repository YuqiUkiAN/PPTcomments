package com.example.pptcomments.commentS

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pptcomments.R
import com.example.pptcomments.commentS.Comment
import java.text.SimpleDateFormat
import java.util.*

class CommentsAdapter(var comments: List<Comment>) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val usernameTextView: TextView = view.findViewById(R.id.usernameTextView)
        private val contentTextView: TextView = view.findViewById(R.id.contentTextView)
        private val timestampTextView: TextView = view.findViewById(R.id.timestampTextView)

        fun bind(comment: Comment) {
            usernameTextView.text = comment.username ?: "Anonymous"
            contentTextView.text = comment.content
            timestampTextView.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(comment.timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount() = comments.size
}