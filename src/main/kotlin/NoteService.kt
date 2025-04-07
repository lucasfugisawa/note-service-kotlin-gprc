package com.fugisawa.noteservice

import com.fugisawa.grpc.noteservice.CreateNoteRequest
import com.fugisawa.grpc.noteservice.Note
import com.fugisawa.grpc.noteservice.NoteServiceGrpcKt
import com.fugisawa.grpc.noteservice.note
import com.fugisawa.grpc.noteservice.noteMetadata
import com.fugisawa.grpc.noteservice.timestamps
import com.google.protobuf.int32Value
import java.time.LocalDateTime
import java.util.*

class NoteServiceImpl : NoteServiceGrpcKt.NoteServiceCoroutineImplBase() {
    override suspend fun createNote(request: CreateNoteRequest): Note {
        val newNoteId = UUID.randomUUID().toString()

        val now = LocalDateTime.now().toString()

        val wordCount = if (request.hasContent()) {
            request.content.split("\\s+".toRegex()).size
        } else {
            0
        }

        return note {
            id = newNoteId
            title = request.title

            if (request.hasContent()) {
                content = request.content
            }

            tags += request.tagsList
            status = request.status
            metadata.putAll(request.metadataMap)
            attachment = request.attachment
            author = request.author

            timestamps = timestamps {
                createdAt = now
                updatedAt = now
            }

            metadataDetails = noteMetadata {
                this.wordCount = int32Value { this.value = wordCount }
            }
        }
    }
}
