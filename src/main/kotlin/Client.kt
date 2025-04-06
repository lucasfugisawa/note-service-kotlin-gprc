package com.fugisawa.noteservice

import com.fugisawa.grpc.noteservice.Attachment
import com.fugisawa.grpc.noteservice.NoteServiceGrpcKt.NoteServiceCoroutineStub
import com.fugisawa.grpc.noteservice.NoteStatus
import com.fugisawa.grpc.noteservice.attachment
import com.fugisawa.grpc.noteservice.createNoteRequest
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val channel = ManagedChannelBuilder
        .forAddress("localhost", 50051)
        .usePlaintext()
        .build()

    val stub = NoteServiceCoroutineStub(channel)

    val note = stub.createNote(
        createNoteRequest {
            title = "gRPC Article"
            content = "This article introduces several Protobuf concepts in Kotlin."

            tags += listOf("grpc", "kotlin", "protobuf")

            status = NoteStatus.ACTIVE

            metadata["author"] = "Lucas Fugisawa"
            metadata["level"] = "intermediate"

            attachment = attachment {
                url = "https://example.com/attachment"
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
    when (note.attachment.sourceCase) {
        Attachment.SourceCase.URL -> println("  Attachment (URL): ${note.attachment.url}")
        Attachment.SourceCase.FILE_PATH -> println("  Attachment (File): ${note.attachment.filePath}")
        Attachment.SourceCase.SOURCE_NOT_SET -> println("  Attachment: <none>")
    }
    println("  Word count: ${note.metadataDetails.wordCount.value}")
}
