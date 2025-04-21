package com.kaustubh.spring_boot_crash_course.controllers

import com.kaustubh.spring_boot_crash_course.controllers.NoteController.NoteResponse
import com.kaustubh.spring_boot_crash_course.database.model.Note
import com.kaustubh.spring_boot_crash_course.database.repository.NoteRepository
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

// GET http://localhost:8080/notes?ownerId=67f7c5a43a33864fb4dba611
// POST http://localhost:8080/notes
// DELETE http://localhost:8080/notes/123

@RestController
@RequestMapping("/notes")
class NoteController (private val noteRepository: NoteRepository){

    data class NoteRequest(
        val id: String?,
        val title: String,
        val content: String,
        val color: String
    )

    data class NoteResponse(
        val id: String,
        val title: String,
        val content: String,
        val color: String,
        val createdAt: Instant
    )

    @PostMapping
    fun save(@RequestBody body: NoteRequest): NoteResponse{
       val note =  noteRepository.save(
            Note(
                id = body.id?.let { ObjectId(it)} ?: ObjectId.get() ,
                title = body.title,
                content = body.content,
                color = body.color,
                createdAt = Instant.now(),
                ownerId = ObjectId()
            )
        )

        return note.toResponse()
    }

    @GetMapping
    fun findByOwnerId(): List<NoteResponse>{
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        return noteRepository.findByOwnerId(ObjectId(ownerId)).map {
            it.toResponse()
        }
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String){
         noteRepository.deleteById(ObjectId(id))
    }
}

private fun Note.toResponse(): NoteController.NoteResponse{
    return NoteResponse(
        id = id.toHexString(),
        title = title,
        content = content,
        createdAt = createdAt,
        color = color
    )
}