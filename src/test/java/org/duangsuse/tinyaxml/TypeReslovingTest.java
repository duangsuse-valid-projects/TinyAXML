package org.duangsuse.tinyaxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.duangsuse.tinyaxml.AxmlFile;
import org.duangsuse.tinyaxml.type.AttributeType;
import org.duangsuse.tinyaxml.type.ChunkType;
import org.junit.Test; // <3

/**
 * Unit tests for {@code AttributeType} and {@code ChunkType} class
 * <p> Ensure type resloving functions works
 * 
 * @author duangsuse
 * @version 1.0
 * @see AxmlFile
 * @see ChunkType
 * @see AttributeType
 */
public class TypeReslovingTest {
    /**
     * Magic map initializes
     * @see AxmlFile.MagicMaps
     */
    @Test
    public void magicMapInitializes() {
        assertNotNull(AxmlFile.MagicMaps.ATTR_TABLE);
        assertNotNull(AxmlFile.MagicMaps.ATTR_TABLE_REVERSE);
        assertNotNull(AxmlFile.MagicMaps.CHUNK_TABLE);
        assertNotNull(AxmlFile.MagicMaps.CHUNK_TABLE_REVERSE);
    }

    /**
     * Magic map not none
     * 
     * @see AxmlFile.MagicMaps
     */
    @Test
    public void magicMapHasElements() {
        assertNotEquals(0, AxmlFile.MagicMaps.ATTR_TABLE.values().size());
        assertNotEquals(0, AxmlFile.MagicMaps.ATTR_TABLE_REVERSE.values().size());
        assertNotEquals(0, AxmlFile.MagicMaps.CHUNK_TABLE.values().size());
        assertNotEquals(0, AxmlFile.MagicMaps.CHUNK_TABLE_REVERSE.values().size());
    }

    /** Chunk magic class works */
    @Test
    public void chunkMagicWorks() {
        assertEquals(ChunkType.AXML, ChunkType.fromMagic(0x00080003));
        assertEquals(ChunkType.TEXT_TAG, ChunkType.fromMagic(0x00100104));
        assertEquals(ChunkType.RES_MAP, ChunkType.fromMagic(0x00080180));
        assertEquals(ChunkType.END_TAG, ChunkType.fromMagic(0x00100103));
    }

    /** Chunk magic enum -> int works */
    @Test
    public void chunkMagicReverseWorks() {
        assertEquals(0x00080003, ChunkType.AXML.toMagic());
        assertEquals(0x00100103, ChunkType.END_TAG.toMagic());
        assertEquals(0x00080180, ChunkType.RES_MAP.toMagic());
    }

    /** ChunkType get desc works */
    @Test
    public void chunkMagicDescWorks() {
        assert ChunkType.RES_MAP.getDescription().contains("Resource");
        assert ChunkType.TEXT_TAG.getDescription().contains("tag");
    }

    /** AttributeType fromMagic(int) works */
    @Test
    public void attrMagicWorks() {
        assertEquals(AttributeType.STR, AttributeType.fromMagic(0x03000008));
        assertEquals(AttributeType.COLOR2, AttributeType.fromMagic(0x1D000008));
        assertEquals(AttributeType.DIMEN, AttributeType.fromMagic(0x05000008));
    }

    /** AttributeType -> int works */
    @Test
    public void attrMagicReverseWorks() {
        assertEquals(0x03000008, AttributeType.STR.toMagic());
        assertEquals(0x1D000008, AttributeType.COLOR2.toMagic());
        assertEquals(0x04000008, AttributeType.FLOAT.toMagic());
    }

    /** attrMagic description is OK */
    @Test
    public void attrMagicDescWorks() {
        assert AttributeType.COLOR1.getDescription().contains("Color");
        assert AttributeType.STR.getDescription().contains("Str");
    }

    /** toString() for type enum is OK */
    @Test
    public void toStringWorks() {
        assertEquals(String.valueOf(ChunkType.RES_MAP), "Resource map");
        assertEquals(String.valueOf(AttributeType.BOOL), "Boolean");
    }
}
