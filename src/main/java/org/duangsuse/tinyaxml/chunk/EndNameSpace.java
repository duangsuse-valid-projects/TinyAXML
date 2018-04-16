package org.duangsuse.tinyaxml.chunk;

import org.duangsuse.tinyaxml.IChunk;

/**
 * End namespace chunk class
 * <p> {@link StartNameSpace}
 * 
 * @author duangsuse
 * @since 1.0
 */
public class EndNameSpace implements IChunk {
    /** Header size */
    int hsize;
    /** Magic */
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
    public EndNameSpace() {}

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
