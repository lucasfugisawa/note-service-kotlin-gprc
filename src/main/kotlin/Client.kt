package com.fugisawa.noteservice

import com.fugisawa.grpc.noteservice.NoteServiceGrpcKt.NoteServiceCoroutineStub
import com.fugisawa.grpc.noteservice.createNoteRequest
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val channel =
        ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .build()

    val stub = NoteServiceCoroutineStub(channel)

    val request = createNoteRequest {
        title = "My first note"
        content = "This is my first note created with gRPC!"
    }

    val note = stub.createNote(request)

    println("Note created: ${note.id} - ${note.title}")
}
