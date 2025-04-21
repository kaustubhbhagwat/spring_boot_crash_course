package com.kaustubh.spring_boot_crash_course.database.repository

import com.kaustubh.spring_boot_crash_course.database.model.RefreshToken
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface RefreshTokenRepository: MongoRepository<RefreshToken, ObjectId> {


    fun findByUSerIdAndHashedToken(userId: ObjectId, hashedToken: String): RefreshToken?
    fun deleteByUSerIdAndHashedToken(userId: ObjectId,hashedToken: String)

}