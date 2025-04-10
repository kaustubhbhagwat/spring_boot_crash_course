package com.kaustubh.database.repository

import com.kaustubh.database.model.Note
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface NoteRepository : MongoRepository<Note, ObjectId>{
    fun findByOwnerId(ownerId: ObjectId): List<Note>
}

fun temp(repository: NoteRepository){
    repository.findAll()
}