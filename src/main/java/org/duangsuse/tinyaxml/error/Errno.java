package org.duangsuse.tinyaxml.error;

public enum Errno {
    EIOEXCEPT,
    EPLUGINNODEF;

    public String getDescription() {
        switch (this) {
            case EPLUGINNODEF:
                return "Plugin not found";
            case EIOEXCEPT:
                return "IO Exception";
            default:
                return "unknown";
        }
    }
}
