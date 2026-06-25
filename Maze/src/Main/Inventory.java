package Main;
import java.awt.Graphics2D;
import Item.Item;
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
    public Item[] slots = new Item[4];
    public Item equippedItem;
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
        
        // 1. Menggambar isi tas (4 kotak berdempetan di kanan)
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != null) {
                int itemSize = 28; 
                
                // GESER KE KANAN: Naikkan dari 58 menjadi 68
                int offsetX = this.x + 68; 
                
                // GESER KE ATAS: Turunkan dari 18 menjadi 12 (atau 10 jika masih kurang atas)
                int offsetY = this.y + 8; 
                
                // Jarak antar slot sudah pas, biarkan 40
                int jarakAntarSlot = i * 34; 
                
                int slotX = offsetX + jarakAntarSlot; 
                int slotY = offsetY;
                
                slots[i].draw(g2, slotX, slotY, itemSize, itemSize);
            }
        }

        // 2. Menggambar item yang sedang dipakai (kotak terpisah di paling kiri)
        if (equippedItem != null) {
            int itemSize = 28;
            
            // GESER KE KANAN: Naikkan dari 10 menjadi 18
            int kotakKiriX = this.x + 13;
            
            // GESER KE ATAS: Turunkan dari 18 menjadi 12
            int kotakKiriY = this.y + 8;
            
            equippedItem.draw(g2, kotakKiriX, kotakKiriY, itemSize, itemSize);
        }
    }

    public boolean addItem(Item item) {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == null) { // Cari slot yang masih kosong (null)
                slots[i] = item;
                System.out.println(item.name + " masuk ke slot " + i);
                return true; // Berhasil masuk, keluar dari fungsi
            }
        }
        System.out.println("Inventory Penuh!");
        return false; // Gagal masuk karena semua slot terisi
    }


}
