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
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != null) {
                
                // 1. UKURAN ITEM (Perkecil agar tidak keluar garis kotak)
                // Coba turunkan dari 32 ke 24 atau 26
                int itemSize = 24; 
                
                // 2. POSISI AWAL (Padding kiri dan atas)
                // Ubah angka ini untuk menggeser posisi item di kotak pertama
                int offsetX = this.x + 13; // Tambah angka untuk geser ke KANAN
                int offsetY = this.y + 10; // Tambah angka untuk geser ke BAWAH
                
                // 3. JARAK ANTAR KOTAK
                // Jarak dari kotak 1 ke kotak 2, dst.
                int jarakAntarSlot = i * 45; 
                
                int slotX = offsetX + jarakAntarSlot; 
                int slotY = offsetY;
                
                // Menggambar item dengan variabel yang sudah disesuaikan
                slots[i].draw(g2, slotX, slotY, itemSize, itemSize);
            }
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
