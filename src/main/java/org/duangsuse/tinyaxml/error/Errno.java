package org.duangsuse.tinyaxml.error;

/**
 * TinyAxml error kind enumeration
 * 
 * @since 1.0
 * @author duangsuse
 */
public enum Errno {
    /** IO Exception */
    EIOEXCEPT,
    /** Could not find given plugin */
    EPLUGINNODEF,
    /** Bad AXML Magic */
    EBADMAGIC,
    /** Bad chunk length */
    EBADLEN;

    /**
     * Gets the human-readable description of this error kind
     * <p>
     * Example:
     * <p> {@code EIOEXCEPT.getDescription()} to "IO Exception"
     * 
     * @return description of this error kind
     * @since 1.0
     */
    public String getDescription() {
        switch (this) {
            case EPLUGINNODEF:
                return "Plugin not found";
            case EIOEXCEPT:
                return "IO Exception";
            case EBADMAGIC:
                return "Bad AXML file magic";
            case EBADLEN:
                return "Bad length";
            default:
                return "Unknown";
        }
    }

    /**
     * Implementaion of {@link Object#toString()}
     * 
     * @return Errno description
     * @since 1.0
     */
    @Override
    public String toString() {
        return getDescription(); // <3
    }
}
