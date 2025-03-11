[![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&metadataUrl=https://repo1.maven.org/maven2/io/github/hehua2008/kmpbuffer/maven-metadata.xml&style=for-the-badge)](https://repo.maven.apache.org/maven2/io/github/hehua2008/kmpbuffer)

# KMPBuffer - Kotlin Multiplatform ByteBuffer Implementation

A Kotlin Multiplatform library providing a cross-platform `ByteBuffer` implementation compatible with Java NIO semantics, supporting Android, iOS, JVM.

## Features

- **Multiplatform Support**: Write once, run on Android, iOS, JVM
- **NIO Compatibility**: Familiar API matching Java's `ByteBuffer` interface
- **Memory Efficiency**: 
  - Direct memory allocation (off-heap) support
  - Heap-backed buffers for managed memory
- **Endianness Control**: Configurable byte order (Big/Little Endian)
- **Buffer Operations**:
  - Absolute/relative get/put methods
  - Bulk transfer operations
  - Buffer slicing and duplication
  - Compact, flip, rewind, and clear operations
- **Type Support**: Primitive type handling (Char, Short, Int, Long, Float, Double, etc.)

## Installation

Add to your `build.gradle.kts`:

```kotlin
kotlin {
  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation("io.github.hehua2008:kmpbuffer:0.9.0")
      }
    }
  }
}
```

## Usage

### Basic Example

```kotlin
// Allocate a direct buffer with 1024 bytes capacity
val buffer = allocateDirectByteBuffer(1024)

buffer.putInt(42)
buffer.putDouble(3.14159)
val strBytes = "Hello".encodeToByteArray()
buffer.put(strBytes, 0, strBytes.size)

// Flip for reading
buffer.flip()

val intValue = buffer.getInt()
val doubleValue = buffer.getDouble()
val byteArray = ByteArray(buffer.remaining()) { buffer.get() }
val str = byteArray.decodeToString()

// Release native direct memory
buffer.release()
```

### Buffer Allocation

```kotlin
// Heap-backed buffer
val heapBuffer = allocateHeapByteBuffer(512)

// Direct memory buffer (platform-specific implementation)
val directBuffer = allocateDirectByteBuffer(1024)

// Wrap existing byte array
val wrappedBuffer = byteArrayOf(1,2,3,4).wrapInHeapByteBuffer()
```

### Platform-Specific Notes

| Platform    | Characteristics                                                      |
|-------------|----------------------------------------------------------------------|
| **JVM**     | Uses `java.nio.ByteBuffer` internally (NOTE: Java version <= 18)     |
| **Android** | Shares JVM implementation with heap and direct buffer support        |
| **iOS**     | Native implementation using `ByteArray` and `CArrayPointer<ByteVar>` |

## Building from Source

1. Clone the repository
2. Build with Gradle:
```bash
./gradlew assemble
```

## Contributing

Contributions welcome!

## License

Apache License 2.0 - See [LICENSE](LICENSE) for details
