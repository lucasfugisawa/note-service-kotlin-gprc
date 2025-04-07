package com.fugisawa.noteservice

import com.fugisawa.grpc.noteservice.NoteServiceGrpcKt
import com.fugisawa.grpc.noteservice.NoteStatus
import com.fugisawa.grpc.noteservice.author
import com.fugisawa.grpc.noteservice.createNoteRequest
import com.fugisawa.grpc.noteservice.note
import com.fugisawa.grpc.noteservice.noteTagFilter
import io.grpc.ManagedChannelBuilder
import io.grpc.Status
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

fun main() = runBlocking {
    val channel = ManagedChannelBuilder
        .forAddress("localhost", 50051)
        .usePlaintext()
        .build()

    val stub = NoteServiceGrpcKt.NoteServiceCoroutineStub(channel)

    println("== CreateNote with Deadline ==")
    val stubWithDeadline = stub.withDeadlineAfter(3, TimeUnit.SECONDS)
    try {
        val note = stubWithDeadline.createNote(
            createNoteRequest {
                title = "gRPC + Kotlin"
                content = "Deadlines, streams and structured errors."
                tags += listOf("grpc", "deadline")
                status = NoteStatus.ACTIVE
                author = author {
                    id = "123"
                    name = "Lucas"
                    email = "lucas@fugisawa.com"
                }
            }
        )
        println("Created: ${note.title}")
    } catch (e: StatusRuntimeException) {
        if (e.status.code == Status.Code.DEADLINE_EXCEEDED) {
            println("Request timed out!")
        } else {
            println("Other error: ${e.status}")
        }
    }

    println("\n== Server Streaming ==")
    val tagFilter = noteTagFilter { tag = "kotlin" }
    val streamFlow = stub.streamNotesByTag(tagFilter)

    val job = launch {
        streamFlow.take(3).collect { note ->
            println("Received: ${note.title}")
        }
    }
    job.join()

    println("\n== Client Streaming ==")
    val notesFlow = flow {
        emit(note { title = "Streamed A" })
        emit(note { title = "Streamed B" })
    }
    val summary = stub.createNotes(notesFlow)
    println("Server stored ${summary.count} notes")

    println("\n== Bidirectional Streaming ==")
    val chatStream = flow {
        emit(note { title = "Live 1"; content = "Hey!" })
        emit(note { title = "Live 2"; content = "Still there?" })
    }
    val responses = stub.noteCollab(chatStream)
    responses.collect { note ->
        println("Response: ${note.title} - ${note.content}")
    }
}
