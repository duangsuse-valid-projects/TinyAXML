package org.duangsuse.tinyaxml.type; // <3

// Elementmememememt~
import org.duangsuse.tinyaxml.chunk.StartElement;
import org.duangsuse.tinyaxml.chunk.EndElement;

/**
 * An XML element
 * <p> Containing AXML start element and end element.
 * 
 * @since 1.0
 * @author duangsuse
 */
public class Element {
    /** AXML start element */
    public StartElement start;
    /** AXML end element */
    public EndElement end;

    /** Blank constructor */
    public Element() {}
    
    /** Short-link for setting-up variables
     * 
     * @param s startelement
     * @param e endelement
     */
    public Element(StartElement s, EndElement e) {
        start = s;
        end = e;
    }
}
