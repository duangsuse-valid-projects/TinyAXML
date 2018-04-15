package org.duangsuse.tinyaxml; // duangsuse <3 duangsuse XD

// WORD&binary <3 duangsuse

/**
 * An Serializable and deserializable AXML Chunk
 * <p> with chunk (header)size, chunk magic, chunk body.
 * 
 * @author duangsuse
 * {@link https://gist.github.com/duangsuse/3ae94e339eb188fa4ec8a87b6e105331}
 */
public interface IChunk {
    /**
     * Gets the chunk size
     * 
     * @return chunk size
     * @since 1.0
     */
    int getSize();

    /**
     * Gets the chunk header size
     * 
     * @return chunk header size
     * @since 1.0
     */
    int getHeaderSize();

    /**
     * Gets the chunk raw magic
     * 
     * @return raw magic
     * @since 1.0
     */
    int getMagic();

    /**
     * Serialize to bytes
     * 
     * @return serialized byte array
     * @since 1.0
     */
    public byte[] toBytes();

    /**
     * Construct from bytes
     * <p> post-initialize chunk classes
     * <p> should be overriten by all classes that implements IChunk
     * 
     * @param bs chunk bytes including magic, size and body
     * @return constructed object
     * @since 1.0
     */
    public void fromBytes(byte[] bs);
}
