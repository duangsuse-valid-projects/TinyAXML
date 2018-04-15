package org.duangsuse.tinyaxml;

// AxmlFile parser class library

import java.util.Hashtable;

import org.duangsuse.tinyaxml.type.AttributeType;
import org.duangsuse.tinyaxml.type.ChunkType;

/**
 * Android AXML binary file class
 * <p> AxmlFile can serialize to byte[] and back
 * 
 * {@link https://github.com/rednaga/axmlprinter/tree/master/src/main/java/android/content/res/chunk} parser
 * {@link https://gist.github.com/duangsuse/3ae94e339eb188fa4ec8a87b6e105331} axml format documentation(in Chinese)
 * 
 * @since 1.0
 * @author duangsuse
 */
public class AxmlFile {
    /**
     * Axml binary magic codes
     * 
     * @author duangsuse
     * @since 1.0
     */
    public static class MagicMaps {
        /** Chunk type magics */
        static int[] CHUNK_TYPE_MAGICS = new int[] {};
        /** Chunk type enum types */
        static ChunkType[] CHUNK_TYPE_TYPES = new ChunkType[] {};
        /** magic to type mapping */
        public static Hashtable<Integer, ChunkType> CHUNK_TABLE;
        /** type to magic mapping */
        public static Hashtable<ChunkType, Integer> CHUNK_TABLE_REVERSE;

        /** Attribute type magics */
        static int[] ATTR_TYPE_MAGICS = new int[] {};
        /** Attribute type enum types */
        static AttributeType[] ATTR_TYPE_TYPES = new AttributeType[] {};
        /** magic to attr type enum mapping */
        public static Hashtable<Integer, AttributeType> ATTR_TABLE;
        /** attr type back to enum type mapping */
        public static Hashtable<AttributeType, Integer> ATTR_TABLE_REVERSE;

        // Class initializer, initialize mappings
        /** Initailize mappings */
        static {
            if (CHUNK_TYPE_MAGICS.length != CHUNK_TYPE_TYPES.length || ATTR_TYPE_MAGICS.length != ATTR_TYPE_TYPES.length)
                Main.panic("Failed to initialize MagicMaps - mapping size not equal");
            int i = 0;
            // initialize mappings
            CHUNK_TABLE = new Hashtable<>();
            CHUNK_TABLE_REVERSE = new Hashtable<>();
            // ..for attr mapping
            ATTR_TABLE = new Hashtable<>();
            ATTR_TABLE_REVERSE = new Hashtable<>();
            // create mappings
            for (int m:CHUNK_TYPE_MAGICS)
                CHUNK_TABLE.put(m, CHUNK_TYPE_TYPES[i++]);
            i = 0; // reset counter
            for (ChunkType ct:CHUNK_TYPE_TYPES)
                CHUNK_TABLE_REVERSE.put(ct, CHUNK_TYPE_MAGICS[i++]);
            i = 0;
            for (int m:ATTR_TYPE_MAGICS)
                ATTR_TABLE.put(m, ATTR_TYPE_TYPES[i++]);
            i = 0; // reset counter
            for (AttributeType at:ATTR_TYPE_TYPES)
                ATTR_TABLE_REVERSE.put(at, ATTR_TYPE_MAGICS[i++]);
        }
    }
    /** AxmlFile magic */
    public int magic;
    /** AxmlFile header size */
    public int hsize;
    /** Chunk size, including header and body */
    public int fsize;

    /**
     * Constructor with byte[] input
     * 
     * @param bytes AXML file bytes
     * @author duangsuse
     * @since 1.0
     */
    public AxmlFile(byte[] bytes) {}

    /**
     * Constructs AxmlFile with {@code byte[]-reprsentation} of target AXML document
     * and a {@code compat} switch as an argument to the parser
     * 
     * @param input AXML file bytes
     * @param compat Try to parse the file even if it's not supported
     * @since 1.0
     */
    public AxmlFile(byte[] input, boolean compat) {}

    /**
     * Alias for constructor
     * 
     * @param f input axml bytes
     * @return axml object
     * @since 1.0
     */
    public static AxmlFile fromBytes(byte[] f) {
        return new AxmlFile(f);
    }

    /**
     * Serialize this AxmlFile object to a sequence of byte
     * 
     * @return serialized bytes, should be in a valid AXML format
     * @author duangsuse
     * @since 1.0
     */
    public byte[] getBytes() {
        byte[] tmp = new byte[fsize];
        return tmp;
    }

    /** Alias for getBytes()
     * @see getBytes() */
    public byte[] toBytes() { return getBytes(); }
}
