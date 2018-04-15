package org.duangsuse.tinyaxml.chunk;

import java.util.ArrayList;
import org.duangsuse.tinyaxml.IChunk;

/**
 * String pool struct and parser
 * 
 * @author duangsuse
 * @since 1.0
 * {@link https://github.com/fourbrother/AXMLEditor/blob/master/src/cn/wjdiankong/chunk/StringChunk.java}
 */
public class StringPool implements IChunk {
    /** Header size(orignal) */
    public int hsize;
    /** Type magic number */
    public int type;
    /** Chunk size(orignal) */
    public int size;

    /** String contents */
    public ArrayList<String> strings;

    /** Blank constructor */
    public StringPool() {}

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getHeaderSize() {
        return hsize;
    }

    @Override
    public int getMagic() {
        return type;
    }

    @Override
    public void fromBytes(byte[] bs) {}

    @Override
    public byte[] toBytes() {
        byte[] ret = new byte[size];
        return ret;
    }

    @Override
    public void update() {}
}
