package org.duangsuse.tinyaxml.error;

import java.io.File;

/**
 * TinyAXML File IO Error(maybe useless XD)
 * 
 * @author duangsuse
 * @since 1.0
 */
public class FileIOError extends Exception {
    /** Serializable class generated VersionUID */
    private static final long serialVersionUID = -6865406274370569752L;

    /** Error kind @see Errno */
    public Errno errnum;

    /** Target file */
    public File file;

    /**
     * Constructor of FileIOError class
     * 
     * @param e Errno
     */
    public FileIOError(Errno e) {
        errnum = e; // O<C
    }
}
