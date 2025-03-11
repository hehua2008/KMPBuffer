package com.hym.kmpbuffer

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * @author hehua2008
 * @date 2025/3/6
 */
class HeapByteBufferTest {
    @Test
    fun testReadmeExample() {
        val buffer = allocateHeapByteBuffer(1024)

        buffer.putInt(42)
        buffer.putDouble(3.14159)
        val strBytes = "Hello".encodeToByteArray()
        buffer.put(strBytes, 0, strBytes.size)

        buffer.flip()

        val intValue = buffer.getInt()
        val doubleValue = buffer.getDouble()
        val byteArray = ByteArray(buffer.remaining()) { buffer.get() }
        val str = byteArray.decodeToString()

        assertEquals(42, intValue)
        assertEquals(3.14159, doubleValue)
        assertEquals("Hello", str)
    }

    @Test
    fun testAllocate() {
        val buffer = HeapByteBuffer.allocate(10)
        assertEquals(10, buffer.capacity())
        assertEquals(0, buffer.position())
        assertEquals(10, buffer.limit())
    }

    @Test
    fun testWrap() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(5, buffer.capacity())
        assertEquals(0, buffer.position())
        assertEquals(5, buffer.limit())
    }

    @Test
    fun testWrapWithOffset() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val buffer = HeapByteBuffer.wrap(array, 1, 3)
        assertEquals(5, buffer.capacity())
        assertEquals(1, buffer.position())
        assertEquals(4, buffer.limit())
    }

    @Test
    fun testPosition() {
        val buffer = HeapByteBuffer.allocate(10)
        buffer.position(5)
        assertEquals(5, buffer.position())
    }

    @Test
    fun testLimit() {
        val buffer = HeapByteBuffer.allocate(10)
        buffer.limit(5)
        assertEquals(5, buffer.limit())
    }

    @Test
    fun testMarkAndReset() {
        val buffer = HeapByteBuffer.allocate(10)
        buffer.position(5)
        buffer.mark()
        buffer.position(8)
        buffer.reset()
        assertEquals(5, buffer.position())
    }

    @Test
    fun testClear() {
        val buffer = HeapByteBuffer.allocate(10)
        buffer.position(5)
        buffer.limit(8)
        buffer.clear()
        assertEquals(0, buffer.position())
        assertEquals(10, buffer.limit())
    }

    @Test
    fun testFlip() {
        val buffer = HeapByteBuffer.allocate(10)
        buffer.position(5)
        buffer.flip()
        assertEquals(0, buffer.position())
        assertEquals(5, buffer.limit())
    }

    @Test
    fun testRewind() {
        val buffer = HeapByteBuffer.allocate(10)
        buffer.position(5)
        buffer.rewind()
        assertEquals(0, buffer.position())
    }

    @Test
    fun testRemaining() {
        val buffer = HeapByteBuffer.allocate(10)
        buffer.position(5)
        assertEquals(5, buffer.remaining())
    }

    @Test
    fun testHasRemaining() {
        val buffer = HeapByteBuffer.allocate(10)
        buffer.position(5)
        assertTrue(buffer.hasRemaining())
        buffer.position(10)
        assertFalse(buffer.hasRemaining())
    }

    @Test
    fun testGet() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(1, buffer.get())
        assertEquals(2, buffer.get())
    }

    @Test
    fun testPut() {
        val buffer = HeapByteBuffer.allocate(10)
        buffer.put(1)
        buffer.put(2)
        buffer.flip()
        assertEquals(1, buffer.get())
        assertEquals(2, buffer.get())
    }

    @Test
    fun testGetWithIndex() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(3, buffer.get(2))
    }

    @Test
    fun testPutWithIndex() {
        val buffer = HeapByteBuffer.allocate(10)
        buffer.put(2, 3)
        assertEquals(3, buffer.get(2))
    }

    @Test
    fun testSlice() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val buffer = HeapByteBuffer.wrap(array)
        buffer.position(1)
        val slice = buffer.slice()
        assertEquals(4, slice.capacity())
        assertEquals(0, slice.position())
        assertEquals(4, slice.limit())
    }

    @Test
    fun testDuplicate() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val buffer = HeapByteBuffer.wrap(array)
        val duplicate = buffer.duplicate()
        assertEquals(buffer.capacity(), duplicate.capacity())
        assertEquals(buffer.position(), duplicate.position())
        assertEquals(buffer.limit(), duplicate.limit())
    }

    @Test
    fun testAsReadOnlyBuffer() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val buffer = HeapByteBuffer.wrap(array)
        val readOnlyBuffer = buffer.asReadOnlyBuffer()
        assertTrue(readOnlyBuffer.isReadOnly())
    }

    @Test
    fun testCompact() {
        val buffer = HeapByteBuffer.allocate(10)
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
        val buffer = HeapByteBuffer.wrap(array)
        assertContentEquals(array, buffer.array())
    }

    @Test
    fun testArrayOffset() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val buffer = HeapByteBuffer.wrap(array, 1, 3)
        assertEquals(0, buffer.arrayOffset())
    }

    @Test
    fun testHasArray() {
        val buffer = HeapByteBuffer.allocate(10)
        assertTrue(buffer.hasArray())
    }

    @Test
    fun testIsDirect() {
        val buffer = HeapByteBuffer.allocate(10)
        assertFalse(buffer.isDirect())
    }

    @Test
    fun testToString() {
        val buffer = HeapByteBuffer.allocate(10)
        assertEquals("HeapByteBuffer[pos=0 lim=10 cap=10]", buffer.toString())
    }

    @Test
    fun testEquals() {
        val buffer1 = HeapByteBuffer.wrap(byteArrayOf(1, 2, 3, 4, 5))
        val buffer2 = HeapByteBuffer.wrap(byteArrayOf(1, 2, 3, 4, 5))
        assertEquals(buffer1, buffer2)
    }

    @Test
    fun testHashCode() {
        val buffer1 = HeapByteBuffer.wrap(byteArrayOf(1, 2, 3, 4, 5))
        val buffer2 = HeapByteBuffer.wrap(byteArrayOf(1, 2, 3, 4, 5))
        assertEquals(buffer1.hashCode(), buffer2.hashCode())
    }

    @Test
    fun testGetChar() {
        val array = byteArrayOf(0x00, 0x41)
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals('A', buffer.getChar())
    }

    @Test
    fun testPutChar() {
        val buffer = HeapByteBuffer.allocate(2)
        buffer.putChar('A')
        buffer.flip()
        assertEquals('A', buffer.getChar())
    }

    @Test
    fun testGetShort() {
        val array = byteArrayOf(0x00, 0x01)
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(1, buffer.getShort())
    }

    @Test
    fun testPutShort() {
        val buffer = HeapByteBuffer.allocate(2)
        buffer.putShort(1)
        buffer.flip()
        assertEquals(1, buffer.getShort())
    }

    @Test
    fun testGetInt() {
        val array = byteArrayOf(0x00, 0x00, 0x00, 0x01)
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(1, buffer.getInt())
    }

    @Test
    fun testPutInt() {
        val buffer = HeapByteBuffer.allocate(4)
        buffer.putInt(1)
        buffer.flip()
        assertEquals(1, buffer.getInt())
    }

    @Test
    fun testGetLong() {
        val array = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01)
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(1L, buffer.getLong())
    }

    @Test
    fun testPutLong() {
        val buffer = HeapByteBuffer.allocate(8)
        buffer.putLong(1L)
        buffer.flip()
        assertEquals(1L, buffer.getLong())
    }

    @Test
    fun testGetFloat() {
        val array = byteArrayOf(0x3F, 0x80.toByte(), 0x00, 0x00)
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(1.0f, buffer.getFloat())
    }

    @Test
    fun testPutFloat() {
        val buffer = HeapByteBuffer.allocate(4)
        buffer.putFloat(1.0f)
        buffer.flip()
        assertEquals(1.0f, buffer.getFloat())
    }

    @Test
    fun testGetDouble() {
        val array = byteArrayOf(0x3F, 0xF0.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(1.0, buffer.getDouble())
    }

    @Test
    fun testPutDouble() {
        val buffer = HeapByteBuffer.allocate(8)
        buffer.putDouble(1.0)
        buffer.flip()
        assertEquals(1.0, buffer.getDouble())
    }

    @Test
    fun testGetCharWithIndex() {
        val array = byteArrayOf(0x00, 0x41, 0x00, 0x42)
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals('A', buffer.getChar(0))
        assertEquals('B', buffer.getChar(2))
    }

    @Test
    fun testPutCharWithIndex() {
        val buffer = HeapByteBuffer.allocate(4)
        buffer.putChar(0, 'A')
        buffer.putChar(2, 'B')
        assertEquals('A', buffer.getChar(0))
        assertEquals('B', buffer.getChar(2))
    }

    @Test
    fun testGetShortWithIndex() {
        val array = byteArrayOf(0x00, 0x01, 0x00, 0x02)
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(1, buffer.getShort(0))
        assertEquals(2, buffer.getShort(2))
    }

    @Test
    fun testPutShortWithIndex() {
        val buffer = HeapByteBuffer.allocate(4)
        buffer.putShort(0, 1)
        buffer.putShort(2, 2)
        assertEquals(1, buffer.getShort(0))
        assertEquals(2, buffer.getShort(2))
    }

    @Test
    fun testGetIntWithIndex() {
        val array = byteArrayOf(0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x02)
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(1, buffer.getInt(0))
        assertEquals(2, buffer.getInt(4))
    }

    @Test
    fun testPutIntWithIndex() {
        val buffer = HeapByteBuffer.allocate(8)
        buffer.putInt(0, 1)
        buffer.putInt(4, 2)
        assertEquals(1, buffer.getInt(0))
        assertEquals(2, buffer.getInt(4))
    }

    @Test
    fun testGetLongWithIndex() {
        val array =
            byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02)
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(1L, buffer.getLong(0))
        assertEquals(2L, buffer.getLong(8))
    }

    @Test
    fun testPutLongWithIndex() {
        val buffer = HeapByteBuffer.allocate(16)
        buffer.putLong(0, 1L)
        buffer.putLong(8, 2L)
        assertEquals(1L, buffer.getLong(0))
        assertEquals(2L, buffer.getLong(8))
    }

    @Test
    fun testGetFloatWithIndex() {
        val array = byteArrayOf(0x3F, 0x80.toByte(), 0x00, 0x00, 0x40, 0x00, 0x00, 0x00)
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(1.0f, buffer.getFloat(0))
        assertEquals(2.0f, buffer.getFloat(4))
    }

    @Test
    fun testPutFloatWithIndex() {
        val buffer = HeapByteBuffer.allocate(8)
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
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(1.0, buffer.getDouble(0))
        assertEquals(2.0, buffer.getDouble(8))
    }

    @Test
    fun testPutDoubleWithIndex() {
        val buffer = HeapByteBuffer.allocate(16)
        buffer.putDouble(0, 1.0)
        buffer.putDouble(8, 2.0)
        assertEquals(1.0, buffer.getDouble(0))
        assertEquals(2.0, buffer.getDouble(8))
    }

    @Test
    fun testReadOnlyBuffer() {
        val array = byteArrayOf(1, 2, 3, 4, 5)
        val buffer = HeapByteBuffer.wrap(array).asReadOnlyBuffer()
        assertTrue(buffer.isReadOnly())
        assertFailsWith<ReadOnlyBufferException> { buffer.put(0, 1) }
    }

    @Test
    fun testBufferUnderflowException() {
        val array = byteArrayOf(1, 2, 3)
        val buffer = HeapByteBuffer.wrap(array)
        buffer.position(3)
        assertFailsWith<BufferUnderflowException> { buffer.get() }
    }

    @Test
    fun testBufferOverflowException() {
        val buffer = HeapByteBuffer.allocate(2)
        buffer.put(1)
        buffer.put(2)
        assertFailsWith<BufferOverflowException> { buffer.put(3) }
    }

    @Test
    fun testReadOnlyBufferException() {
        val array = byteArrayOf(1, 2, 3)
        val buffer = HeapByteBuffer.wrap(array).asReadOnlyBuffer()
        assertFailsWith<ReadOnlyBufferException> { buffer.put(1) }
    }

    @Test
    fun testReadmeExampleLittleEndian() {
        val buffer = allocateHeapByteBuffer(1024).order(ByteOrder.LittleEndian)

        buffer.putInt(42)
        buffer.putDouble(3.14159)
        val strBytes = "Hello".encodeToByteArray()
        buffer.put(strBytes, 0, strBytes.size)

        buffer.flip()

        val intValue = buffer.getInt()
        val doubleValue = buffer.getDouble()
        val byteArray = ByteArray(buffer.remaining()) { buffer.get() }
        val str = byteArray.decodeToString()

        assertEquals(42, intValue)
        assertEquals(3.14159, doubleValue)
        assertEquals("Hello", str)
    }

    @Test
    fun testGetCharLittleEndian() {
        val array = byteArrayOf(0x41, 0x00)
        val buffer = HeapByteBuffer.wrap(array).order(ByteOrder.LittleEndian)
        assertEquals('A', buffer.getChar())
    }

    @Test
    fun testPutCharLittleEndian() {
        val buffer = HeapByteBuffer.allocate(2).order(ByteOrder.LittleEndian)
        buffer.putChar('A')
        buffer.flip()
        assertEquals('A', buffer.getChar())
    }

    @Test
    fun testGetShortLittleEndian() {
        val array = byteArrayOf(0x01, 0x00)
        val buffer = HeapByteBuffer.wrap(array).order(ByteOrder.LittleEndian)
        assertEquals(1, buffer.getShort())
    }

    @Test
    fun testPutShortLittleEndian() {
        val buffer = HeapByteBuffer.allocate(2).order(ByteOrder.LittleEndian)
        buffer.putShort(1)
        buffer.flip()
        assertEquals(1, buffer.getShort())
    }

    @Test
    fun testGetIntLittleEndian() {
        val array = byteArrayOf(0x01, 0x00, 0x00, 0x00)
        val buffer = HeapByteBuffer.wrap(array).order(ByteOrder.LittleEndian)
        assertEquals(1, buffer.getInt())
    }

    @Test
    fun testPutIntLittleEndian() {
        val buffer = HeapByteBuffer.allocate(4).order(ByteOrder.LittleEndian)
        buffer.putInt(1)
        buffer.flip()
        assertEquals(1, buffer.getInt())
    }

    @Test
    fun testGetLongLittleEndian() {
        val array = byteArrayOf(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
        val buffer = HeapByteBuffer.wrap(array).order(ByteOrder.LittleEndian)
        assertEquals(1L, buffer.getLong())
    }

    @Test
    fun testPutLongLittleEndian() {
        val buffer = HeapByteBuffer.allocate(8).order(ByteOrder.LittleEndian)
        buffer.putLong(1L)
        buffer.flip()
        assertEquals(1L, buffer.getLong())
    }

    @Test
    fun testGetFloatLittleEndian() {
        val array = byteArrayOf(0x00, 0x00, 0x80.toByte(), 0x3F)
        val buffer = HeapByteBuffer.wrap(array).order(ByteOrder.LittleEndian)
        assertEquals(1.0f, buffer.getFloat())
    }

    @Test
    fun testPutFloatLittleEndian() {
        val buffer = HeapByteBuffer.allocate(4).order(ByteOrder.LittleEndian)
        buffer.putFloat(1.0f)
        buffer.flip()
        assertEquals(1.0f, buffer.getFloat())
    }

    @Test
    fun testGetDoubleLittleEndian() {
        val array = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xF0.toByte(), 0x3F)
        val buffer = HeapByteBuffer.wrap(array).order(ByteOrder.LittleEndian)
        assertEquals(1.0, buffer.getDouble())
    }

    @Test
    fun testPutDoubleLittleEndian() {
        val buffer = HeapByteBuffer.allocate(8).order(ByteOrder.LittleEndian)
        buffer.putDouble(1.0)
        buffer.flip()
        assertEquals(1.0, buffer.getDouble())
    }

    @Test
    fun testGetCharWithIndexLittleEndian() {
        val array = byteArrayOf(0x41, 0x00, 0x42, 0x00)
        val buffer = HeapByteBuffer.wrap(array).order(ByteOrder.LittleEndian)
        assertEquals('A', buffer.getChar(0))
        assertEquals('B', buffer.getChar(2))
    }

    @Test
    fun testPutCharWithIndexLittleEndian() {
        val buffer = HeapByteBuffer.allocate(4).order(ByteOrder.LittleEndian)
        buffer.putChar(0, 'A')
        buffer.putChar(2, 'B')
        assertEquals('A', buffer.getChar(0))
        assertEquals('B', buffer.getChar(2))
    }

    @Test
    fun testGetShortWithIndexLittleEndian() {
        val array = byteArrayOf(0x01, 0x00, 0x02, 0x00)
        val buffer = HeapByteBuffer.wrap(array).order(ByteOrder.LittleEndian)
        assertEquals(1, buffer.getShort(0))
        assertEquals(2, buffer.getShort(2))
    }

    @Test
    fun testPutShortWithIndexLittleEndian() {
        val buffer = HeapByteBuffer.allocate(4).order(ByteOrder.LittleEndian)
        buffer.putShort(0, 1)
        buffer.putShort(2, 2)
        assertEquals(1, buffer.getShort(0))
        assertEquals(2, buffer.getShort(2))
    }

    @Test
    fun testGetIntWithIndexLittleEndian() {
        val array = byteArrayOf(0x01, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00)
        val buffer = HeapByteBuffer.wrap(array).order(ByteOrder.LittleEndian)
        assertEquals(1, buffer.getInt(0))
        assertEquals(2, buffer.getInt(4))
    }

    @Test
    fun testPutIntWithIndexLittleEndian() {
        val buffer = HeapByteBuffer.allocate(8).order(ByteOrder.LittleEndian)
        buffer.putInt(0, 1)
        buffer.putInt(4, 2)
        assertEquals(1, buffer.getInt(0))
        assertEquals(2, buffer.getInt(4))
    }

    @Test
    fun testGetLongWithIndexLittleEndian() {
        val array =
            byteArrayOf(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
        val buffer = HeapByteBuffer.wrap(array).order(ByteOrder.LittleEndian)
        assertEquals(1L, buffer.getLong(0))
        assertEquals(2L, buffer.getLong(8))
    }

    @Test
    fun testPutLongWithIndexLittleEndian() {
        val buffer = HeapByteBuffer.allocate(16).order(ByteOrder.LittleEndian)
        buffer.putLong(0, 1L)
        buffer.putLong(8, 2L)
        assertEquals(1L, buffer.getLong(0))
        assertEquals(2L, buffer.getLong(8))
    }

    @Test
    fun testGetFloatWithIndexLittleEndian() {
        val array = byteArrayOf(0x00, 0x00, 0x80.toByte(), 0x3F, 0x00, 0x00, 0x00, 0x40)
        val buffer = HeapByteBuffer.wrap(array).order(ByteOrder.LittleEndian)
        assertEquals(1.0f, buffer.getFloat(0))
        assertEquals(2.0f, buffer.getFloat(4))
    }

    @Test
    fun testPutFloatWithIndexLittleEndian() {
        val buffer = HeapByteBuffer.allocate(8).order(ByteOrder.LittleEndian)
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
        val buffer = HeapByteBuffer.wrap(array).order(ByteOrder.LittleEndian)
        assertEquals(1.0, buffer.getDouble(0))
        assertEquals(2.0, buffer.getDouble(8))
    }

    @Test
    fun testPutDoubleWithIndexLittleEndian() {
        val buffer = HeapByteBuffer.allocate(16).order(ByteOrder.LittleEndian)
        buffer.putDouble(0, 1.0)
        buffer.putDouble(8, 2.0)
        assertEquals(1.0, buffer.getDouble(0))
        assertEquals(2.0, buffer.getDouble(8))
    }

    // Negative value test cases
    @Test
    fun testGetNegativeShort() {
        val array = byteArrayOf(0xFF.toByte(), 0xFF.toByte())
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(-1, buffer.getShort())
    }

    @Test
    fun testPutNegativeShort() {
        val buffer = HeapByteBuffer.allocate(2)
        buffer.putShort(-1)
        buffer.flip()
        assertEquals(-1, buffer.getShort())
    }

    @Test
    fun testGetNegativeInt() {
        val array = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte())
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(-1, buffer.getInt())
    }

    @Test
    fun testPutNegativeInt() {
        val buffer = HeapByteBuffer.allocate(4)
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
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(-1L, buffer.getLong())
    }

    @Test
    fun testPutNegativeLong() {
        val buffer = HeapByteBuffer.allocate(8)
        buffer.putLong(-1L)
        buffer.flip()
        assertEquals(-1L, buffer.getLong())
    }

    @Test
    fun testGetNegativeFloat() {
        val array = byteArrayOf(0xBF.toByte(), 0x80.toByte(), 0x00, 0x00)
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(-1.0f, buffer.getFloat())
    }

    @Test
    fun testPutNegativeFloat() {
        val buffer = HeapByteBuffer.allocate(4)
        buffer.putFloat(-1.0f)
        buffer.flip()
        assertEquals(-1.0f, buffer.getFloat())
    }

    @Test
    fun testGetNegativeDouble() {
        val array = byteArrayOf(0xBF.toByte(), 0xF0.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
        val buffer = HeapByteBuffer.wrap(array)
        assertEquals(-1.0, buffer.getDouble())
    }

    @Test
    fun testPutNegativeDouble() {
        val buffer = HeapByteBuffer.allocate(8)
        buffer.putDouble(-1.0)
        buffer.flip()
        assertEquals(-1.0, buffer.getDouble())
    }
}
