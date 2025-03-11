package com.hym.kmpbuffer

/**
 * @author hehua2008
 * @date 2025/3/6
 */
// Enum for byte order (big-endian or little-endian)
expect class ByteOrder

expect val BigEndianOrder: ByteOrder
expect val LittleEndianOrder: ByteOrder
expect val NativeOrder: ByteOrder

// Custom exceptions
expect class BufferUnderflowException
expect class BufferOverflowException
expect class ReadOnlyBufferException

expect abstract class ByteBuffer {
    /**
     * Returns whether the buffer is read-only.
     *
     * @return Whether the buffer is read-only.
     */
    abstract fun isReadOnly(): Boolean

    /**
     * Returns this buffer's capacity.
     *
     * @return The capacity of this buffer.
     */
    fun capacity(): Int

    /**
     * Returns this buffer's position.
     *
     * @return The position of this buffer.
     */
    fun position(): Int

    /**
     * Returns this buffer's limit.
     *
     * @return The limit of this buffer.
     */
    fun limit(): Int

    /**
     * Returns the number of elements between the current position and the limit.
     *
     * @return The number of elements remaining in this buffer.
     */
    fun remaining(): Int

    /**
     * Tells whether there are any elements between the current position and the limit.
     *
     * @return `true` if, and only if, there is at least one element remaining in this buffer.
     */
    fun hasRemaining(): Boolean

    /**
     * Relative get method. Reads the byte at the current position and increments the position.
     *
     * @return The byte at the current position.
     * @throws BufferUnderflowException If there are no remaining bytes in this buffer.
     */
    abstract fun get(): Byte

    /**
     * Relative put method. Writes the given byte into this buffer at the current position and increments the position.
     *
     * @param b The byte to be written.
     * @return This buffer.
     * @throws BufferOverflowException If there is no remaining space in this buffer.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    abstract fun put(b: Byte): ByteBuffer

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
    fun get(dst: ByteArray, offset: Int, length: Int): ByteBuffer

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
    fun put(src: ByteArray, offset: Int, length: Int): ByteBuffer

    /**
     * Absolute get method. Reads the byte at the given index.
     *
     * @param index The index from which the byte will be read.
     * @return The byte at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    abstract fun get(index: Int): Byte

    /**
     * Absolute put method. Writes the given byte into this buffer at the given index.
     *
     * @param index The index at which the byte will be written.
     * @param b The byte to be written.
     * @return This buffer.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     */
    abstract fun put(index: Int, b: Byte): ByteBuffer

    /**
     * Returns this buffer's byte order.
     *
     * @return This buffer's byte order.
     */
    fun order(): ByteOrder

    /**
     * Modifies this buffer's byte order.
     *
     * @param bo The new byte order.
     * @return This buffer.
     */
    fun order(bo: ByteOrder): ByteBuffer

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
    abstract fun slice(): ByteBuffer

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
    abstract fun slice(index: Int, length: Int): ByteBuffer

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
    abstract fun duplicate(): ByteBuffer

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
    abstract fun asReadOnlyBuffer(): ByteBuffer

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
    abstract fun compact(): ByteBuffer

    /**
     * Returns the byte array that backs this buffer.
     *
     * @return The byte array that backs this buffer.
     * @throws ReadOnlyBufferException If this buffer is read-only.
     * @throws UnsupportedOperationException If this buffer is not backed by an accessible array.
     */
    fun array(): ByteArray

    /**
     * Returns the offset within this buffer's backing array of the first element of the buffer.
     *
     * @return The offset within this buffer's array of the first element of the buffer.
     * @throws UnsupportedOperationException If this buffer is not backed by an accessible array.
     */
    fun arrayOffset(): Int

    /**
     * Tells whether or not this buffer is backed by an accessible byte array.
     *
     * @return `true` if, and only if, this buffer is backed by an array and is not read-only.
     */
    fun hasArray(): Boolean

    /**
     * Tells whether or not this byte buffer is direct.
     *
     * @return `true` if, and only if, this buffer is direct.
     */
    abstract fun isDirect(): Boolean

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
    abstract fun getChar(): Char

    /**
     * Relative [get] method for reading a short value.
     *
     * Reads the next two bytes at this buffer's current position, composing them into a short value according to the
     * current byte order, and then increments the position by two.
     *
     * @return The short value at the buffer's current position.
     * @throws BufferUnderflowException If there are fewer than two bytes remaining in this buffer.
     */
    abstract fun getShort(): Short

    /**
     * Relative [get] method for reading an int value.
     *
     * Reads the next four bytes at this buffer's current position, composing them into an int value according to the
     * current byte order, and then increments the position by four.
     *
     * @return The int value at the buffer's current position.
     * @throws BufferUnderflowException If there are fewer than four bytes remaining in this buffer.
     */
    abstract fun getInt(): Int

    /**
     * Relative [get] method for reading a long value.
     *
     * Reads the next eight bytes at this buffer's current position, composing them into a long value according to the
     * current byte order, and then increments the position by eight.
     *
     * @return The long value at the buffer's current position.
     * @throws BufferUnderflowException If there are fewer than eight bytes remaining in this buffer.
     */
    abstract fun getLong(): Long

    /**
     * Relative [get] method for reading a float value.
     *
     * Reads the next four bytes at this buffer's current position, composing them into a float value according to the
     * current byte order, and then increments the position by four.
     *
     * @return The float value at the buffer's current position.
     * @throws BufferUnderflowException If there are fewer than four bytes remaining in this buffer.
     */
    abstract fun getFloat(): Float

    /**
     * Relative [get] method for reading a double value.
     *
     * Reads the next eight bytes at this buffer's current position, composing them into a double value according to the
     * current byte order, and then increments the position by eight.
     *
     * @return The double value at the buffer's current position.
     * @throws BufferUnderflowException If there are fewer than eight bytes remaining in this buffer.
     */
    abstract fun getDouble(): Double

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
    abstract fun putChar(value: Char): ByteBuffer

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
    abstract fun putShort(value: Short): ByteBuffer

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
    abstract fun putInt(value: Int): ByteBuffer

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
    abstract fun putLong(value: Long): ByteBuffer

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
    abstract fun putFloat(value: Float): ByteBuffer

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
    abstract fun putDouble(value: Double): ByteBuffer

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
    abstract fun getChar(index: Int): Char

    /**
     * Absolute [get] method for reading a short value.
     *
     * Reads two bytes at the given index, composing them into a short value according to the current byte order.
     *
     * @param index The index from which the bytes will be read.
     * @return The short value at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    abstract fun getShort(index: Int): Short

    /**
     * Absolute [get] method for reading an int value.
     *
     * Reads four bytes at the given index, composing them into a int value according to the current byte order.
     *
     * @param index The index from which the bytes will be read.
     * @return The int value at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    abstract fun getInt(index: Int): Int

    /**
     * Absolute [get] method for reading a long value.
     *
     * Reads eight bytes at the given index, composing them into a long value according to the current byte order.
     *
     * @param index The index from which the bytes will be read.
     * @return The long value at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    abstract fun getLong(index: Int): Long

    /**
     * Absolute [get] method for reading a float value.
     *
     * Reads four bytes at the given index, composing them into a float value according to the current byte order.
     *
     * @param index The index from which the bytes will be read.
     * @return The float value at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    abstract fun getFloat(index: Int): Float

    /**
     * Absolute [get] method for reading a double value.
     *
     * Reads eight bytes at the given index, composing them into a double value according to the current byte order.
     *
     * @param index The index from which the bytes will be read.
     * @return The double value at the given index.
     * @throws IndexOutOfBoundsException If the index is negative or not smaller than the buffer's limit.
     */
    abstract fun getDouble(index: Int): Double

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
    abstract fun putChar(index: Int, value: Char): ByteBuffer

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
    abstract fun putShort(index: Int, value: Short): ByteBuffer

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
    abstract fun putInt(index: Int, value: Int): ByteBuffer

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
    abstract fun putLong(index: Int, value: Long): ByteBuffer

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
    abstract fun putFloat(index: Int, value: Float): ByteBuffer

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
    abstract fun putDouble(index: Int, value: Double): ByteBuffer

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
    override fun hashCode(): Int

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
    override fun equals(other: Any?): Boolean

    /**
     * Returns a string summarizing the state of this buffer.
     *
     * @return A summary string.
     */
    override fun toString(): String
}

/**
 * Sets this buffer's position.
 *
 * @param newPosition The new position value; must be non-negative and no larger than the current limit.
 * @return This buffer.
 * @throws IllegalArgumentException If the preconditions on the parameter do not hold.
 */
expect fun ByteBuffer.position(newPosition: Int): ByteBuffer

/**
 * Sets this buffer's limit.
 *
 * @param newLimit The new limit value; must be non-negative and no larger than this buffer's capacity.
 * @return This buffer.
 * @throws IllegalArgumentException If the preconditions on the parameter do not hold.
 */
expect fun ByteBuffer.limit(newLimit: Int): ByteBuffer

/**
 * Marks the current position in this buffer.
 *
 * @return This buffer.
 */
expect fun ByteBuffer.mark(): ByteBuffer

/**
 * Resets this buffer's position to the previously-marked position.
 *
 * @return This buffer.
 * @throws IllegalArgumentException If the mark has not been set.
 */
expect fun ByteBuffer.reset(): ByteBuffer

/**
 * Clears this buffer.
 *
 * @return This buffer.
 */
expect fun ByteBuffer.clear(): ByteBuffer

/**
 * Flips this buffer.
 *
 * @return This buffer.
 */
expect fun ByteBuffer.flip(): ByteBuffer

/**
 * Rewinds this buffer.
 *
 * @return This buffer.
 */
expect fun ByteBuffer.rewind(): ByteBuffer

/**
 * Allocates a new direct byte buffer with off-heap storage.
 *
 * ### Buffer Initial State:
 * - Position: `0`
 * - Limit/Capacity: [capacity]
 * - Mark: `undefined`
 * - Byte order: [BigEndianOrder]
 * - Backing array: May not exist (check via [ByteBuffer.hasArray])
 * - Elements: Zero-initialized
 *
 * @param capacity Buffer storage capacity in bytes (must be ≥ 0)
 * @throws IllegalArgumentException If [capacity] is negative
 */
expect fun allocateDirectByteBuffer(capacity: Int): ByteBuffer

/**
 * Allocates a new heap-backed byte buffer.
 *
 * ### Buffer Initial State:
 * - Storage: Heap-allocated byte array
 * - Position: `0`
 * - Limit/Capacity: [capacity]
 * - Byte order: [BigEndianOrder]
 * - Backing array: Present ([ByteBuffer.array] available)
 * - Array offset: `0`
 * - Elements: Zero-initialized
 *
 * @param capacity Buffer storage capacity in bytes (must be ≥ 0)
 * @throws IllegalArgumentException If [capacity] is negative
 */
expect fun allocateHeapByteBuffer(capacity: Int): ByteBuffer

/**
 * Wraps a sub-range of this byte array into a heap buffer.
 *
 * ### Buffer Initial State:
 * - Backing storage: This array (modifications are bidirectional)
 * - Position: [offset]
 * - Limit: [offset] + [length]
 * - Capacity: [size]
 * - Byte order: [BigEndianOrder]
 * - Array offset: `0`
 *
 * ### Parameter Constraints:
 * - `0 ≤ offset ≤ [size]`
 * - `0 ≤ length ≤ [size] - offset`
 *
 * @throws IndexOutOfBoundsException If [offset] or [length] violates constraints
 * @see [ByteArray.wrapInHeapByteBuffer] For full-array wrapping
 */
expect fun ByteArray.wrapInHeapByteBuffer(offset: Int, length: Int): ByteBuffer

/**
 * Wraps the entire byte array into a heap buffer (equivalent to `wrapInHeapByteBuffer(0, size)`).
 *
 * ### Buffer Initial State:
 * - Backing storage: This array (modifications are bidirectional)
 * - Position: `0`
 * - Limit/Capacity: [size]
 * - Byte order: [BigEndianOrder]
 * - Array offset: `0`
 */
expect fun ByteArray.wrapInHeapByteBuffer(): ByteBuffer
