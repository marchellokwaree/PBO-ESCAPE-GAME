package Main;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
public class Inventory {
    BufferedImage image;
    int x, y;
    int width, height;
    BufferedImage spriteSheet;
    boolean isVisible = true;
    public Inventory(GamePanel gp) {
        this.x = (gp.screenWidth - 216) / 2; // Center horizontally
        this.y = gp.screenHeight - 48; // Position at the bottom of the screen
        this.width = 108 * 2;
        this.height = 32 * 2;
        try {
            this.spriteSheet = loadBufferedImage("/Assets/ASSET/Sample-InventorySlotsSet.png");
            this.image = spriteSheet.getSubimage(111, 80, 108, 32);            
        } catch (Exception e) {
            System.out.println("Error loading inventory image: " + e.getMessage());
            e.printStackTrace();
        }
   }
   
   protected BufferedImage loadBufferedImage(String path) {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream != null) {
                return ImageIO.read(stream);
            }
            File file = resolveImageFile(path);
            return ImageIO.read(file);
        } catch (IOException e) {
            System.err.println("Failed to load inventory buffered image: " + path + " -> " + e.getMessage());
            return null;
        }
    }

    private File resolveImageFile(String path) {
        String normalizedPath = path.replace('/', File.separatorChar);
        String userDir = System.getProperty("user.dir");

        File candidate = new File(userDir + File.separator + "src" + normalizedPath);
        if (candidate.exists()) {
            return candidate;
        }

        candidate = new File(userDir + normalizedPath);
        if (candidate.exists()) {
            return candidate;
        }

        candidate = new File(userDir + File.separator + "Maze" + File.separator + "src" + normalizedPath);
        if (candidate.exists()) {
            return candidate;
        }
        return new File(userDir + File.separator + "src" + normalizedPath);
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, width, height, null);
    }


}
