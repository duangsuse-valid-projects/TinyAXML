package org.duangsuse.tinyaxml.type;

/**
 * Android AXML attribute class
 * <p> Contains namespace, name, rawValue, typedValue, resValue
 * 
 * @author duangsuse
 * @since 1.0
 */
public class Attribute {
    /** Namespace of this attribute */
    public int nameSpaceIndex;
    /** Attribute name index(strPool) */
    public int nameIndex;
    /** Raw string value */
    public int strValue;
    // now typedData
    /** Datatype */
    public int type;
    /** Typed data */
    public int typedData;

    /**
     * Raw constructor
     * 
     * @since 1.0
     */
    public Attribute() {}

    /** Object quick-setup
     * @param ns namespace index
     * @param name name index
     * @param str raw value index
     * @param type type
     * @param data typed data
     */
    public Attribute(int ns, int name, int str, int type, int data) {
        nameSpaceIndex = ns;
        nameIndex = name;
        strValue = str; // too long.....
        this.type = type;
        typedData = data;
    }
}
