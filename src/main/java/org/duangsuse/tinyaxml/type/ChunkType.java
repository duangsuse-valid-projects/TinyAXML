package org.duangsuse.tinyaxml.type;

// Imports <3 duangsuse
import org.duangsuse.tinyaxml.Main;
import org.duangsuse.tinyaxml.AxmlFile;

/**
 * Axml chunk type magics
 * <p> e.g. STR_POOL RES_MAP
 * 
 * @author duangsuse
 * @since 1.0
 * {@link https://gist.github.com/duangsuse/3ae94e339eb188fa4ec8a87b6e105331#%E4%BA%8C-axml-%E6%96%87%E4%BB%B6%E6%A0%BC%E5%BC%8F}
 * {@link https://github.com/rednaga/axmlprinter/blob/master/src/main/java/android/content/res/chunk/ChunkType.java}
 */
public enum ChunkType {
    /** RES_NULL_TYPE */
    TNUL,
    /** RES_STRING_POOL_TYPE */
    TSTRPOL,
    /** RES_TABLE_TYPE */
    TTBL,

    /** RES_XML_TYPE */
    TXTYPE,
    /** RES_XML_FIRST_CHUNK_TYPE */
    TXFIRSTCHUNK, // unused, same to TXSTARTNS
    /** RES_XML_START_NAMESPACE_TYPE */
    TXSTARTNS,
    /** RES_XML_END_NAMESPACE_TYPE */
    TXENDNS,
    /** RES_XML_START_ELEMENT_TYPE */
    TXSTARTE,
    /** RES_XML_END_ELEMENT_TYPE */
    TXENDE,
    /** RES_XML_CDATA_TYPE */
    TXCDATA,
    /** RES_XML_LAST_CHUNK_TYPE */
    TXLASTCHUNK,

    /** RES_XML_RESOURCE_MAP_TYPE */
    TRESMAP,
    /** RES_TABLE_PACKAGE_TYPE */
    TTPKG,
    /** RES_TABLE_TYPE_TYPE */
    TTTYPE,
    /** RES_TABLE_TYPE_SPEC_TYPE */
    TTSPEC;

    public static ChunkType fromMagic(int magic) {
        ChunkType tmp = AxmlFile.MagicMaps.CHUNK_TABLE.get(magic);
        if (tmp == null)
            if (!Main.tryCompat)
                Main.panic("Failed to map magic " + magic);
            else
                Main.warn("Unresolved chunk magic: " + magic);
        return tmp;
    }
    
    public int toMagic() {
        return AxmlFile.MagicMaps.CHUNK_TABLE_REVERSE.get(this);
    }

    public String getDescription() {
        return "";
    }
}
