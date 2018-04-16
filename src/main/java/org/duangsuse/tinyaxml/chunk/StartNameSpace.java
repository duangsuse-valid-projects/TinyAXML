package org.duangsuse.tinyaxml.chunk;

import org.duangsuse.tinyaxml.IChunk;

/**
 * AXML start namespace chunk
 * 
 * @author duangsuse
 * @since 1.0
 */
public class StartNameSpace implements IChunk {
    /** Chunk header size */
    int hsize;
    /** Chunk magic */
    int type;
    /** Chunk size */
    int size;
    /** Line number */
    public int lineNum;
    /** Unknown */
    public int unknown;
    /** Prefix */
    public int prefix;
    /** Uri index */
    public int uri;

    /** Blank ctor */
    public StartNameSpace() {}

    @Override
    public int getSize() {
        update();
        return size;
    }

    @Override
    public int getHeaderSize() {
        update(); // ;-)
        return hsize;
    }

    @Override
    public int getMagic() {
        return type;
    }

    // parser
    @Override
    public void fromBytes(byte[] bs) {}

    // serializer
    @Override
    public byte[] toBytes() {
        byte[] ret = new byte[size];
        return ret;
    }

    // make header inforamtion
    @Override
    public void update() {}
}
