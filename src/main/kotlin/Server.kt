package com.fugisawa.noteservice

import io.grpc.Server
import io.grpc.ServerBuilder

fun main() {
    val server: Server = ServerBuilder
        .forPort(50051)
        .addService(NoteServiceImpl())
        .build()

    server.start()
    println("Server started on port 50051")
    server.awaitTermination()
}
