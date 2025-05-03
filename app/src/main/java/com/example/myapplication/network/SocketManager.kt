package com.example.myapplication.network

import android.util.Log
import com.example.myapplication.di.AppDispatchers
import com.google.gson.Gson
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.net.Socket
import java.nio.ByteBuffer
import javax.inject.Inject

private const val TAG = "SocketManager"
private const val address = "challenge.ciliz.com"
private const val port = 2222

class SocketManager @Inject constructor(
    val coroutineDispatchers: AppDispatchers
) {

    private var socket: Socket? = null

    suspend fun connect() = withContext(coroutineDispatchers.ioDispatcher) {
        socket = Socket(address, port)
        Log.d(TAG, "connected: ${socket?.isConnected}")
    }

    suspend fun send(request: TestRequest) = withContext(coroutineDispatchers.ioDispatcher) {

        val gson = Gson()
        val json = gson.toJson(request)

        Log.i(TAG, "sending: $json")

        val messageBytes = json.toByteArray()
        val lengthBytes = ByteBuffer.allocate(4).putInt(messageBytes.size).array()

        val outputStream = socket?.getOutputStream()
        outputStream?.write(lengthBytes)
        outputStream?.write(messageBytes)
        outputStream?.flush()
    }

    suspend fun receive(): TestResponse = withContext(coroutineDispatchers.ioDispatcher) {
        val inputStream = DataInputStream(socket?.getInputStream())

        val lengthBytes = ByteArray(4)
        inputStream.readFully(lengthBytes)
        val length = ByteBuffer.wrap(lengthBytes).int

        val buffer = ByteArray(length)
        inputStream.readFully(buffer)
        val message = String(buffer, 0, length)
        Log.d(TAG, "received: $message")
        val gson = Gson()
        gson.fromJson<TestResponse>(message, TestResponse::class.java)
    }

    suspend fun close() = withContext(coroutineDispatchers.ioDispatcher) {
        socket?.close()
        socket = null
    }
}
