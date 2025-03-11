package com.hym.kmpbuffer

/**
 * @author hehua2008
 * @date 2025/3/6
 */
// Custom exceptions
actual typealias BufferUnderflowException = java.nio.BufferUnderflowException
actual typealias BufferOverflowException = java.nio.BufferOverflowException
actual typealias ReadOnlyBufferException = java.nio.ReadOnlyBufferException

// Enum for byte order (big-endian or little-endian)
actual typealias ByteOrder = java.nio.ByteOrder

actual val BigEndianOrder: ByteOrder
    get() = java.nio.ByteOrder.BIG_ENDIAN

actual val LittleEndianOrder: ByteOrder
    get() = java.nio.ByteOrder.LITTLE_ENDIAN

actual val NativeOrder: ByteOrder
    get() = java.nio.ByteOrder.nativeOrder()

actual typealias ByteBuffer = java.nio.ByteBuffer

actual fun ByteBuffer.position(newPosition: Int): ByteBuffer {
    return this.position(newPosition) as ByteBuffer
}

actual fun ByteBuffer.limit(newLimit: Int): ByteBuffer {
    return this.limit(newLimit) as ByteBuffer
}

actual fun ByteBuffer.mark(): ByteBuffer {
    return this.mark() as ByteBuffer
}

actual fun ByteBuffer.reset(): ByteBuffer {
    return this.reset() as ByteBuffer
}

actual fun ByteBuffer.clear(): ByteBuffer {
    return this.clear() as ByteBuffer
}

actual fun ByteBuffer.flip(): ByteBuffer {
    return this.flip() as ByteBuffer
}

actual fun ByteBuffer.rewind(): ByteBuffer {
    return this.rewind() as ByteBuffer
}

actual fun allocateDirectByteBuffer(capacity: Int): ByteBuffer {
    return ByteBuffer.allocateDirect(capacity)
}

actual fun allocateHeapByteBuffer(capacity: Int): ByteBuffer {
    return ByteBuffer.allocate(capacity)
}

actual fun ByteArray.wrapInHeapByteBuffer(offset: Int, length: Int): ByteBuffer {
    return ByteBuffer.wrap(this, offset, length)
}

actual fun ByteArray.wrapInHeapByteBuffer(): ByteBuffer {
    return ByteBuffer.wrap(this)
}
