/** @author María Fortuna */

package com.rpgmaker.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Cache de imágenes para evitar cargar repetidamente desde disco.
 * Soporta PNG y GIF (incluidos GIFs animados vía Toolkit).
 */
public class ImageCache {

    private static final Map<String, Image> cache = new HashMap<>();

    /**
     * Obtiene una imagen del cache o la carga desde disco si no está cacheada.
     * Para GIFs animados usa Toolkit para preservar la animación.
     */
    public static Image getImage(String path) {
        if (path == null || path.isBlank()) return null;

        if (cache.containsKey(path)) {
            return cache.get(path);
        }

        try {
            File file = new File(path);
            if (!file.exists()) return null;

            Image img;
            if (path.toLowerCase().endsWith(".gif")) {
                // Usar Toolkit para GIF animados
                img = Toolkit.getDefaultToolkit().createImage(path);
            } else {
                // PNG u otros formatos estáticos
                BufferedImage bimg = ImageIO.read(file);
                if (bimg == null) return null;
                img = bimg;
            }

            cache.put(path, img);
            return img;
        } catch (Exception e) {
            System.err.println("Error al cargar imagen: " + path + " - " + e.getMessage());
            return null;
        }
    }

    /** Limpia toda la cache de imágenes. */
    public static void clearCache() {
        cache.clear();
    }

    /** Elimina una imagen específica de la cache. */
    public static void removeFromCache(String path) {
        cache.remove(path);
    }
}
