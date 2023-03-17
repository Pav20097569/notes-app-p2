package persistence

import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import models.Note
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * A serializer implementation that uses the CBOR serialization format for persistence.
 * @param file The file to read from/write to.
 */
class CBORSerializer(private val file: File) : Serializer {

    /**
     * Reads and deserializes data from the file.
     * @return The deserialized data.
     * @throws Exception If there is an error reading or deserializing the data.
     */
    @Throws(Exception::class)
    override fun read(): Any {
        // Create a FileInputStream for the file
        val fileInputStream = FileInputStream(file)
        // Use the Cbor serializer to decode the data from the file input stream
        val data = Cbor.decodeFromByteArray<Note>(fileInputStream.readBytes())
        // Close the input stream
        fileInputStream.close()
        // Return the deserialized data
        return data
    }

    /**
     * Serializes and writes data to the file.
     * @param obj The object to serialize and write.
     * @throws Exception If there is an error serializing or writing the data.
     */

    @Throws(Exception::class)
    override fun write(obj: Any?) {
        // Ensure that obj is a Note object
        if (obj !is Note) {
            throw IllegalArgumentException("Object must be of type Note")
        }
        // Create a FileOutputStream for the file
        val fileOutputStream = FileOutputStream(file)
        // Use the Cbor serializer to encode the data to a byte array
        val data = Cbor.encodeToByteArray(obj)
        // Write the encoded data to the output stream
        fileOutputStream.write(data)
        // Close the output stream
        fileOutputStream.close()
    }
}