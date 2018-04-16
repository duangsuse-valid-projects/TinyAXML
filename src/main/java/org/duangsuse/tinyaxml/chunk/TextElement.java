package org.duangsuse.tinyaxml.chunk;

import org.duangsuse.tinyaxml.IChunk;

/**
 * Text element chunk
 * 
 * @author duangsuse
 * @since 1.0
 */
public class TextElement implements IChunk {
    /** Header size */
    int hsize;
    /** Type magic */
    int type;
    /** Size */
    int size;
    /** Line number */
    public int lineNum;
    /** Unknown */
    public int unknown;
    /** Name reference */
    public int name;
    /** Unknown 1 */
    public int unknown1;
    /** Unknown 2 */
    public int unknown2;

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
