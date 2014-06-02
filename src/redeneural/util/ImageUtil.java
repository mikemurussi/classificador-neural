package redeneural.util;

import java.awt.image.BufferedImage;
import javax.imageio.spi.IIORegistry;

/**
 *
 * @author Michael Murussi
 */
public final class ImageUtil {

    public static void registerServiceProviders() {
        IIORegistry registry = IIORegistry.getDefaultInstance();
        registry.registerServiceProvider(new com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi());
        registry.registerServiceProvider(new com.sun.media.imageioimpl.plugins.tiff.TIFFImageReaderSpi());
    }

    public static int[] loadImageData(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        int[] pixels = image.getRGB(0, 0, w, h, null, 0, w);
        return pixels;
    }

    public static void saveImageData(BufferedImage image, int[] pixels) {
        int w = image.getWidth();
        int h = image.getHeight();
        image.setRGB(0, 0, w, h, pixels, 0, w);
    }

}
