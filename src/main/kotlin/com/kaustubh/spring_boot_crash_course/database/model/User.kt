package com.kaustubh.spring_boot_crash_course.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

data class User(
    @Id val id: ObjectId,
    val userName: String,
    val email: String
)
