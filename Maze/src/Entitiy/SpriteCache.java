package Entitiy;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache statis untuk sprite sheet agar tidak di-load ulang per monster.
 * Semua monster tipe yang sama berbagi sprite yang sama di memory.
 */
public class SpriteCache {
    private static final Map<String, BufferedImage> cache = new HashMap<>();

    /**
     * Ambil sprite dari cache. Jika belum ada, load dari resource lalu simpan.
     */
    public static BufferedImage get(String path, Entity loader) {
        BufferedImage img = cache.get(path);
        if (img == null) {
            img = loader.loadBufferedImage(path);
            if (img != null) {
                cache.put(path, img);
            }
        }
        return img;
    }

    /**
     * Bersihkan seluruh cache (misalnya saat reset game).
     */
    public static void clear() {
        cache.clear();
    }
}
