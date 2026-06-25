package Item;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class Item {
    public String name;
    public BufferedImage image;
    
    // Abstract method yang harus di-override oleh item spesifik
    // Anda bisa passing class Player di sini jika damage ada di class Player (misal: use(Player p))
    public abstract void use(); 

    // Method untuk menggambar item di layar (di dalam slot inventory)
    public void draw(Graphics2D g2, int x, int y, int width, int height) {
        if (image != null) {
            g2.drawImage(image, x, y, width, height, null);
        }
    }
}