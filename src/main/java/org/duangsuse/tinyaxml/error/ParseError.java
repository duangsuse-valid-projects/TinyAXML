package org.duangsuse.tinyaxml.error;

/**
 * TinyAXML Parser error
 * 
 * @author duangsuse
 * @since 1.0
 */
public class ParseError extends Exception {
    /** Serializable class generated VersionUID */
    private static final long serialVersionUID = 1936366107961749570L;

    /** Error kind @see Errno */
    public Errno errnum;

    /** Parsing offset */
    public int offset;

    /** Stops at section(String) */
    public String section;

    /**
     * Constructor of ExtensionBootstrapError class
     * 
     * @param e Errno
     */
    public ParseError(Errno e) {
        errnum = e; // QAQ
    }
}
