package org.duangsuse.tinyaxml.type; // Bad(

// require helper classes
import org.duangsuse.tinyaxml.Main;
import org.duangsuse.tinyaxml.AxmlFile;

/**
 * Attribute types
 * <p>
 * Example: STR INT BOOL
 * 
 * https://github.com/rednaga/axmlprinter/blob/master/src/main/java/android/content/res/chunk/AttributeType.java
 * @author duangsuse
 * @since 1.0
 */
public enum AttributeType {
    /** String */
    STR,
    /** 32bit Integer */
    INT,
    /** Boolean */
    BOOL,
    /** Resource */
    RESOURCE,
    /** Attribute */
    ATTR,
    /** Dimen */
    DIMEN,
    /** Fraction */
    FRACTION,
    /** Float point */
    FLOAT,
    /** Flags */
    FLAGS,
    /** Color 1 */
    COLOR1,
    /** Color 2 */
    COLOR2;

    /**
     * Converts an integer to maching Attribute representation
     * 
     * @since 1.0
     * @param magic integer magic
     * @return maching Attribute enumeration or null if no maching found
     * @see AxmlFile.MagicMaps#ATTR_TABLE
     */
    public static AttributeType fromMagic(int magic) {
        AttributeType ret = AxmlFile.MagicMaps.ATTR_TABLE.get(magic);
        if (ret == null)
            if (!Main.tryCompat)
                Main.panic("Failed to map attr magic " + magic);
            else
                Main.warn("Unresolved attr magic: " + magic);
        return ret;
    }

    /**
     * Converts AttributeType to integer
     * 
     * @since 1.0
     * @return Maching attributeType magic
     * @see AttributeType#fromMagic(int)
     */
    public int toMagic() {
        return AxmlFile.MagicMaps.ATTR_TABLE_REVERSE.get(this);
    }

    /**
     * Gets a human-readbale description of this AttributeType
     * 
     * @since 1.0
     * @return description of this AttributeType
     */
    public String getDescription() {
        switch (this) {
            case STR: return "String";
            case INT: return "Integer";
            case BOOL: return "Boolean";
            case RESOURCE: return "Resource";
            case ATTR: return "Attribute";
            case DIMEN: return "Dimen";
            case FRACTION: return "Fraction";
            case FLOAT: return "Float";
            case FLAGS: return "Flags";
            case COLOR1: return "Color1";
            case COLOR2: return "Color2";
            default: return "Unknown";
        }
    }

    /**
     * {@link Object#toString()} implementation
     * @return description of this type
     * @since 1.0
     */
    @Override
    public String toString() {
        return getDescription();
    }
}
