package Item;
import Entitiy.Player;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class DefenseItem extends Item {
    public int bonusDef;

    // Parameter sekarang HANYA meminta Nama dan Nilai Defense
    public DefenseItem(String namaArmor, int def) {
        this.name = namaArmor;
        this.bonusDef = def;
        
        // 1. TENTUKAN KOLOM OTOMATIS BERDASARKAN NAMA
        int colIndex = 0; // Default awal
        
        switch (namaArmor) {
            case "Baju Besi": 
                colIndex = 0; break;
            case "Baju Perunggu": 
                colIndex = 1; break;
            case "Armor Emas": 
                colIndex = 2; break;
            case "Armor Dewa": 
                colIndex = 3; break;
            default: 
                colIndex = 0; break; // Jika nama salah ketik, default ke Baju Besi
        }
        
        // 2. PROSES POTONG GAMBAR (Tetap sama seperti sebelumnya)
        try {
            String path = "/Assets/Item/Weapon/armor_black_outline.png"; 
            InputStream stream = getClass().getResourceAsStream(path);
            
            if (stream != null) {
                BufferedImage spriteSheet = ImageIO.read(stream);
                int spriteSize = 16; 
                int rowIndex = 1; 
                
                int x = colIndex * spriteSize;
                int y = rowIndex * spriteSize;
                
                this.image = spriteSheet.getSubimage(x, y, spriteSize, spriteSize);
            }
        } catch (Exception e) {
            System.out.println("Error memuat gambar armor: " + e.getMessage());
        }
    }

    @Override
    public void use(Player p) {
        p.defense += this.bonusDef; 
        System.out.println(this.name + " di-equip! Defense + " + bonusDef + "%");
    }
}