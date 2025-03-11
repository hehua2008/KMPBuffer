package com.hym.kmpbuffer

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret

/**
 * @author hehua2008
 * @date 2025/3/6
 */
// Custom exceptions
actual class BufferUnderflowException : Exception("Buffer underflow")
actual class BufferOverflowException : Exception("Buffer overflow")
actual class ReadOnlyBufferException : Exception("Buffer is read-only")

// Enum for byte order (big-endian or little-endian)
actual class ByteOrder private constructor(private val name: String) {
    companion object {
        val BigEndian = ByteOrder("BIG_ENDIAN")
        val LittleEndian = ByteOrder("LITTLE_ENDIAN")

        val NativeOrder: ByteOrder by lazy {
            memScoped {
                val cint = alloc<Int>(0x12345678)
                val cbytes = cint.ptr.reinterpret<ByteVar>()
                if (cbytes[0].toInt() == 0x78) { // Check the location of the least significant byte
                    LittleEndian
                } else {
                    BigEndian
                }
            }
        }
    }

    override fun toString(): String = name
}

actual val BigEndianOrder: ByteOrder
    get() = ByteOrder.BigEndian

actual val LittleEndianOrder: ByteOrder
    get() = ByteOrder.LittleEndian

actual val NativeOrder: ByteOrder
    get() = ByteOrder.NativeOrder

actual abstract class ByteBuffer : Buffer<ByteBuffer>() {
    /**
     * Offset within the backing array (used for slice and duplicate).
     */
    protected abstract val offset: Int

    /**
     * Byte order (big-endian by default).
     */
    protected abstract var isBigEndian: Boolean

    /**
     * Whether the buffer is read-only.
     */
    protected abstract val isReadOnly: Boolean

    /**
     * Returns whether the buffer is read-only.
     *
     * @return Whether the buffer is read-only.
     */
    actual abstract fun isReadOnly(): Boolean

    /**
     * Returns this buffer's capacity.
     *
     * @return The capacity of this buffer.
     */
    actual fun capacity(): Int {
        return capacity
    }

    /**
     * Returns this buffer's position.
     *
     * @return The position of this buffer.
     */
    actual fun position(): Int {
        return position
    }

    /**
     * Returns this buffer's limit.
     *
     * @return The limit of this buffer.
     */
    actual fun limit(): Int {
        return limit
    }

    /**
     * Returns the number of elements between the current position and the limit.
     *
     * @return The number of elements remaining in this buffer.
     */
    actual fun remaining(): Int {
        return limit - position
    }

    /**
     * Tells whether there are any elements between the current position and the limit.
     *
     * @return `true` if, and only if, there is at least one element remaining in this buffer.
     */
    actual fun hasRemaining(): Boolean {
        return position < limit
    }

    /**
     * Relative get method. Reads the byte at the current position and increments the position.
     *
     * @return The byte at the current position.
     * @throws BufferUnderflowException If there are no remaining bytes in this buffer.
     */
    actual abstract fun get(): Byte

    /**
     * Relative put method. Writes the given byte into this buffer at the current position and increments the position.
     *
     * @param b The byte to be written.
     * @return This buffer.
     * @throws BufferOverflowException If there is no remaining space in this buffer.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    actual abstract fun put(b: Byte): ByteBuffer

    /**
     * Bulk get method. Transfers bytes from this buffer into the given destination array.
     *
     * @param dst The destination array.
     * @param offset The offset within the array of the first byte to be written.
     * @param length The number of bytes to be transferred.
     * @return This buffer.
     * @throws BufferUnderflowException If there are fewer than `length` bytes remaining in this buffer.
     * @throws IndexOutOfBoundsException If the preconditions on the `offset` and `length` parameters do not hold.
     */
    actual fun get(dst: ByteArray, offset: Int, length: Int): ByteBuffer {
        if (length > remaining()) throw BufferUnderflowException()
        if (offset < 0 || length < 0 || offset + length > dst.size) {
            throw IndexOutOfBoundsException("Invalid offset or length")
        }
        for (i in 0 until length) {
            dst[offset + i] = unsafeGet(position + i)
        }
        position += length
        return this
    }

    /**
     * Bulk put method. Transfers bytes from the given source array into this buffer.
     *
     * @param src The source array.
     * @param offset The offset within the array of the first byte to be read.
     * @param length The number of bytes to be transferred.
     * @return This buffer.
     * @throws BufferOverflowException If there is no remaining space in this buffer.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     * @throws IndexOutOfBoundsException If the preconditions on the `offset` and `length` parameters do not hold.
     */
    actual fun put(src: ByteArray, offset: Int, length: Int): ByteBuffer {
        if (isReadOnly) throw ReadOnlyBufferException()
        if (length > remaining()) throw BufferOverflowException()
        if (offset < 0 || length < 0 || offset + length > src.size) {
            throw IndexOutOfBoundsException("Invalid offset or length")
        }
        for (i in 0 until length) {
            unsafePut(position + i, src[offset + i])
        }
        position += length
        return this
    }

    /**
     * Absolute get method. Reads the byte at the given index without safe bounds checking on the index.
     *
     * @param index The index from which the byte will be read.
     * @return The byte at the given index.
     */
    protected abstract fun unsafeGet(index: Int): Byte

    /**
     * Absolute get method. Reads the byte at the given index.
     *
     * @param index The index from which the byte will be read.
     * @return The byte at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    actual abstract fun get(index: Int): Byte

    /**
     * Absolute put method. Writes the given byte into this buffer at the given index without safe bounds checking on
     * the index and read-only checking.
     *
     * @param index The index at which the byte will be written.
     * @param b The byte to be written.
     * @return This buffer.
     */
    protected abstract fun unsafePut(index: Int, b: Byte): ByteBuffer

    /**
     * Absolute put method. Writes the given byte into this buffer at the given index.
     *
     * @param index The index at which the byte will be written.
     * @param b The byte to be written.
     * @return This buffer.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    actual abstract fun put(index: Int, b: Byte): ByteBuffer

    /**
     * Returns this buffer's byte order.
     *
     * @return This buffer's byte order.
     */
    actual fun order(): ByteOrder {
        return if (isBigEndian) ByteOrder.BigEndian else ByteOrder.LittleEndian
    }

    /**
     * Modifies this buffer's byte order.
     *
     * @param bo The new byte order.
     * @return This buffer.
     */
    actual fun order(bo: ByteOrder): ByteBuffer {
        isBigEndian = bo == ByteOrder.BigEndian
        return this
    }

    /**
     * Creates a new byte buffer whose content is a shared subsequence of this buffer's content.
     *
     * The new buffer's content starts at the current position of this buffer. Changes to either buffer's content will
     * be visible in the other. However, the two buffers' position, limit, and mark values are independent.
     *
     * The new buffer's characteristics:
     * - Position: `0`
     * - Capacity and limit: Number of bytes remaining in this buffer (equivalent to [remaining])
     * - Mark: Undefined
     * - Byte order: [ByteOrder.BigEndian]
     * - Read-only: `true` if and only if this buffer is read-only
     *
     * @return The new byte buffer.
     */
    actual abstract fun slice(): ByteBuffer

    /**
     * Creates a new byte buffer whose content is a shared subsequence of this buffer's content.
     *
     * The content of the new buffer will start at position [index] in this buffer and will contain [length] elements.
     * Changes to this buffer's content will be visible in the new buffer, and vice versa. The two buffers' position,
     * limit, and mark values will be independent.
     *
     * The new buffer's characteristics:
     * - Position: `0`
     * - Capacity and limit: [length]
     * - Mark: Undefined
     * - Byte order: [ByteOrder.BigEndian]
     * - Read-only: `true` if and only if this buffer is read-only
     *
     * @param index The position in this buffer at which the content of the new buffer will start;
     * must be non-negative and no larger than [limit]
     * @param length The number of elements the new buffer will contain;
     * must be non-negative and no larger than [limit] - [index]
     *
     * @return The new byte buffer.
     *
     * @throws IllegalArgumentException If:
     * - [index] is negative or greater than [limit]
     * - [length] is negative
     * - [length] > [limit] - [index]
     */
    actual abstract fun slice(index: Int, length: Int): ByteBuffer

    /**
     * Creates a new byte buffer that shares this buffer's content.
     *
     * The new buffer's content is the same as this buffer's content. Changes to either buffer's content will be visible
     * in the other. However, the two buffers' position, limit, and mark values are independent.
     *
     * The new buffer's characteristics:
     * - Capacity, limit, position, and mark values: Identical to this buffer's
     * - Byte order: [ByteOrder.BigEndian]
     * - Read-only: `true` if and only if this buffer is read-only
     *
     * @return The new byte buffer.
     */
    actual abstract fun duplicate(): ByteBuffer

    /**
     * Creates a new read-only byte buffer that shares this buffer's content.
     *
     * The new buffer's content is the same as this buffer's content. Changes to this buffer's content will be visible
     * in the new buffer. However, the new buffer is read-only and will not allow modifications to the shared content.
     * The two buffers' position, limit, and mark values are independent.
     *
     * The new buffer's characteristics:
     * - Capacity, limit, position, and mark values: Identical to this buffer's
     * - Byte order: [ByteOrder.BigEndian]
     * - Read-only: Always `true`
     *
     * If this buffer is already read-only, this method behaves exactly like the [duplicate] method.
     *
     * @return The new read-only byte buffer.
     *
     * @see [duplicate]
     */
    actual abstract fun asReadOnlyBuffer(): ByteBuffer

    /**
     * Compacts this buffer _(optional operation)_.
     *
     * Copies the bytes between the buffer's current position and limit (if any) to the beginning of the buffer.
     * The copying behavior follows these rules:
     * - Byte at index `p` = [position] is copied to index 0
     * - Byte at index `p + 1` is copied to index 1
     * - This pattern continues until the byte at index [limit] - 1
     *
     * After compaction:
     * - New position is set to `n + 1` where `n = [limit] - [position] - 1`
     * - Limit is set to [capacity]
     * - Any existing mark is discarded
     *
     * The position is set to the number of bytes copied (rather than 0) to allow immediate subsequent relative _put_
     * operations.
     *
     * ### Typical Usage
     * Use this method after incomplete write operations. Example channel-to-channel copy pattern:
     * ```
     * buf.clear()  // Prepare buffer
     * while (in.read(buf) >= 0 || buf.position != 0) {
     *     buf.flip()
     *     out.write(buf)
     *     buf.compact()  // Handle partial writes
     * }
     * ```
     *
     * @return This buffer.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    actual abstract fun compact(): ByteBuffer

    /**
     * Returns the byte array that backs this buffer.
     *
     * @return The byte array that backs this buffer.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     * @throws UnsupportedOperationException If this buffer is not backed by an accessible array.
     */
    actual fun array(): ByteArray {
        if (this is HeapByteBuffer) {
            if (isReadOnly) throw ReadOnlyBufferException()
            return buffer
        } else {
            throw UnsupportedOperationException()
        }
    }

    /**
     * Returns the offset within this buffer's backing array of the first element of the buffer.
     *
     * @return The offset within this buffer's array of the first element of the buffer.
     * @throws UnsupportedOperationException If this buffer is not backed by an accessible array.
     */
    actual fun arrayOffset(): Int {
        if (this is HeapByteBuffer) {
            return offset
        } else {
            throw UnsupportedOperationException()
        }
    }

    /**
     * Tells whether or not this buffer is backed by an accessible byte array.
     *
     * @return `true` if, and only if, this buffer is backed by an array and is not read-only.
     */
    actual fun hasArray(): Boolean {
        if (this is HeapByteBuffer) {
            return !isReadOnly
        } else {
            return false
        }
    }

    /**
     * Tells whether or not this byte buffer is direct.
     *
     * @return `true` if, and only if, this buffer is direct.
     */
    actual abstract fun isDirect(): Boolean

    // Relative get methods
    /**
     * Relative [get] method for reading a char value.
     *
     * Reads the next two bytes at this buffer's current position, composing them into a char value according to the
     * current byte order, and then increments the position by two.
     *
     * @return The char value at the buffer's current position.
     * @throws BufferUnderflowException If there are fewer than two bytes remaining in this buffer.
     */
    actual abstract fun getChar(): Char

    /**
     * Relative [get] method for reading a short value.
     *
     * Reads the next two bytes at this buffer's current position, composing them into a short value according to the
     * current byte order, and then increments the position by two.
     *
     * @return The short value at the buffer's current position.
     * @throws BufferUnderflowException If there are fewer than two bytes remaining in this buffer.
     */
    actual abstract fun getShort(): Short

    /**
     * Relative [get] method for reading an int value.
     *
     * Reads the next four bytes at this buffer's current position, composing them into an int value according to the
     * current byte order, and then increments the position by four.
     *
     * @return The int value at the buffer's current position.
     * @throws BufferUnderflowException If there are fewer than four bytes remaining in this buffer.
     */
    actual abstract fun getInt(): Int

    /**
     * Relative [get] method for reading a long value.
     *
     * Reads the next eight bytes at this buffer's current position, composing them into a long value according to the
     * current byte order, and then increments the position by eight.
     *
     * @return The long value at the buffer's current position.
     * @throws BufferUnderflowException If there are fewer than eight bytes remaining in this buffer.
     */
    actual abstract fun getLong(): Long

    /**
     * Relative [get] method for reading a float value.
     *
     * Reads the next four bytes at this buffer's current position, composing them into a float value according to the
     * current byte order, and then increments the position by four.
     *
     * @return The float value at the buffer's current position.
     * @throws BufferUnderflowException If there are fewer than four bytes remaining in this buffer.
     */
    actual abstract fun getFloat(): Float

    /**
     * Relative [get] method for reading a double value.
     *
     * Reads the next eight bytes at this buffer's current position, composing them into a double value according to the
     * current byte order, and then increments the position by eight.
     *
     * @return The double value at the buffer's current position.
     * @throws BufferUnderflowException If there are fewer than eight bytes remaining in this buffer.
     */
    actual abstract fun getDouble(): Double

    // Relative put methods
    /**
     * Relative [put] method for writing a char value.
     *
     * Writes two bytes containing the given char value, in the current byte order, into this buffer at the current
     * position, and then increments the position by two.
     *
     * @param value The char value to be written.
     * @return This buffer.
     * @throws BufferOverflowException If there are fewer than two bytes remaining in this buffer.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    actual abstract fun putChar(value: Char): ByteBuffer

    /**
     * Relative [put] method for writing a short value.
     *
     * Writes two bytes containing the given short value, in the current byte order, into this buffer at the current
     * position, and then increments the position by two.
     *
     * @param value The short value to be written.
     * @return This buffer.
     * @throws BufferOverflowException If there are fewer than two bytes remaining in this buffer.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    actual abstract fun putShort(value: Short): ByteBuffer

    /**
     * Relative [put] method for writing an int value.
     *
     * Writes four bytes containing the given int value, in the current byte order, into this buffer at the current
     * position, and then increments the position by four.
     *
     * @param value The int value to be written.
     * @return This buffer.
     * @throws BufferOverflowException If there are fewer than four bytes remaining in this buffer.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    actual abstract fun putInt(value: Int): ByteBuffer

    /**
     * Relative [put] method for writing a long value.
     *
     * Writes eight bytes containing the given long value, in the current byte order, into this buffer at the current
     * position, and then increments the position by eight.
     *
     * @param value The long value to be written.
     * @return This buffer.
     * @throws BufferOverflowException If there are fewer than eight bytes remaining in this buffer.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    actual abstract fun putLong(value: Long): ByteBuffer

    /**
     * Relative [put] method for writing a float value.
     *
     * Writes four bytes containing the given float value, in the current byte order, into this buffer at the current
     * position, and then increments the position by four.
     *
     * @param value The float value to be written.
     * @return This buffer.
     * @throws BufferOverflowException If there are fewer than four bytes remaining in this buffer.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    actual abstract fun putFloat(value: Float): ByteBuffer

    /**
     * Relative [put] method for writing a double value.
     *
     * Writes eight bytes containing the given double value, in the current byte order, into this buffer at the current
     * position, and then increments the position by eight.
     *
     * @param value The double value to be written.
     * @return This buffer.
     * @throws BufferOverflowException If there are fewer than eight bytes remaining in this buffer.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    actual abstract fun putDouble(value: Double): ByteBuffer

    // Absolute get methods
    /**
     * Absolute [get] method for reading a char value.
     *
     * Reads two bytes at the given index, composing them into a char value according to the current byte order.
     *
     * @param index The index from which the bytes will be read.
     * @return The char value at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    actual abstract fun getChar(index: Int): Char

    /**
     * Absolute [get] method for reading a short value.
     *
     * Reads two bytes at the given index, composing them into a short value according to the current byte order.
     *
     * @param index The index from which the bytes will be read.
     * @return The short value at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    actual abstract fun getShort(index: Int): Short

    /**
     * Absolute [get] method for reading an int value.
     *
     * Reads four bytes at the given index, composing them into a int value according to the current byte order.
     *
     * @param index The index from which the bytes will be read.
     * @return The int value at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    actual abstract fun getInt(index: Int): Int

    /**
     * Absolute [get] method for reading a long value.
     *
     * Reads eight bytes at the given index, composing them into a long value according to the current byte order.
     *
     * @param index The index from which the bytes will be read.
     * @return The long value at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    actual abstract fun getLong(index: Int): Long

    /**
     * Absolute [get] method for reading a float value.
     *
     * Reads four bytes at the given index, composing them into a float value according to the current byte order.
     *
     * @param index The index from which the bytes will be read.
     * @return The float value at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    actual abstract fun getFloat(index: Int): Float

    /**
     * Absolute [get] method for reading a double value.
     *
     * Reads eight bytes at the given index, composing them into a double value according to the current byte order.
     *
     * @param index The index from which the bytes will be read.
     * @return The double value at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    actual abstract fun getDouble(index: Int): Double

    // Absolute put methods
    /**
     * Absolute [put] method for writing a char value.
     *
     * Writes two bytes containing the given char value, in the current byte order, into this buffer at the given index.
     *
     * @param index The index at which the bytes will be written.
     * @param value The char value to be written.
     * @return This buffer.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    actual abstract fun putChar(index: Int, value: Char): ByteBuffer

    /**
     * Absolute [put] method for writing a short value.
     *
     * Writes two bytes containing the given short value, in the current byte order, into this buffer at the given
     * index.
     *
     * @param index The index at which the bytes will be written.
     * @param value The short value to be written.
     * @return This buffer.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    actual abstract fun putShort(index: Int, value: Short): ByteBuffer

    /**
     * Absolute [put] method for writing an int value.
     *
     * Writes four bytes containing the given int value, in the current byte order, into this buffer at the given index.
     *
     * @param index The index at which the bytes will be written.
     * @param value The int value to be written.
     * @return This buffer.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    actual abstract fun putInt(index: Int, value: Int): ByteBuffer

    /**
     * Absolute [put] method for writing a long value.
     *
     * Writes eight bytes containing the given long value, in the current byte order, into this buffer at the given
     * index.
     *
     * @param index The index at which the bytes will be written.
     * @param value The long value to be written.
     * @return This buffer.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    actual abstract fun putLong(index: Int, value: Long): ByteBuffer

    /**
     * Absolute [put] method for writing a float value.
     *
     * Writes four bytes containing the given float value, in the current byte order, into this buffer at the given
     * index.
     *
     * @param index The index at which the bytes will be written.
     * @param value The float value to be written.
     * @return This buffer.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    actual abstract fun putFloat(index: Int, value: Float): ByteBuffer

    /**
     * Absolute [put] method for writing a double value.
     *
     * Writes eight bytes containing the given double value, in the current byte order, into this buffer at the given
     * index.
     *
     * @param index The index at which the bytes will be written.
     * @param value The double value to be written.
     * @return This buffer.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    actual abstract fun putDouble(index: Int, value: Double): ByteBuffer

    /**
     * Returns the current hash code of this buffer.
     *
     * The hash code is calculated based only on the remaining elements: from [position] (inclusive) to
     * [limit] - 1 (inclusive).
     *
     * ### Important Notes:
     * - Hash code is content-dependent and will change if buffer contents are modified
     * - Strongly discouraged to use buffers as keys in hash-based collections (like [HashMap])
     *   unless the content is guaranteed to remain immutable
     *
     * @return The current hash code value reflecting buffer's content.
     */
    actual override fun hashCode(): Int {
        var result = 1
        for (i in position until limit) {
            result = 31 * result + unsafeGet(i).toInt()
        }
        return result
    }

    /**
     * Indicates whether this buffer is equal to another object.
     *
     * Two byte buffers are considered equal **if and only if** all the following conditions are met:
     * 1. They are both byte buffers
     * 2. They have the same number of remaining elements ([remaining])
     * 3. All pairs of corresponding remaining elements (from [position] to [limit]-1 in each buffer) are identical
     *
     * A byte buffer is never equal to objects of other types.
     *
     * @param other The object to compare with this buffer.
     * @return `true` if and only if the specified object is a byte buffer that meets all equality conditions.
     */
    actual override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ByteBuffer) return false
        if (remaining() != other.remaining()) return false
        for (i in 0 until remaining()) {
            if (unsafeGet(position + i) != other.unsafeGet(other.position + i)) return false
        }
        return true
    }

    /**
     * Returns a string summarizing the state of this buffer.
     *
     * @return A summary string.
     */
    actual override fun toString(): String {
        return "${this::class.simpleName}[pos=$position lim=$limit cap=$capacity]"
    }
}

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
    return DirectByteBuffer.allocate(capacity)
}

actual fun allocateHeapByteBuffer(capacity: Int): ByteBuffer {
    return HeapByteBuffer.allocate(capacity)
}

actual fun ByteArray.wrapInHeapByteBuffer(offset: Int, length: Int): ByteBuffer {
    return HeapByteBuffer.wrap(this, offset, length)
}

actual fun ByteArray.wrapInHeapByteBuffer(): ByteBuffer {
    return HeapByteBuffer.wrap(this)
}
