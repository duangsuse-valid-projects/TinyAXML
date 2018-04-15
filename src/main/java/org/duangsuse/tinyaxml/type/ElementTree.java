package org.duangsuse.tinyaxml.type;

// Ary O<| duangsuse
import java.util.ArrayList;

import org.duangsuse.tinyaxml.chunk.EndElement;
import org.duangsuse.tinyaxml.chunk.StartElement;
// Element!!!
import org.duangsuse.tinyaxml.type.Element;

/**
 * A tree of elements
 * <p>
 * Like:
 * <p> {@code <a name="foo"><b>foo</b></a><c></c> }
 * @since 1.0
 * @author duangsuse
 */
public class ElementTree {
    /** This element */
    public Element me; // <- Duangsuse@2333333 for duangsuse
    /** Belonging elements */
    public ArrayList<ElementTree> inner;
    /** Next elements */
    public ElementTree next;

    /** Blank constructor */
    public ElementTree() {}

    /** Construct from StartElement and EndElement list */
    public ElementTree(ArrayList<StartElement> startags, ArrayList<EndElement> endtags) {}
}
