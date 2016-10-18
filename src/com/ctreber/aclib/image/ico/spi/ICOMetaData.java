package com.ctreber.aclib.image.ico.spi;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.Node;

import com.ctreber.aclib.image.ico.BitmapDescriptor;

/**
 * <p>
 * ICO image meta data. I have no idea whether what I'm doing here is what is
 * expected from me!
 * </p>
 * @author &copy; Christian Treber, ct@ctreber.com
 */
public class ICOMetaData extends IIOMetadata {
    private final BitmapDescriptor _entry;

    /**
     * @param pEntry
     */
    public ICOMetaData(final BitmapDescriptor pEntry) {
        super();
        _entry = pEntry;
    }

    public Node getAsTree(final String pFormatName) {
        final IIOMetadataNode lRoot = new IIOMetadataNode(
                "javax_imageio_ico_image_1.0");

        IIOMetadataNode lNode = new IIOMetadataNode("width");
        lNode.setNodeValue("" + _entry.getWidth());
        lRoot.appendChild(lNode);
        lNode = new IIOMetadataNode("height");
        lNode.setNodeValue("" + _entry.getHeight());
        lRoot.appendChild(lNode);

        return lRoot;
    }

    public boolean isReadOnly() {
        return true;
    }

    public void mergeTree(final String pFormatName, final Node pRoot) {
        // Not needed.
    }

    public void reset() {
        // Not needed.
    }
}