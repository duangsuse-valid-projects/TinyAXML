package org.duangsuse.tinyaxml;

import java.util.Hashtable;

import org.duangsuse.tinyaxml.type.AttributeType;
import org.duangsuse.tinyaxml.type.ChunkType;

public class AxmlFile {
    public static class MagicMaps {
        static int[] CHUNK_TYPE_MAGICS = new int[] {
            0x0000, 0x0001, 0x0002, 0x0003, 0x0100, 0x0101, 0x0102, 0x0103, 0x0104, 0x017f, 0x0180, 0x0200, 0x0201, 0x0202
        };
        static ChunkType[] CHUNK_TYPE_TYPES = new ChunkType[] {
            ChunkType.TNUL, ChunkType.TSTRPOL,ChunkType.TTBL, ChunkType.TXTYPE, ChunkType.TXSTARTNS, ChunkType.TXENDNS, ChunkType.TXSTARTE,
            ChunkType.TXENDE, ChunkType.TXCDATA, ChunkType.TXLASTCHUNK, ChunkType.TRESMAP, ChunkType.TTPKG, ChunkType.TTTYPE, ChunkType.TTSPEC
        };
        public static Hashtable<Integer, ChunkType> CHUNK_TABLE;
        public static Hashtable<ChunkType, Integer> CHUNK_TABLE_REVERSE;

        static int[] ATTR_TYPE_MAGICS = new int[] {};
        static AttributeType[] ATTR_TYPE_TYPES = new AttributeType[] {};
        static Hashtable<Integer, AttributeType> ATTR_TABLE;
        static Hashtable<AttributeType, Integer> ATTR_TABLE_REVERSE;

        // Class initializer, initialize mappings
        static {
            if (CHUNK_TYPE_MAGICS.length != CHUNK_TYPE_TYPES.length || ATTR_TYPE_MAGICS.length != ATTR_TYPE_TYPES.length)
                Main.panic("Failed to initialize MagicMaps - mapping size not equal");
            int i = 0;
            CHUNK_TABLE = new Hashtable<>();
            CHUNK_TABLE_REVERSE = new Hashtable<>();
            for (int m:CHUNK_TYPE_MAGICS)
                CHUNK_TABLE.put(m, CHUNK_TYPE_TYPES[i++]);
            i = 0; // reset counter
            for (ChunkType ct:CHUNK_TYPE_TYPES)
                CHUNK_TABLE_REVERSE.put(ct, CHUNK_TYPE_MAGICS[i++]);
        }
    }
    int fsize;

    public AxmlFile(byte[] bytes) {}

    public byte[] getBytes() {
        byte[] tmp = new byte[fsize];
        return tmp;
    }
}
