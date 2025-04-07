package com.fugisawa.noteservice

import com.fugisawa.grpc.noteservice.Attachment
import com.fugisawa.grpc.noteservice.NoteServiceGrpcKt
import com.fugisawa.grpc.noteservice.NoteStatus
import com.fugisawa.grpc.noteservice.attachment
import com.fugisawa.grpc.noteservice.author
import com.fugisawa.grpc.noteservice.createNoteRequest
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val channel = ManagedChannelBuilder
        .forAddress("localhost", 50051)
        .usePlaintext()
        .build()

    val stub = NoteServiceGrpcKt.NoteServiceCoroutineStub(channel)

    val note = stub.createNote(
        createNoteRequest {
            title = "gRPC DSL Composition"
            content = "In this note we explore how to nest messages and use Kotlin DSL builders."

            tags += listOf("grpc", "kotlin", "composition")
            status = NoteStatus.ACTIVE

            metadata["source"] = "example"
            metadata["complexity"] = "moderate"

            attachment = attachment {
                url = "https://example.com/nested"
            }

            author = author {
                id = "user-42"
                name = "Lucas Fugisawa"
                email = "lucas@fugisawa.com"
            }
        }
    )

    println("Note created:")
    println("  ID: ${note.id}")
    println("  Title: ${note.title}")
    println("  Status: ${note.status}")
    if (note.hasContent()) {
        println("  Content: ${note.content}")
    } else {
        println("  Content: <unset>")
    }
    println("  Tags: ${note.tagsList}")
    println("  Metadata: ${note.metadataMap}")
    println("  Author: ${note.author.name} (${note.author.email})")
    println("  Created at: ${note.timestamps.createdAt}")
    println("  Updated at: ${note.timestamps.updatedAt}")
    when (note.attachment.sourceCase) {
        Attachment.SourceCase.URL -> println("  Attachment (URL): ${note.attachment.url}")
        Attachment.SourceCase.FILE_PATH -> println("  Attachment (File): ${note.attachment.filePath}")
        Attachment.SourceCase.SOURCE_NOT_SET -> println("  Attachment: <none>")
    }
    println("  Word count: ${note.metadataDetails.wordCount.value}")
}
