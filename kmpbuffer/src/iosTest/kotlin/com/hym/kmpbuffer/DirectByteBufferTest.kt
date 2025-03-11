package com.hym.kmpbuffer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.free
import kotlinx.cinterop.nativeHeap

/**
 * @author hehua2008
 * @date 2025/3/6
 */
class DirectByteBufferTest {
    @Test
    fun testAllocate() {
        val buffer = DirectByteBuffer.allocate(10)
        assertEquals(10, buffer.capacity())
        assertEquals(0, buffer.position())
        assertEquals(10, buffer.limit())
    }

    @Test
    fun testWrap() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(5, buffer.capacity())
        assertEquals(0, buffer.position())
        assertEquals(5, buffer.limit())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testWrapWithOffset() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size, 1, 3)
        assertEquals(5, buffer.capacity())
        assertEquals(1, buffer.position())
        assertEquals(4, buffer.limit())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPosition() {
        val buffer = DirectByteBuffer.allocate(10)
        buffer.position(5)
        assertEquals(5, buffer.position())
    }

    @Test
    fun testLimit() {
        val buffer = DirectByteBuffer.allocate(10)
        buffer.limit(5)
        assertEquals(5, buffer.limit())
    }

    @Test
    fun testMarkAndReset() {
        val buffer = DirectByteBuffer.allocate(10)
        buffer.position(5)
        buffer.mark()
        buffer.position(8)
        buffer.reset()
        assertEquals(5, buffer.position())
    }

    @Test
    fun testClear() {
        val buffer = DirectByteBuffer.allocate(10)
        buffer.position(5)
        buffer.limit(8)
        buffer.clear()
        assertEquals(0, buffer.position())
        assertEquals(10, buffer.limit())
    }

    @Test
    fun testFlip() {
        val buffer = DirectByteBuffer.allocate(10)
        buffer.position(5)
        buffer.flip()
        assertEquals(0, buffer.position())
        assertEquals(5, buffer.limit())
    }

    @Test
    fun testRewind() {
        val buffer = DirectByteBuffer.allocate(10)
        buffer.position(5)
        buffer.rewind()
        assertEquals(0, buffer.position())
    }

    @Test
    fun testRemaining() {
        val buffer = DirectByteBuffer.allocate(10)
        buffer.position(5)
        assertEquals(5, buffer.remaining())
    }

    @Test
    fun testHasRemaining() {
        val buffer = DirectByteBuffer.allocate(10)
        buffer.position(5)
        assertTrue(buffer.hasRemaining())
        buffer.position(10)
        assertFalse(buffer.hasRemaining())
    }

    @Test
    fun testGet() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(1, buffer.get())
        assertEquals(2, buffer.get())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPut() {
        val buffer = DirectByteBuffer.allocate(10)
        buffer.put(1)
        buffer.put(2)
        buffer.flip()
        assertEquals(1, buffer.get())
        assertEquals(2, buffer.get())
    }

    @Test
    fun testGetWithIndex() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(3, buffer.get(2))
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutWithIndex() {
        val buffer = DirectByteBuffer.allocate(10)
        buffer.put(2, 3)
        assertEquals(3, buffer.get(2))
    }

    @Test
    fun testSlice() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        buffer.position(1)
        val slice = buffer.slice()
        assertEquals(4, slice.capacity())
        assertEquals(0, slice.position())
        assertEquals(4, slice.limit())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testDuplicate() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        val duplicate = buffer.duplicate()
        assertEquals(buffer.capacity(), duplicate.capacity())
        assertEquals(buffer.position(), duplicate.position())
        assertEquals(buffer.limit(), duplicate.limit())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testAsReadOnlyBuffer() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        val readOnlyBuffer = buffer.asReadOnlyBuffer()
        assertTrue(readOnlyBuffer.isReadOnly())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testCompact() {
        val buffer = DirectByteBuffer.allocate(10)
        buffer.put(1)
        buffer.put(2)
        buffer.put(3)
        buffer.flip()
        buffer.get()
        buffer.compact()
        assertEquals(2, buffer.position())
        assertEquals(10, buffer.limit())
    }

    @Test
    fun testArray() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertFailsWith<UnsupportedOperationException> { buffer.array() }
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testArrayOffset() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size, 1, 3)
        assertFailsWith<UnsupportedOperationException> { buffer.arrayOffset() }
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testHasArray() {
        val buffer = DirectByteBuffer.allocate(10)
        assertFalse(buffer.hasArray())
    }

    @Test
    fun testIsDirect() {
        val buffer = DirectByteBuffer.allocate(10)
        assertTrue(buffer.isDirect())
    }

    @Test
    fun testToString() {
        val buffer = DirectByteBuffer.allocate(10)
        assertEquals("DirectByteBuffer[pos=0 lim=10 cap=10]", buffer.toString())
    }

    @Test
    fun testEquals() {
        val array1 = byteArrayOf(1, 2, 3, 4, 5)
        val array2 = byteArrayOf(1, 2, 3, 4, 5)
        val cArrayPointer1 = nativeHeap.allocArrayOf(array1)
        val cArrayPointer2 = nativeHeap.allocArrayOf(array2)
        val buffer1 = DirectByteBuffer.wrap(cArrayPointer1, array1.size)
        val buffer2 = DirectByteBuffer.wrap(cArrayPointer2, array2.size)
        assertEquals(buffer1, buffer2)
        nativeHeap.free(cArrayPointer1)
        nativeHeap.free(cArrayPointer2)
    }

    @Test
    fun testHashCode() {
        val array1 = byteArrayOf(1, 2, 3, 4, 5)
        val array2 = byteArrayOf(1, 2, 3, 4, 5)
        val cArrayPointer1 = nativeHeap.allocArrayOf(array1)
        val cArrayPointer2 = nativeHeap.allocArrayOf(array2)
        val buffer1 = DirectByteBuffer.wrap(cArrayPointer1, array1.size)
        val buffer2 = DirectByteBuffer.wrap(cArrayPointer2, array2.size)
        assertEquals(buffer1.hashCode(), buffer2.hashCode())
        nativeHeap.free(cArrayPointer1)
        nativeHeap.free(cArrayPointer2)
    }

    @Test
    fun testGetChar() {
        val array = byteArrayOf(0x00, 0x41)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals('A', buffer.getChar())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutChar() {
        val buffer = DirectByteBuffer.allocate(2)
        buffer.putChar('A')
        buffer.flip()
        assertEquals('A', buffer.getChar())
    }

    @Test
    fun testGetShort() {
        val array = byteArrayOf(0x00, 0x01)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(1, buffer.getShort())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutShort() {
        val buffer = DirectByteBuffer.allocate(2)
        buffer.putShort(1)
        buffer.flip()
        assertEquals(1, buffer.getShort())
    }

    @Test
    fun testGetInt() {
        val array = byteArrayOf(0x00, 0x00, 0x00, 0x01)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(1, buffer.getInt())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutInt() {
        val buffer = DirectByteBuffer.allocate(4)
        buffer.putInt(1)
        buffer.flip()
        assertEquals(1, buffer.getInt())
    }

    @Test
    fun testGetLong() {
        val array = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(1L, buffer.getLong())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutLong() {
        val buffer = DirectByteBuffer.allocate(8)
        buffer.putLong(1L)
        buffer.flip()
        assertEquals(1L, buffer.getLong())
    }

    @Test
    fun testGetFloat() {
        val array = byteArrayOf(0x3F, 0x80.toByte(), 0x00, 0x00)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(1.0f, buffer.getFloat())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutFloat() {
        val buffer = DirectByteBuffer.allocate(4)
        buffer.putFloat(1.0f)
        buffer.flip()
        assertEquals(1.0f, buffer.getFloat())
    }

    @Test
    fun testGetDouble() {
        val array = byteArrayOf(0x3F, 0xF0.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(1.0, buffer.getDouble())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutDouble() {
        val buffer = DirectByteBuffer.allocate(8)
        buffer.putDouble(1.0)
        buffer.flip()
        assertEquals(1.0, buffer.getDouble())
    }

    @Test
    fun testGetCharWithIndex() {
        val array = byteArrayOf(0x00, 0x41, 0x00, 0x42)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals('A', buffer.getChar(0))
        assertEquals('B', buffer.getChar(2))
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutCharWithIndex() {
        val buffer = DirectByteBuffer.allocate(4)
        buffer.putChar(0, 'A')
        buffer.putChar(2, 'B')
        assertEquals('A', buffer.getChar(0))
        assertEquals('B', buffer.getChar(2))
    }

    @Test
    fun testGetShortWithIndex() {
        val array = byteArrayOf(0x00, 0x01, 0x00, 0x02)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(1, buffer.getShort(0))
        assertEquals(2, buffer.getShort(2))
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutShortWithIndex() {
        val buffer = DirectByteBuffer.allocate(4)
        buffer.putShort(0, 1)
        buffer.putShort(2, 2)
        assertEquals(1, buffer.getShort(0))
        assertEquals(2, buffer.getShort(2))
    }

    @Test
    fun testGetIntWithIndex() {
        val array = byteArrayOf(0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x02)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(1, buffer.getInt(0))
        assertEquals(2, buffer.getInt(4))
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutIntWithIndex() {
        val buffer = DirectByteBuffer.allocate(8)
        buffer.putInt(0, 1)
        buffer.putInt(4, 2)
        assertEquals(1, buffer.getInt(0))
        assertEquals(2, buffer.getInt(4))
    }

    @Test
    fun testGetLongWithIndex() {
        val array =
            byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(1L, buffer.getLong(0))
        assertEquals(2L, buffer.getLong(8))
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutLongWithIndex() {
        val buffer = DirectByteBuffer.allocate(16)
        buffer.putLong(0, 1L)
        buffer.putLong(8, 2L)
        assertEquals(1L, buffer.getLong(0))
        assertEquals(2L, buffer.getLong(8))
    }

    @Test
    fun testGetFloatWithIndex() {
        val array = byteArrayOf(0x3F, 0x80.toByte(), 0x00, 0x00, 0x40, 0x00, 0x00, 0x00)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(1.0f, buffer.getFloat(0))
        assertEquals(2.0f, buffer.getFloat(4))
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutFloatWithIndex() {
        val buffer = DirectByteBuffer.allocate(8)
        buffer.putFloat(0, 1.0f)
        buffer.putFloat(4, 2.0f)
        assertEquals(1.0f, buffer.getFloat(0))
        assertEquals(2.0f, buffer.getFloat(4))
    }

    @Test
    fun testGetDoubleWithIndex() {
        val array = byteArrayOf(
            0x3F, 0xF0.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        )
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(1.0, buffer.getDouble(0))
        assertEquals(2.0, buffer.getDouble(8))
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutDoubleWithIndex() {
        val buffer = DirectByteBuffer.allocate(16)
        buffer.putDouble(0, 1.0)
        buffer.putDouble(8, 2.0)
        assertEquals(1.0, buffer.getDouble(0))
        assertEquals(2.0, buffer.getDouble(8))
    }

    @Test
    fun testReadOnlyBuffer() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size).asReadOnlyBuffer()
        assertTrue(buffer.isReadOnly())
        assertFailsWith<ReadOnlyBufferException> { buffer.put(0, 1) }
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testBufferUnderflowException() {
        val array = byteArrayOf(1, 2, 3)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        buffer.position(3)
        assertFailsWith<BufferUnderflowException> { buffer.get() }
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testBufferOverflowException() {
        val buffer = DirectByteBuffer.allocate(2)
        buffer.put(1)
        buffer.put(2)
        assertFailsWith<BufferOverflowException> { buffer.put(3) }
    }

    @Test
    fun testReadOnlyBufferException() {
        val array = byteArrayOf(1, 2, 3)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size).asReadOnlyBuffer()
        assertFailsWith<ReadOnlyBufferException> { buffer.put(1) }
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testGetCharLittleEndian() {
        val array = byteArrayOf(0x41, 0x00)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size).order(ByteOrder.LittleEndian)
        assertEquals('A', buffer.getChar())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutCharLittleEndian() {
        val buffer = DirectByteBuffer.allocate(2).order(ByteOrder.LittleEndian)
        buffer.putChar('A')
        buffer.flip()
        assertEquals('A', buffer.getChar())
    }

    @Test
    fun testGetShortLittleEndian() {
        val array = byteArrayOf(0x01, 0x00)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size).order(ByteOrder.LittleEndian)
        assertEquals(1, buffer.getShort())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutShortLittleEndian() {
        val buffer = DirectByteBuffer.allocate(2).order(ByteOrder.LittleEndian)
        buffer.putShort(1)
        buffer.flip()
        assertEquals(1, buffer.getShort())
    }

    @Test
    fun testGetIntLittleEndian() {
        val array = byteArrayOf(0x01, 0x00, 0x00, 0x00)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size).order(ByteOrder.LittleEndian)
        assertEquals(1, buffer.getInt())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutIntLittleEndian() {
        val buffer = DirectByteBuffer.allocate(4).order(ByteOrder.LittleEndian)
        buffer.putInt(1)
        buffer.flip()
        assertEquals(1, buffer.getInt())
    }

    @Test
    fun testGetLongLittleEndian() {
        val array = byteArrayOf(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size).order(ByteOrder.LittleEndian)
        assertEquals(1L, buffer.getLong())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutLongLittleEndian() {
        val buffer = DirectByteBuffer.allocate(8).order(ByteOrder.LittleEndian)
        buffer.putLong(1L)
        buffer.flip()
        assertEquals(1L, buffer.getLong())
    }

    @Test
    fun testGetFloatLittleEndian() {
        val array = byteArrayOf(0x00, 0x00, 0x80.toByte(), 0x3F)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size).order(ByteOrder.LittleEndian)
        assertEquals(1.0f, buffer.getFloat())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutFloatLittleEndian() {
        val buffer = DirectByteBuffer.allocate(4).order(ByteOrder.LittleEndian)
        buffer.putFloat(1.0f)
        buffer.flip()
        assertEquals(1.0f, buffer.getFloat())
    }

    @Test
    fun testGetDoubleLittleEndian() {
        val array = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xF0.toByte(), 0x3F)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size).order(ByteOrder.LittleEndian)
        assertEquals(1.0, buffer.getDouble())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutDoubleLittleEndian() {
        val buffer = DirectByteBuffer.allocate(8).order(ByteOrder.LittleEndian)
        buffer.putDouble(1.0)
        buffer.flip()
        assertEquals(1.0, buffer.getDouble())
    }

    @Test
    fun testGetCharWithIndexLittleEndian() {
        val array = byteArrayOf(0x41, 0x00, 0x42, 0x00)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size).order(ByteOrder.LittleEndian)
        assertEquals('A', buffer.getChar(0))
        assertEquals('B', buffer.getChar(2))
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutCharWithIndexLittleEndian() {
        val buffer = DirectByteBuffer.allocate(4).order(ByteOrder.LittleEndian)
        buffer.putChar(0, 'A')
        buffer.putChar(2, 'B')
        assertEquals('A', buffer.getChar(0))
        assertEquals('B', buffer.getChar(2))
    }

    @Test
    fun testGetShortWithIndexLittleEndian() {
        val array = byteArrayOf(0x01, 0x00, 0x02, 0x00)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size).order(ByteOrder.LittleEndian)
        assertEquals(1, buffer.getShort(0))
        assertEquals(2, buffer.getShort(2))
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutShortWithIndexLittleEndian() {
        val buffer = DirectByteBuffer.allocate(4).order(ByteOrder.LittleEndian)
        buffer.putShort(0, 1)
        buffer.putShort(2, 2)
        assertEquals(1, buffer.getShort(0))
        assertEquals(2, buffer.getShort(2))
    }

    @Test
    fun testGetIntWithIndexLittleEndian() {
        val array = byteArrayOf(0x01, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size).order(ByteOrder.LittleEndian)
        assertEquals(1, buffer.getInt(0))
        assertEquals(2, buffer.getInt(4))
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutIntWithIndexLittleEndian() {
        val buffer = DirectByteBuffer.allocate(8).order(ByteOrder.LittleEndian)
        buffer.putInt(0, 1)
        buffer.putInt(4, 2)
        assertEquals(1, buffer.getInt(0))
        assertEquals(2, buffer.getInt(4))
    }

    @Test
    fun testGetLongWithIndexLittleEndian() {
        val array =
            byteArrayOf(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size).order(ByteOrder.LittleEndian)
        assertEquals(1L, buffer.getLong(0))
        assertEquals(2L, buffer.getLong(8))
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutLongWithIndexLittleEndian() {
        val buffer = DirectByteBuffer.allocate(16).order(ByteOrder.LittleEndian)
        buffer.putLong(0, 1L)
        buffer.putLong(8, 2L)
        assertEquals(1L, buffer.getLong(0))
        assertEquals(2L, buffer.getLong(8))
    }

    @Test
    fun testGetFloatWithIndexLittleEndian() {
        val array = byteArrayOf(0x00, 0x00, 0x80.toByte(), 0x3F, 0x00, 0x00, 0x00, 0x40)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size).order(ByteOrder.LittleEndian)
        assertEquals(1.0f, buffer.getFloat(0))
        assertEquals(2.0f, buffer.getFloat(4))
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutFloatWithIndexLittleEndian() {
        val buffer = DirectByteBuffer.allocate(8).order(ByteOrder.LittleEndian)
        buffer.putFloat(0, 1.0f)
        buffer.putFloat(4, 2.0f)
        assertEquals(1.0f, buffer.getFloat(0))
        assertEquals(2.0f, buffer.getFloat(4))
    }

    @Test
    fun testGetDoubleWithIndexLittleEndian() {
        val array = byteArrayOf(
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xF0.toByte(), 0x3F, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x40
        )
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size).order(ByteOrder.LittleEndian)
        assertEquals(1.0, buffer.getDouble(0))
        assertEquals(2.0, buffer.getDouble(8))
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutDoubleWithIndexLittleEndian() {
        val buffer = DirectByteBuffer.allocate(16).order(ByteOrder.LittleEndian)
        buffer.putDouble(0, 1.0)
        buffer.putDouble(8, 2.0)
        assertEquals(1.0, buffer.getDouble(0))
        assertEquals(2.0, buffer.getDouble(8))
    }

    // Negative value test cases
    @Test
    fun testGetNegativeShort() {
        val array = byteArrayOf(0xFF.toByte(), 0xFF.toByte())
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(-1, buffer.getShort())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutNegativeShort() {
        val buffer = DirectByteBuffer.allocate(2)
        buffer.putShort(-1)
        buffer.flip()
        assertEquals(-1, buffer.getShort())
    }

    @Test
    fun testGetNegativeInt() {
        val array = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte())
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(-1, buffer.getInt())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutNegativeInt() {
        val buffer = DirectByteBuffer.allocate(4)
        buffer.putInt(-1)
        buffer.flip()
        assertEquals(-1, buffer.getInt())
    }

    @Test
    fun testGetNegativeLong() {
        val array = byteArrayOf(
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte()
        )
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(-1L, buffer.getLong())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutNegativeLong() {
        val buffer = DirectByteBuffer.allocate(8)
        buffer.putLong(-1L)
        buffer.flip()
        assertEquals(-1L, buffer.getLong())
    }

    @Test
    fun testGetNegativeFloat() {
        val array = byteArrayOf(0xBF.toByte(), 0x80.toByte(), 0x00, 0x00)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(-1.0f, buffer.getFloat())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutNegativeFloat() {
        val buffer = DirectByteBuffer.allocate(4)
        buffer.putFloat(-1.0f)
        buffer.flip()
        assertEquals(-1.0f, buffer.getFloat())
    }

    @Test
    fun testGetNegativeDouble() {
        val array = byteArrayOf(0xBF.toByte(), 0xF0.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
        val cArrayPointer = nativeHeap.allocArrayOf(array)
        val buffer = DirectByteBuffer.wrap(cArrayPointer, array.size)
        assertEquals(-1.0, buffer.getDouble())
        nativeHeap.free(cArrayPointer)
    }

    @Test
    fun testPutNegativeDouble() {
        val buffer = DirectByteBuffer.allocate(8)
        buffer.putDouble(-1.0)
        buffer.flip()
        assertEquals(-1.0, buffer.getDouble())
    }
}
