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
 * {@link https://gist.github.com/duangsuse/3ae94e339eb188fa4ec8a87b6e105331#%E4%BA%8C-axml-%E6%96%87%E4%BB%B6%E6%A0%BC%E5%BC%8F} (not used)
 * {@link https://github.com/rednaga/axmlprinter/blob/master/src/main/java/android/content/res/chunk/ChunkType.java}
 */
public enum ChunkType {
    /** AXML header magic */
    AXML,
    /** String pool magic */
    STR_POOL,
    /** Resource map magic */
    RES_MAP,
    /** Axml start namespace */
    START_NS,
    /** Axml end namespace */
    END_NS,
    /** Axml start element */
    START_TAG,
    /** Axml end element */
    END_TAG,
    /** Axml text tag */
    TEXT_TAG;

    /**
     * Converts an integer magic to maching ChunkType enumeration
     * 
     * @since 1.0
     * @param magic chunk magic
     * @return maching type or {@code null} if no maching found
     * @see AxmlFile.MagicMaps#CHUNK_TABLE
     */
    public static ChunkType fromMagic(int magic) {
        ChunkType tmp = AxmlFile.MagicMaps.CHUNK_TABLE.get(magic);
        if (tmp == null)
            if (!Main.tryCompat)
                Main.panic("Failed to map magic " + magic);
            else
                Main.warn("Unresolved chunk magic: " + magic);
        return tmp;
    }
    
    /**
     * Converts the {@code ChunkType} enum to maching integer represenation
     * 
     * @since 1.0
     * @return integer (binary 4-bytes aka a word) magic
     * @see ChunkType#fromMagic(int)
     */
    public int toMagic() {
        return AxmlFile.MagicMaps.CHUNK_TABLE_REVERSE.get(this);
    }

    /**
     * Gets a human-readable description of the chunk type
     * 
     * @since 1.0
     * @return description of this chunk type
     */
    public String getDescription() {
        switch (this) {
            case AXML: return "Axml file";
            case STR_POOL: return "String pool";
            case RES_MAP: return "Resource map";
            case START_NS: return "Start namespace";
            case END_NS: return "End namespace";
            case START_TAG: return "Start tag";
            case END_TAG: return "End tag";
            case TEXT_TAG: return "Text tag";
            default: return "Unknown";
        }
    }
}
