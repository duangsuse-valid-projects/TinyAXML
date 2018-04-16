package org.duangsuse.tinyaxml.chunk;

import org.duangsuse.tinyaxml.IChunk;

/**
 * AXML end element chunk
 * 
 * @author duangsuse
 * @since 1.0
 */
public class EndElement implements IChunk {
    /** Header size */
    int hsize;
    /** Type */
    int type;
    /** Size */
    int size;
    /** Line number */
    public int lineNum;
    /** Unknown */
    public int unknown;
    /** NS reference */
    public int uri;
    /** Tag name */
    public int name;

    /** Blank ctor */
    public EndElement() {}

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
