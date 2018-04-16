package org.duangsuse.tinyaxml.chunk;

import java.util.ArrayList;
import org.duangsuse.tinyaxml.IChunk;
import org.duangsuse.tinyaxml.type.Attribute;

/**
 * Start element chunk
 * <p> See: https://github.com/duangsuse/AXMLEdit/blob/master/src/main/java/cn/wjdiankong/axmledit/chunk/StartTagChunk.java
 * 
 * @author duangsuse
 * @since 1.0
 */
public class StartElement implements IChunk {
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
    /** NS reference */
    public int uri;
    /** Tag name */
    public int name;
    /** Flags */
    public int flag;
    /** Class attribute */
    public int classAttr;
    /** Attributes */
    public ArrayList<Attribute> attrList;

    /** Blank ctor */
    public StartElement() {}

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
