package Item;

import java.io.InputStream;
import javax.imageio.ImageIO;

public class DamageItem extends Item {

    public DamageItem() {
        this.name = "Damage Booster";
        try {
            // Path ini disesuaikan persis dengan struktur foldermu di gambar
            InputStream stream = getClass().getResourceAsStream("/Assets/Item/Weapon/Icon28_01.png");
            
            if (stream != null) {
                this.image = ImageIO.read(stream);
            } else {
                System.out.println("Gambar tidak ditemukan! Cek kembali path-nya.");
            }
        } catch (Exception e) {
            System.out.println("Error loading DamageItem image: " + e.getMessage());
        }
    }

    @Override
    public void use() {
        // Logika untuk menambah damage player. 
        // Contoh: Player.attackDamage += 10;
        System.out.println(this.name + " digunakan! Damage meningkat!");
    }
}