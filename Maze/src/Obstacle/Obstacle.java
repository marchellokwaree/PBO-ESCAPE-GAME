package Obstacle;

import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;


import javax.imageio.ImageIO;

import Main.GamePanel;

public class Obstacle {
    public int x, y, width, height;
    boolean active = false;
    
    public Obstacle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean collidesWith(int playerX, int playerY, int playerSize) {
        return playerX < x + width &&
               playerX + playerSize > x &&
               playerY < y + height &&
               playerY + playerSize > y;
    }

    protected BufferedImage loadBufferedImage(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                throw new IOException("Resource not found: " + path);
            }
            return ImageIO.read(url);
        } catch (IOException e) {
            System.out.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void drawCamera(Graphics2D g2, GamePanel gp, BufferedImage img) {
        if (img != null && gp.getPlayer() != null) {
            // Rumus posisi layar = Posisi dunia - Posisi Player + Titik tengah layar
            int screenX = x - gp.getPlayer().x + gp.getPlayer().screenX;
            int screenY = y - gp.getPlayer().y + gp.getPlayer().screenY;

            // Optimasi: Hanya gambar jika jebakan masuk dalam area layar
            if (x + width > gp.getPlayer().x - gp.getPlayer().screenX &&
                x - width < gp.getPlayer().x + gp.getPlayer().screenX &&
                y + height > gp.getPlayer().y - gp.getPlayer().screenY &&
                y - height < gp.getPlayer().y + gp.getPlayer().screenY) {
                
                g2.drawImage(img, screenX, screenY, width, height, null);
            }
        }
    }


}
