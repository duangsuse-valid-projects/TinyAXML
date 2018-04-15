package org.duangsuse.tinyaxml.error;

/**
 * Extension bootstrap error thrown in CLI class
 * 
 * @author duangsuse
 * @since 1.0
 */
public class ExtensionBootstrapError extends Exception {
    /** Serializable class generated VersionUID */
    private static final long serialVersionUID = -959152103689398633L;
    /** Error kind @see Errno */
    public Errno errnum;

    /**
     * Constructor of ExtensionBootstrapError class
     * 
     * @param e Errno
     */
    public ExtensionBootstrapError(Errno e) {
        errnum = e; // O<|C
    }
}
