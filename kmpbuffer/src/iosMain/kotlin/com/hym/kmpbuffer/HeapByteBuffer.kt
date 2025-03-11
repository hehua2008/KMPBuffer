package com.hym.kmpbuffer

/**
 * @author hehua2008
 * @date 2025/3/5
 */
/**
 * A byte buffer.
 *
 * This class defines six categories of operations upon byte buffers:
 *
 * - Absolute and relative [get] and [put] methods that read and write single bytes;
 * - Bulk get methods that transfer contiguous sequences of bytes from this buffer into an array;
 * - Bulk put methods that transfer contiguous sequences of bytes from a byte array into this buffer;
 * - A method for [compact] a byte buffer.
 */
class HeapByteBuffer private constructor(
    /**
     * Backing byte array for the buffer.
     */
    internal val buffer: ByteArray,
    /**
     * Offset within the backing array (used for slice and duplicate).
     */
    override val offset: Int,
    /**
     * The capacity of the buffer.
     */
    override val capacity: Int,
    /**
     * Current position in the buffer.
     */
    override var position: Int,
    /**
     * Limit of the buffer (the index of the first element that should not be read or written).
     */
    override var limit: Int,
    /**
     * Mark position (used for reset).
     */
    override var mark: Int,
    /**
     * Byte order (big-endian by default).
     */
    override var isBigEndian: Boolean,
    /**
     * Whether the buffer is read-only.
     */
    override val isReadOnly: Boolean
) : ByteBuffer() {
    /**
     * Primary constructor for normal allocation.
     *
     * @param capacity The capacity of the buffer.
     */
    constructor(capacity: Int) : this(
        buffer = ByteArray(capacity),
        offset = 0,
        capacity = capacity,
        position = 0,
        limit = capacity,
        mark = -1,
        isBigEndian = true,
        isReadOnly = false
    )

    companion object {
        /**
         * The new buffer will have:
         * - A backing array accessible via [array]
         * - An array offset ([arrayOffset]) of zero
         *
         * @param capacity The new buffer's capacity in bytes.
         * @return The new byte buffer.
         * @throws IllegalArgumentException If [capacity] is negative.
         */
        fun allocate(capacity: Int): HeapByteBuffer {
            if (capacity < 0) throw IllegalArgumentException("Negative capacity: $capacity")
            return HeapByteBuffer(capacity)
        }

        /**
         * Wraps a byte array into a buffer.
         *
         * The new buffer is backed by the given array: modifications to the buffer will modify the array, and vice
         * versa.
         *
         * ### Buffer Initial State:
         * - Capacity: `array.size`
         * - Position: [offset]
         * - Limit: [offset] + [length]
         * - Mark: Undefined
         * - Byte order: [ByteOrder.BigEndian]
         * - Backing array: [array]
         * - Array offset: `0`
         *
         * @param array The array to back the buffer (will be directly modified by buffer operations).
         * @param offset The starting position in the array:
         *               - Must be ≥ 0
         *               - Must be ≤ [array.size]
         * @param length The number of bytes to use:
         *               - Must be ≥ 0
         *               - Must satisfy [offset] + [length] ≤ [array.size]
         *
         * @return The new byte buffer.
         * @throws IndexOutOfBoundsException If:
         * - [offset] or [length] is negative
         * - [offset] + [length] exceeds [array.size]
         */
        fun wrap(array: ByteArray, offset: Int, length: Int): HeapByteBuffer {
            if (offset < 0 || length < 0 || offset + length > array.size) {
                throw IndexOutOfBoundsException("Invalid offset or length")
            }
            return HeapByteBuffer(
                buffer = array,
                offset = 0,
                capacity = array.size,
                position = offset,
                limit = offset + length,
                mark = -1,
                isBigEndian = true,
                isReadOnly = false
            )
        }

        /**
         * Wraps a byte array into a buffer.
         *
         * The new buffer is backed by the given array: modifications to the buffer will modify the array, and vice
         * versa.
         *
         * ### Buffer Initial State:
         * - Capacity and limit: `array.size`
         * - Position: `0`
         * - Mark: Undefined
         * - Byte order: [ByteOrder.BigEndian]
         * - Backing array: [array]
         * - Array offset: `0`
         *
         * @param array The array to back the buffer (will be directly modified by buffer operations).
         * @return The new byte buffer.
         */
        fun wrap(array: ByteArray): HeapByteBuffer {
            return HeapByteBuffer(
                buffer = array,
                offset = 0,
                capacity = array.size,
                position = 0,
                limit = array.size,
                mark = -1,
                isBigEndian = true,
                isReadOnly = false
            )
        }
    }

    /**
     * Returns whether the buffer is read-only.
     *
     * @return Whether the buffer is read-only.
     */
    override fun isReadOnly(): Boolean {
        return isReadOnly
    }

    /**
     * Relative get method. Reads the byte at the current position and increments the position.
     *
     * @return The byte at the current position.
     * @throws BufferUnderflowException If there are no remaining bytes in this buffer.
     */
    override fun get(): Byte {
        if (!hasRemaining()) throw BufferUnderflowException()
        return buffer[offset + position++]
    }

    /**
     * Relative put method. Writes the given byte into this buffer at the current position and increments the position.
     *
     * @param b The byte to be written.
     * @return This buffer.
     * @throws BufferOverflowException If there is no remaining space in this buffer.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    override fun put(b: Byte): HeapByteBuffer {
        if (isReadOnly) throw ReadOnlyBufferException()
        if (!hasRemaining()) throw BufferOverflowException()
        buffer[offset + position++] = b
        return this
    }

    /**
     * Absolute get method. Reads the byte at the given index without safe bounds checking on the index.
     *
     * @param index The index from which the byte will be read.
     * @return The byte at the given index.
     */
    override fun unsafeGet(index: Int): Byte {
        return buffer[offset + index]
    }

    /**
     * Absolute get method. Reads the byte at the given index.
     *
     * @param index The index from which the byte will be read.
     * @return The byte at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    override fun get(index: Int): Byte {
        if (index < 0 || index >= limit) throw IndexOutOfBoundsException("Invalid index: $index")
        return buffer[offset + index]
    }

    /**
     * Absolute put method. Writes the given byte into this buffer at the given index without safe bounds checking on
     * the index and read-only checking.
     *
     * @param index The index at which the byte will be written.
     * @param b The byte to be written.
     * @return This buffer.
     */
    override fun unsafePut(index: Int, b: Byte): HeapByteBuffer {
        buffer[offset + index] = b
        return this
    }

    /**
     * Absolute put method. Writes the given byte into this buffer at the given index.
     *
     * @param index The index at which the byte will be written.
     * @param b The byte to be written.
     * @return This buffer.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    override fun put(index: Int, b: Byte): HeapByteBuffer {
        if (isReadOnly) throw ReadOnlyBufferException()
        if (index < 0 || index >= limit) throw IndexOutOfBoundsException("Invalid index: $index")
        buffer[offset + index] = b
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
    override fun slice(): HeapByteBuffer {
        return HeapByteBuffer(
            buffer = buffer,
            offset = offset + position,
            capacity = remaining(),
            position = 0,
            limit = remaining(),
            mark = -1,
            isBigEndian = true,
            isReadOnly = isReadOnly
        )
    }

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
    override fun slice(index: Int, length: Int): HeapByteBuffer {
        if (index < 0 || length < 0 || index + length > limit) {
            throw IllegalArgumentException("Invalid index or length")
        }
        return HeapByteBuffer(
            buffer = buffer,
            offset = offset + index,
            capacity = length,
            position = 0,
            limit = length,
            mark = -1,
            isBigEndian = true,
            isReadOnly = isReadOnly
        )
    }

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
    override fun duplicate(): HeapByteBuffer {
        return HeapByteBuffer(
            buffer = buffer,
            offset = offset,
            capacity = capacity,
            position = position,
            limit = limit,
            mark = mark,
            isBigEndian = true,
            isReadOnly = isReadOnly
        )
    }

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
    override fun asReadOnlyBuffer(): HeapByteBuffer {
        return HeapByteBuffer(
            buffer = buffer,
            offset = offset,
            capacity = capacity,
            position = position,
            limit = limit,
            mark = mark,
            isBigEndian = true,
            isReadOnly = true
        )
    }

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
    override fun compact(): HeapByteBuffer {
        if (isReadOnly) throw ReadOnlyBufferException()
        if (position > 0) {
            for (i in position until limit) {
                buffer[offset + i - position] = buffer[offset + i]
            }
        }
        position = limit - position
        limit = capacity
        mark = -1
        return this
    }

    /**
     * Tells whether or not this byte buffer is direct.
     *
     * @return `true` if, and only if, this buffer is direct.
     */
    override fun isDirect(): Boolean {
        return false // This implementation is not direct
    }

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
    override fun getChar(): Char {
        return if (isBigEndian) {
            ((get().toInt() and 0xFF) shl 8) or (get().toInt() and 0xFF)
        } else {
            (get().toInt() and 0xFF) or ((get().toInt() and 0xFF) shl 8)
        }.toChar()
    }

    /**
     * Relative [get] method for reading a short value.
     *
     * Reads the next two bytes at this buffer's current position, composing them into a short value according to the
     * current byte order, and then increments the position by two.
     *
     * @return The short value at the buffer's current position.
     * @throws BufferUnderflowException If there are fewer than two bytes remaining in this buffer.
     */
    override fun getShort(): Short {
        return if (isBigEndian) {
            ((get().toInt() and 0xFF) shl 8) or (get().toInt() and 0xFF)
        } else {
            (get().toInt() and 0xFF) or ((get().toInt() and 0xFF) shl 8)
        }.toShort()
    }

    /**
     * Relative [get] method for reading an int value.
     *
     * Reads the next four bytes at this buffer's current position, composing them into an int value according to the
     * current byte order, and then increments the position by four.
     *
     * @return The int value at the buffer's current position.
     * @throws BufferUnderflowException If there are fewer than four bytes remaining in this buffer.
     */
    override fun getInt(): Int {
        return if (isBigEndian) {
            ((get().toInt() and 0xFF) shl 24) or
                    ((get().toInt() and 0xFF) shl 16) or
                    ((get().toInt() and 0xFF) shl 8) or
                    (get().toInt() and 0xFF)
        } else {
            (get().toInt() and 0xFF) or
                    ((get().toInt() and 0xFF) shl 8) or
                    ((get().toInt() and 0xFF) shl 16) or
                    ((get().toInt() and 0xFF) shl 24)
        }
    }

    /**
     * Relative [get] method for reading a long value.
     *
     * Reads the next eight bytes at this buffer's current position, composing them into a long value according to the
     * current byte order, and then increments the position by eight.
     *
     * @return The long value at the buffer's current position.
     * @throws BufferUnderflowException If there are fewer than eight bytes remaining in this buffer.
     */
    override fun getLong(): Long {
        return if (isBigEndian) {
            ((get().toLong() and 0xFF) shl 56) or
                    ((get().toLong() and 0xFF) shl 48) or
                    ((get().toLong() and 0xFF) shl 40) or
                    ((get().toLong() and 0xFF) shl 32) or
                    ((get().toLong() and 0xFF) shl 24) or
                    ((get().toLong() and 0xFF) shl 16) or
                    ((get().toLong() and 0xFF) shl 8) or
                    (get().toLong() and 0xFF)
        } else {
            (get().toLong() and 0xFF) or
                    ((get().toLong() and 0xFF) shl 8) or
                    ((get().toLong() and 0xFF) shl 16) or
                    ((get().toLong() and 0xFF) shl 24) or
                    ((get().toLong() and 0xFF) shl 32) or
                    ((get().toLong() and 0xFF) shl 40) or
                    ((get().toLong() and 0xFF) shl 48) or
                    ((get().toLong() and 0xFF) shl 56)
        }
    }

    /**
     * Relative [get] method for reading a float value.
     *
     * Reads the next four bytes at this buffer's current position, composing them into a float value according to the
     * current byte order, and then increments the position by four.
     *
     * @return The float value at the buffer's current position.
     * @throws BufferUnderflowException If there are fewer than four bytes remaining in this buffer.
     */
    override fun getFloat(): Float {
        return Float.fromBits(getInt())
    }

    /**
     * Relative [get] method for reading a double value.
     *
     * Reads the next eight bytes at this buffer's current position, composing them into a double value according to the
     * current byte order, and then increments the position by eight.
     *
     * @return The double value at the buffer's current position.
     * @throws BufferUnderflowException If there are fewer than eight bytes remaining in this buffer.
     */
    override fun getDouble(): Double {
        return Double.fromBits(getLong())
    }

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
    override fun putChar(value: Char): HeapByteBuffer {
        if (isBigEndian) {
            put((value.code shr 8).toByte())
            put(value.code.toByte())
        } else {
            put(value.code.toByte())
            put((value.code shr 8).toByte())
        }
        return this
    }

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
    override fun putShort(value: Short): HeapByteBuffer {
        if (isBigEndian) {
            put((value.toInt() shr 8).toByte())
            put(value.toByte())
        } else {
            put(value.toByte())
            put((value.toInt() shr 8).toByte())
        }
        return this
    }

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
    override fun putInt(value: Int): HeapByteBuffer {
        if (isBigEndian) {
            put((value shr 24).toByte())
            put((value shr 16).toByte())
            put((value shr 8).toByte())
            put(value.toByte())
        } else {
            put(value.toByte())
            put((value shr 8).toByte())
            put((value shr 16).toByte())
            put((value shr 24).toByte())
        }
        return this
    }

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
    override fun putLong(value: Long): HeapByteBuffer {
        if (isBigEndian) {
            put((value shr 56).toByte())
            put((value shr 48).toByte())
            put((value shr 40).toByte())
            put((value shr 32).toByte())
            put((value shr 24).toByte())
            put((value shr 16).toByte())
            put((value shr 8).toByte())
            put(value.toByte())
        } else {
            put(value.toByte())
            put((value shr 8).toByte())
            put((value shr 16).toByte())
            put((value shr 24).toByte())
            put((value shr 32).toByte())
            put((value shr 40).toByte())
            put((value shr 48).toByte())
            put((value shr 56).toByte())
        }
        return this
    }

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
    override fun putFloat(value: Float): HeapByteBuffer {
        return putInt(value.toRawBits())
    }

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
    override fun putDouble(value: Double): HeapByteBuffer {
        return putLong(value.toRawBits())
    }

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
    override fun getChar(index: Int): Char {
        return if (isBigEndian) {
            ((get(index).toInt() and 0xFF) shl 8) or (get(index + 1).toInt() and 0xFF)
        } else {
            (get(index).toInt() and 0xFF) or ((get(index + 1).toInt() and 0xFF) shl 8)
        }.toChar()
    }

    /**
     * Absolute [get] method for reading a short value.
     *
     * Reads two bytes at the given index, composing them into a short value according to the current byte order.
     *
     * @param index The index from which the bytes will be read.
     * @return The short value at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    override fun getShort(index: Int): Short {
        return if (isBigEndian) {
            ((get(index).toInt() and 0xFF) shl 8) or (get(index + 1).toInt() and 0xFF)
        } else {
            (get(index).toInt() and 0xFF) or ((get(index + 1).toInt() and 0xFF) shl 8)
        }.toShort()
    }

    /**
     * Absolute [get] method for reading an int value.
     *
     * Reads four bytes at the given index, composing them into a int value according to the current byte order.
     *
     * @param index The index from which the bytes will be read.
     * @return The int value at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    override fun getInt(index: Int): Int {
        return if (isBigEndian) {
            ((get(index).toInt() and 0xFF) shl 24) or
                    ((get(index + 1).toInt() and 0xFF) shl 16) or
                    ((get(index + 2).toInt() and 0xFF) shl 8) or
                    (get(index + 3).toInt() and 0xFF)
        } else {
            (get(index).toInt() and 0xFF) or
                    ((get(index + 1).toInt() and 0xFF) shl 8) or
                    ((get(index + 2).toInt() and 0xFF) shl 16) or
                    ((get(index + 3).toInt() and 0xFF) shl 24)
        }
    }

    /**
     * Absolute [get] method for reading a long value.
     *
     * Reads eight bytes at the given index, composing them into a long value according to the current byte order.
     *
     * @param index The index from which the bytes will be read.
     * @return The long value at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    override fun getLong(index: Int): Long {
        return if (isBigEndian) {
            ((get(index).toLong() and 0xFF) shl 56) or
                    ((get(index + 1).toLong() and 0xFF) shl 48) or
                    ((get(index + 2).toLong() and 0xFF) shl 40) or
                    ((get(index + 3).toLong() and 0xFF) shl 32) or
                    ((get(index + 4).toLong() and 0xFF) shl 24) or
                    ((get(index + 5).toLong() and 0xFF) shl 16) or
                    ((get(index + 6).toLong() and 0xFF) shl 8) or
                    (get(index + 7).toLong() and 0xFF)
        } else {
            (get(index).toLong() and 0xFF) or
                    ((get(index + 1).toLong() and 0xFF) shl 8) or
                    ((get(index + 2).toLong() and 0xFF) shl 16) or
                    ((get(index + 3).toLong() and 0xFF) shl 24) or
                    ((get(index + 4).toLong() and 0xFF) shl 32) or
                    ((get(index + 5).toLong() and 0xFF) shl 40) or
                    ((get(index + 6).toLong() and 0xFF) shl 48) or
                    ((get(index + 7).toLong() and 0xFF) shl 56)
        }
    }

    /**
     * Absolute [get] method for reading a float value.
     *
     * Reads four bytes at the given index, composing them into a float value according to the current byte order.
     *
     * @param index The index from which the bytes will be read.
     * @return The float value at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    override fun getFloat(index: Int): Float {
        return Float.fromBits(getInt(index))
    }

    /**
     * Absolute [get] method for reading a double value.
     *
     * Reads eight bytes at the given index, composing them into a double value according to the current byte order.
     *
     * @param index The index from which the bytes will be read.
     * @return The double value at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    override fun getDouble(index: Int): Double {
        return Double.fromBits(getLong(index))
    }

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
    override fun putChar(index: Int, value: Char): HeapByteBuffer {
        if (isBigEndian) {
            put(index, (value.code shr 8).toByte())
            put(index + 1, value.code.toByte())
        } else {
            put(index, value.code.toByte())
            put(index + 1, (value.code shr 8).toByte())
        }
        return this
    }

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
    override fun putShort(index: Int, value: Short): HeapByteBuffer {
        if (isBigEndian) {
            put(index, (value.toInt() shr 8).toByte())
            put(index + 1, value.toByte())
        } else {
            put(index, value.toByte())
            put(index + 1, (value.toInt() shr 8).toByte())
        }
        return this
    }

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
    override fun putInt(index: Int, value: Int): HeapByteBuffer {
        if (isBigEndian) {
            put(index, (value shr 24).toByte())
            put(index + 1, (value shr 16).toByte())
            put(index + 2, (value shr 8).toByte())
            put(index + 3, value.toByte())
        } else {
            put(index, value.toByte())
            put(index + 1, (value shr 8).toByte())
            put(index + 2, (value shr 16).toByte())
            put(index + 3, (value shr 24).toByte())
        }
        return this
    }

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
    override fun putLong(index: Int, value: Long): HeapByteBuffer {
        if (isBigEndian) {
            put(index, (value shr 56).toByte())
            put(index + 1, (value shr 48).toByte())
            put(index + 2, (value shr 40).toByte())
            put(index + 3, (value shr 32).toByte())
            put(index + 4, (value shr 24).toByte())
            put(index + 5, (value shr 16).toByte())
            put(index + 6, (value shr 8).toByte())
            put(index + 7, value.toByte())
        } else {
            put(index, value.toByte())
            put(index + 1, (value shr 8).toByte())
            put(index + 2, (value shr 16).toByte())
            put(index + 3, (value shr 24).toByte())
            put(index + 4, (value shr 32).toByte())
            put(index + 5, (value shr 40).toByte())
            put(index + 6, (value shr 48).toByte())
            put(index + 7, (value shr 56).toByte())
        }
        return this
    }

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
    override fun putFloat(index: Int, value: Float): HeapByteBuffer {
        return putInt(index, value.toRawBits())
    }

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
    override fun putDouble(index: Int, value: Double): HeapByteBuffer {
        return putLong(index, value.toRawBits())
    }
}
