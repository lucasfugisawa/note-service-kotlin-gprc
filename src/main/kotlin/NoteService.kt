package com.fugisawa.noteservice

import com.fugisawa.grpc.noteservice.CreateNoteRequest
import com.fugisawa.grpc.noteservice.Note
import com.fugisawa.grpc.noteservice.NoteBatchSummary
import com.fugisawa.grpc.noteservice.NoteServiceGrpcKt
import com.fugisawa.grpc.noteservice.NoteTagFilter
import com.fugisawa.grpc.noteservice.note
import com.fugisawa.grpc.noteservice.noteBatchSummary
import com.fugisawa.grpc.noteservice.noteMetadata
import com.fugisawa.grpc.noteservice.timestamps
import com.google.protobuf.int32Value
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
            if (request.hasContent()) content = request.content
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
                this.wordCount = int32Value { value = wordCount }
            }
        }
    }

    override fun streamNotesByTag(request: NoteTagFilter): Flow<Note> = flow {
        try {
            val tag = request.tag
            var count = 1
            while (true) {
                emit(note {
                    title = "Note $count"
                    tags += tag
                })
                count++
                delay(1000)
            }
        } catch (e: Exception) {
            println("Streaming cancelled or failed: ${e.message}")
        }
    }

    override suspend fun createNotes(requests: Flow<Note>): NoteBatchSummary {
        var count = 0
        try {
            requests.collect { note ->
                println("Saving note: ${note.title}")
                count++
            }
        } catch (e: Exception) {
            println("Client closed stream or error: ${e.message}")
        }
        return noteBatchSummary { this.count = count }
    }

    override fun noteCollab(requests: Flow<Note>): Flow<Note> = flow {
        requests.collect { receivedNote ->
            println("Received: ${receivedNote.title} - ${receivedNote.content}")
            emit(note {
                title = "From server: ${receivedNote.title}"
                content = receivedNote.content
            })
        }
    }
}
