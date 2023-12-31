package com.example.pptcomments.commentS

data class Comment(
    val id: String, // 评论的唯一标识符
    val userId: String?, // 用户ID，对于匿名评论可以为null
    val username: String?, // 用户名，对于匿名评论可以为null
    val content: String, // 评论内容
    val pptId: String, // 关联的PPT ID
    val pageNumber: Int, // 评论的PPT页面号
    val timestamp: Long // 评论的时间戳
)