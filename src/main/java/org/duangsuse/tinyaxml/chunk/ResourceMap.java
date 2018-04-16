package org.duangsuse.tinyaxml.chunk;

import java.util.ArrayList;
import org.duangsuse.tinyaxml.IChunk;

/**
 * Resource map is an array of integer
 * 
 * @author duangsuse
 * @since 1.0
 */
public class ResourceMap implements IChunk {
    /** Header size */
    int hsize;
    /** Magic */
    int type;
    /** Size */
    int size;

    public ArrayList<Integer> resIds;

    /** Blank ctor */
    public ResourceMap() {}

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
