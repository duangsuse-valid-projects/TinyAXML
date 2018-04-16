package org.duangsuse.tinyaxml;

// AxmlFile parser class library

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
// needed for mapping ChunkType and AttributeType
import java.util.Hashtable;
import java.util.ArrayList;

// creating magic maps in this class
import org.duangsuse.tinyaxml.type.AttributeType;
import org.duangsuse.tinyaxml.type.ChunkType;
import org.duangsuse.tinyaxml.type.Element;
import org.duangsuse.tinyaxml.type.ElementTree;

// chunks
import org.duangsuse.tinyaxml.chunk.*;
import org.duangsuse.tinyaxml.error.Errno;
import org.duangsuse.tinyaxml.error.ParseError;

// helper
import static org.duangsuse.tinyaxml.Main.int2Word;
import static org.duangsuse.tinyaxml.Main.word2Int;
import static org.duangsuse.tinyaxml.Main.cropAry;

/**
 * Android AXML binary file class
 * <p> AxmlFile can serialize to byte[] and back
 * 
 * https://github.com/rednaga/axmlprinter/tree/master/src/main/java/android/content/res/chunk parser
 * https://gist.github.com/duangsuse/3ae94e339eb188fa4ec8a87b6e105331 axml format documentation(in Chinese)
 * 
 * @since 1.0
 * @author duangsuse
 */
public class AxmlFile implements IChunk {
    /**
     * Axml binary magic codes
     * 
     * @author duangsuse
     * @since 1.0
     */
    public static class MagicMaps {
        /** Chunk type magics */
        static int[] CHUNK_TYPE_MAGICS = new int[] {
            0x00080003, 0x001C0001, 0x00080180, 0x00100100, 0x00100101, 0x00100102, 0x00100103, 0x00100104
        };
        /** Chunk type enum types */
        static ChunkType[] CHUNK_TYPE_TYPES = new ChunkType[] {
            ChunkType.AXML, ChunkType.STR_POOL, ChunkType.RES_MAP, ChunkType.START_NS, ChunkType.END_NS,
            ChunkType.START_TAG, ChunkType.END_TAG, ChunkType.TEXT_TAG
        };
        /** magic to type mapping */
        public static Hashtable<Integer, ChunkType> CHUNK_TABLE;
        /** type to magic mapping */
        public static Hashtable<ChunkType, Integer> CHUNK_TABLE_REVERSE;

        /** Attribute type magics */
        static int[] ATTR_TYPE_MAGICS = new int[] {
            0x03000008, 0x10000008, 0x01000008, 0x12000008, 0x02000008, 0x05000008, 0x06000008, 0x04000008, 0x11000008, 0x1C000008, 0x1D000008
        };
        /** Attribute type enum types */
        static AttributeType[] ATTR_TYPE_TYPES = new AttributeType[] {
            AttributeType.STR, AttributeType.INT, AttributeType.RESOURCE, AttributeType.BOOL, AttributeType.ATTR, AttributeType.DIMEN,
            AttributeType.FRACTION, AttributeType.FLOAT, AttributeType.FLAGS, AttributeType.COLOR1, AttributeType.COLOR2
        };
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
    /** AxmlFile header size(orginal) */
    public int hsize = 4;
    /** Chunk size, including header and body(orginal) */
    public int fsize;

    // chunk data
    public StringPool stringPool;
    public ResourceMap resMap;

    public StartNameSpace startNS;
    public EndNameSpace endNS;
    // contains startTag and endTag information
    public ArrayList<StartElement> startElements;
    public ArrayList<EndElement> endElements;

    public ArrayList<TextElement> texts;

    // quick link
    public ArrayList<Element> elements;
    // operation helper object
    public ElementTree xmltree;

    /**
     * Constructor with byte[] input
     * 
     * @param bytes AXML file bytes
     * @author duangsuse
     * @since 1.0
     */
    public AxmlFile(byte[] bytes) throws ParseError {
        this(bytes, false);
    }

    /**
     * Constructs AxmlFile with {@code byte[]-reprsentation} of target AXML document
     * and a {@code compat} switch as an argument to the parser
     * <p> This method contains call to real parser logic
     * 
     * @param input AXML file bytes
     * @param compat Try to parse the file even if it's not supported
     * @since 1.0
     */
    public AxmlFile(byte[] input, boolean compat) throws ParseError {
        // setup compat
        Main.tryCompat = compat;

        // initialize mappings
        stringPool = new StringPool();
        resMap = new ResourceMap();
        startNS = new StartNameSpace();
        startElements = new ArrayList<>();
        endElements = new ArrayList<>();
        texts = new ArrayList<>();

        // call parser
        fromBytes(input);

        // fill quick links
        for (int i = 0; i < startElements.size() - 1; i++) {
            Element e = new Element(startElements.get(i), endElements.get(i));
            elements.add(e);
        }
        xmltree = new ElementTree(elements); // initialize elementTree API
    }

    /**
     * Constructs an AxmlFile with given file
     * 
     * @param f the file
     * @since 1.0
     * @throws IOException readFile(File) throws an error
     */
    public AxmlFile(File f) throws IOException, ParseError {
        this(readFile(f));
    }

    /**
     * Reads target file, returning an array of byte
     * 
     * @param f target file
     * @return file bytes
     * @since 1.0
     * @throws IOException read/close error
     */
    public static byte[] readFile(File f) throws IOException {
        InputStream is = new FileInputStream(f);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        is.close();
        return buffer;
    }

    /** Blank constructor */
    public AxmlFile() {}

    /**
     * Constructs an AxmlFile with bytes in given file path
     * 
     * @param path file path
     * @since 1.0
     * @throws IOException AxmlFile(File) throws an error
     */
    public AxmlFile(String path) throws IOException, ParseError {
        this(new File(path));
    }

    /**
     * Main parser logic
     * 
     * @param f input axml bytes
     * @since 1.0
     */
    @Override
    public void fromBytes(byte[] f) throws ParseError {
        // begin logic
        // parse magic and size
        if (f.length < 8)
            throw new ParseError(Errno.EBADLEN);

        magic = word2Int(cropAry(f, 0, 3));
        if (magic != ChunkType.AXML.toMagic())
            if (!Main.tryCompat)
                throw new ParseError(Errno.EBADMAGIC);
        fsize = word2Int(cropAry(f, 4, 7)); // file size
        if (fsize != f.length)
            if (!Main.tryCompat)
                throw new ParseError(Errno.EBADLEN);

        int offset = 8; // chunks!
        while (offset <= f.length) {
            int chunk_type = word2Int(cropAry(f, offset, offset + 3)); // magic
            int chunk_size = word2Int(cropAry(f, offset + 4, offset + 4 + 3)); // size

            int spol_magic = ChunkType.STR_POOL.toMagic();
            int resmap_magic = ChunkType.RES_MAP.toMagic();
            int sns_magic = ChunkType.START_NS.toMagic();
            int stag_magic = ChunkType.START_TAG.toMagic();
            int etag_magic = ChunkType.END_TAG.toMagic();
            int ens_magic = ChunkType.END_NS.toMagic();
            int text_magic = ChunkType.TEXT_TAG.toMagic();

            byte[] bs = cropAry(f, offset, offset+ chunk_size);
            // if...
            if (chunk_type == spol_magic) {
                stringPool = new StringPool();
                stringPool.fromBytes(bs);
            } else if (chunk_type == resmap_magic) {
                resMap = new ResourceMap();
                resMap.fromBytes(bs);
            } else if (chunk_type == sns_magic) {
                startNS = new StartNameSpace();
                startNS.fromBytes(bs);
            } else if (chunk_type == stag_magic) {
                StartElement s = new StartElement();
                s.fromBytes(bs);
                startElements.add(s);
            } else if (chunk_type == etag_magic) {
                EndElement e = new EndElement();
                e.fromBytes(bs);
                endElements.add(e);
            } else if (chunk_type == ens_magic) {
                endNS = new EndNameSpace();
                endNS.fromBytes(bs);
            } else if (chunk_type == text_magic) {
                TextElement t = new TextElement();
                t.fromBytes(bs);
                texts.add(t);
            } else {
                if (!Main.tryCompat)
                    Main.panic("Invalid type at " + offset + ": " + f[offset]);
            }
            offset += chunk_size;
        }
    }

    @Override
    public void update() {}

    @Override
    public int getMagic() {
        return magic;
    }

    @Override
    public int getSize() {
        update();
        return fsize;
    }

    @Override
    public int getHeaderSize() {
        update();
        return hsize;
    }

    /**
     * Serialize this AxmlFile object to a sequence of byte
     * 
     * @return serialized bytes, should be in a valid AXML format
     * @author duangsuse
     * @since 1.0
     */
    public byte[] getBytes() {
        update();

        // a new array with chunk size
        byte[] ser = new byte[fsize];
        return ser;
    }

    /** Alias for getBytes()
     * 
     * @return xmlfile bytes
     * @see getBytes() */
    @Override
    public byte[] toBytes() { return getBytes(); }
}
