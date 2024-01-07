package com.example.pptcomments.uploadAndShare

data class PPT(
    val id: String = "", // PPT 的唯一标识符
    val link: String = "", // PPT 链接
    val uploaderId: String = "", // 上传者的用户 ID
    val groupId: String? = null, // 关联的小组 ID，如果不是小组分享则为 null
    val uploadedAt: Long = System.currentTimeMillis() // 上传时间戳
)