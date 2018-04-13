package org.duangsuse.tinyaxml.error;

public class ExtensionBootstrapError extends Exception {
    private static final long serialVersionUID = -959152103689398633L;
    public Errno errnum;

    /**
     * Constructor of ExtensionBootstrapError class
     * 
     * @param e Errno
     */
    public ExtensionBootstrapError(Errno e) {
        errnum = e;
    }
}
