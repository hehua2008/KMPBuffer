package com.hym.kmpbuffer

/**
 * @author hehua2008
 * @date 2025/3/6
 */
abstract class Buffer<T : Buffer<T>> {
    /**
     * The capacity of the buffer.
     */
    protected abstract val capacity: Int

    /**
     * Current position in the buffer.
     */
    protected abstract var position: Int

    /**
     * Limit of the buffer (the index of the first element that should not be read or written).
     */
    protected abstract var limit: Int

    /**
     * Mark position (used for reset).
     */
    protected abstract var mark: Int

    /**
     * Sets this buffer's position.
     *
     * @param newPosition The new position value; must be non-negative and no larger than the current limit.
     * @return This buffer.
     * @throws IllegalArgumentException If the preconditions on the parameter do not hold.
     */
    open fun position(newPosition: Int): T {
        if (newPosition < 0 || newPosition > limit) {
            throw IllegalArgumentException("Invalid position: $newPosition")
        }
        position = newPosition
        if (mark > position) mark = -1
        return this as T
    }

    /**
     * Sets this buffer's limit.
     *
     * @param newLimit The new limit value; must be non-negative and no larger than this buffer's capacity.
     * @return This buffer.
     * @throws IllegalArgumentException If the preconditions on the parameter do not hold.
     */
    open fun limit(newLimit: Int): T {
        if (newLimit < 0 || newLimit > capacity) {
            throw IllegalArgumentException("Invalid limit: $newLimit")
        }
        limit = newLimit
        if (position > limit) position = limit
        if (mark > limit) mark = -1
        return this as T
    }

    /**
     * Marks the current position in this buffer.
     *
     * @return This buffer.
     */
    open fun mark(): T {
        mark = position
        return this as T
    }

    /**
     * Resets this buffer's position to the previously-marked position.
     *
     * @return This buffer.
     * @throws IllegalArgumentException If the mark has not been set.
     */
    open fun reset(): T {
        if (mark < 0) throw IllegalArgumentException("Invalid mark")
        position = mark
        return this as T
    }

    /**
     * Clears this buffer.
     *
     * @return This buffer.
     */
    open fun clear(): T {
        position = 0
        limit = capacity
        mark = -1
        return this as T
    }

    /**
     * Flips this buffer.
     *
     * @return This buffer.
     */
    open fun flip(): T {
        limit = position
        position = 0
        mark = -1
        return this as T
    }

    /**
     * Rewinds this buffer.
     *
     * @return This buffer.
     */
    open fun rewind(): T {
        position = 0
        mark = -1
        return this as T
    }
}
