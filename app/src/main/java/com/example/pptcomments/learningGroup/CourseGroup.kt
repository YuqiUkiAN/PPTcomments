package com.example.pptcomments.learningGroup

import java.time.Instant

data class CourseGroup(
    val id: String,
    val name: String,
    val description: String,
    val creator: String,
    val members: List<String>,
    val lastAccessedTime: Instant = Instant.now()
)
