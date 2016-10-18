package com.ctreber.aclib.image.ico.spi;

import java.util.Locale;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;

/**
 * <p>
 * Information for the ICO image service provider plugin.
 * </p>
 * @author &copy; Christian Treber, ct@ctreber.com
 */
public class ICOImageReaderSPI extends ImageReaderSpi {
    /**
     * Define the capabilities of this provider service.
     */
    public ICOImageReaderSPI() {
        super("Christian Treber, www.ctreber.com, ct@ctreber.com",
                "1.0 December 2003", new String[] { "ico", "ICO" },
                new String[] { "ico", "ICO" }, new String[] { "image/x-ico" },
                "com.ctreber.aclib.image.ico.ICOReader", STANDARD_INPUT_TYPE,
                null, false, null, null,

                null, null,

                false,

                "com.ctreber.aclib.image.ico.ICOMetadata_1.0",
                "com.ctreber.aclib.image.ico.ICOMetadata",

                null, null);
    }

    public boolean canDecodeInput(final Object pSource) {
        // Well, yes, we might see a remote chance that reading will succeed.
        return true;
    }

    /**
     * @see javax.imageio.spi.ImageReaderSpi#createReaderInstance(java.lang.Object)
     * @param pExtension
     *            Not used by our reader.
     */
    public ImageReader createReaderInstance(final Object pExtension) {
        // Return our reader.
        return new ICOReader(this);
    }

    /**
     * @see javax.imageio.spi.IIOServiceProvider#getDescription(java.util.Locale)
     * @param pLocale
     *            Ignored - one locale fits all.
     */
    public String getDescription(final Locale pLocale) {
        return "Microsoft Icon Format (ICO)";
    }
}
