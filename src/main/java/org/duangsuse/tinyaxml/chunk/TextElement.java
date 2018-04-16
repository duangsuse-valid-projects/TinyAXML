package org.duangsuse.tinyaxml.chunk;

import org.duangsuse.tinyaxml.IChunk;

// TODO write document and define this chunk
public class TextElement implements IChunk {
    int hsize;
    int type;
    int size;

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
