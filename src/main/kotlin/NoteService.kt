package com.fugisawa.noteservice

import com.fugisawa.grpc.noteservice.CreateNoteRequest
import com.fugisawa.grpc.noteservice.Note
import com.fugisawa.grpc.noteservice.NoteServiceGrpcKt
import com.fugisawa.grpc.noteservice.note
import java.util.*

class NoteServiceImpl : NoteServiceGrpcKt.NoteServiceCoroutineImplBase() {
    override suspend fun createNote(request: CreateNoteRequest): Note {
        // Here, you will implement your note creation logic, persist the new note etc.
        // Let's just generate dummy note, for this example:
        val newNoteId = UUID.randomUUID().toString()
        return note {
            id = newNoteId
            title = request.title
            content = request.content
        }
    }
}
