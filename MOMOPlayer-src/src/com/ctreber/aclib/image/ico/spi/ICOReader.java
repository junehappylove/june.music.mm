package com.ctreber.aclib.image.ico.spi;

import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.ctreber.aclib.image.ImageInputStreamDecoder;
import com.ctreber.aclib.image.ico.BitmapDescriptor;
import com.ctreber.aclib.image.ico.ICOFile;

/**
 * <p>
 * ICO image service provider plugin. Supports only the most basic ImageIO
 * options (i.e., fires no events etc.).
 * </p>
 * @author &copy; Christian Treber, ct@ctreber.com
 */
public class ICOReader extends ImageReader {
    private static final int[] ONE = new int[1];

    protected ICOFile _icoFile;

    protected ImageInputStream _stream;

    /**
     * @param pProvider
     *            Handle back to the provider.
     */
    public ICOReader(final ImageReaderSpi pProvider) {
        super(pProvider);
    }

    public int getHeight(final int pImageIndex) {
        return getICOEntry(pImageIndex).getHeight();
    }

    public IIOMetadata getImageMetadata(final int pImageIndex) {
        return new ICOMetaData(getICOEntry(pImageIndex));
    }

    public Iterator getImageTypes(final int pImageIndex) {
        final List lTypes = new ArrayList();
        for (int lImageNo = 0; lImageNo < getNumImages(false); lImageNo++) {
            // BitmapDescriptor lEntry = getICOEntry(i);
            // FIXME Get that right, and understand what the spec says.
            final ImageTypeSpecifier lSpecifier = ImageTypeSpecifier
                    .createInterleaved(ColorSpace
                            .getInstance(ColorSpace.CS_sRGB), ONE,
                            DataBuffer.TYPE_BYTE, false, false);
            lTypes.add(lSpecifier);

        }
        return lTypes.iterator();
    }

    public int getNumImages(final boolean pAllowSearch) {
        return getICOFile().getImageCount();
    }

    public IIOMetadata getStreamMetadata() {
        return null;
    }

    public int getWidth(final int pImageIndex) {
        return getICOEntry(pImageIndex).getWidth();
    }

    public BufferedImage read(final int pImageIndex, final ImageReadParam pParam) {
        return getICOEntry(pImageIndex).getBitmap().createImageRGB();
    }

    public void setInput(final Object pInput, final boolean pSeekForwardOnly,
            final boolean pIgnoreMetadata) {
        if (!(pInput instanceof ImageInputStream)) {
            throw new IllegalArgumentException(
                    "Only ImageInputStream supported as input source");
        }

        _stream = (ImageInputStream) pInput;
    }

    /**
     * Get ICOFile object (cached).
     * @return The ICOFile object
     */
    private ICOFile getICOFile() {
        if (_icoFile == null) {
            try {
                _icoFile = new ICOFile("[ImageInputStream]",
                        new ImageInputStreamDecoder(_stream));
            } catch (IOException e) {
                System.err.println("Can't create ICOFile: " + e.getMessage());
            }
        }

        return _icoFile;
    }

    private BitmapDescriptor getICOEntry(final int pImageIndex) {
        return (BitmapDescriptor) getICOFile().getDescriptors()
                .get(pImageIndex);
    }

    /**
     * Check this out on how to read all icons contained in an ICO file with
     * ImageIO.
     * @param pArgs
     *            CLI arguments.
     * @throws IOException
     */
    public static void main(final String[] pArgs) throws IOException {
        if (pArgs.length == 0) {
            System.err.println("Please specify the icon file name");
            System.exit(1);
        }

        // This is how to register manually
        IIORegistry.getDefaultInstance().registerServiceProvider(
                new ICOImageReaderSPI());

        // Just for fun
        listServiceProviders();

        final File lICOFile = getICOFile(pArgs);
        final ImageReader lReader = getICOReader();
        lReader.setInput(ImageIO.createImageInputStream(lICOFile));

        final String lTitle = lICOFile.getName();
        final JFrame lFrame = createWindow(lTitle);

        // Add each contained image to frame.
        final int lNumImages = lReader.getNumImages(true);
        for (int lImageNo = 0; lImageNo < lNumImages; lImageNo++) {
            addImage(lFrame.getContentPane(), lReader, lImageNo);
        }

        lFrame.pack();
        lFrame.setVisible(true);
    }

    /**
     * @param pArgs
     *            As supplied with main().
     * @return ICO file (constructed from first argument)
     */
    private static File getICOFile(final String[] pArgs) {
        final String lIcoFileName = pArgs[0];
        final File lICOFile = new File(lIcoFileName);
        if (!lICOFile.isFile()) {
            System.err.println(lIcoFileName + " not found, or is no file");
            System.exit(1);
        }
        return lICOFile;
    }

    /**
     * @return ImageReader supporting ICO files.
     */
    private static ImageReader getICOReader() {
        final Iterator lImageReaderIt = ImageIO
                .getImageReadersByFormatName("ico");
        if (lImageReaderIt == null || !lImageReaderIt.hasNext()) {
            System.err.println("No reader for format 'ICO' found");
            System.exit(1);
        }

        // Use the first one found.
        return (ImageReader) lImageReaderIt.next();
    }

    private static JFrame createWindow(final String pTitle) {
        final JFrame lFrame = new JFrame(pTitle);
        lFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        lFrame.addWindowListener(new WindowAdapter() {
            public void windowClosed(final WindowEvent pEvent) {
                System.exit(0);
            }
        });
        final LayoutManager lLayout = new BoxLayout(lFrame.getContentPane(),
                BoxLayout.Y_AXIS);
        lFrame.getContentPane().setLayout(lLayout);
        return lFrame;
    }

    private static void addImage(final Container pParent,
            final ImageReader pReader, final int pImageNo) throws IOException {
        final JButton lButton = new JButton();
        lButton.setIcon(new ImageIcon(pReader.read(pImageNo)));
        lButton.setText("" + pReader.getWidth(pImageNo) + "x"
                + pReader.getHeight(pImageNo));
        pParent.add(lButton);
    }

    /**
     * List all formats supported by ImageIO, and show who provides support fir
     * them.
     */
    public static void listServiceProviders() {
        System.out.println("Registered image formats and their providers");
        final String[] lFormats = ImageIO.getReaderFormatNames();
        for (int lFormatNo = 0; lFormatNo < lFormats.length; lFormatNo++) {
            final String lFormat = lFormats[lFormatNo];
            final Iterator lItReader = ImageIO.getImageReadersBySuffix(lFormat);
            while (lItReader.hasNext()) {
                final ImageReader lReader = (ImageReader) lItReader.next();
                final ImageReaderSpi lProvider = lReader
                        .getOriginatingProvider();
                System.out.println(" o " + lFormat + " ("
                        + lProvider.getDescription(Locale.getDefault())
                        + ") by " + lProvider.getVendorName());
            }
        }
    }
}
