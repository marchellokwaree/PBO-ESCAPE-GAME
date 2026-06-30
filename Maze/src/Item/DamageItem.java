package Item;

import Entitiy.Player;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class DamageItem extends Item {
    public int bonusDamage;

    // Parameter sekarang HANYA meminta Nama dan Nilai Damage
    public DamageItem(String namaSenjata, int damage) {
        this.name = namaSenjata;
        this.bonusDamage = damage;

        // 1. TENTUKAN NAMA FILE OTOMATIS BERDASARKAN NAMA SENJATA
        String namaFileGambar = "Icon28_01.png"; // Default awal

        switch (namaSenjata) {
            case "Pedang Kayu":
                namaFileGambar = "Icon28_01.png";
                break;
            case "Pedang Besi":
                namaFileGambar = "Icon28_02.png";
                break;
            case "Kapak Ganda":
                namaFileGambar = "Icon28_03.png";
                break;
            case "Pedang Petir":
                namaFileGambar = "Icon28_04.png";
                break;
            default:
                namaFileGambar = "Icon28_01.png";
                break; // Jika salah ketik, default ke Pedang Kayu
        }

        // 2. PROSES LOAD GAMBAR
        try {
            String path = "/Assets/Item/Weapon/" + namaFileGambar;
            InputStream stream = getClass().getResourceAsStream(path);
            if (stream != null) {
                this.image = ImageIO.read(stream);
            } else {
                System.out.println("Gambar tidak ditemukan: " + path);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void use(Player p) {
        p.attackDamage += this.bonusDamage;
        System.out.println(this.name + " di-equip! Damage + " + bonusDamage);
    }
}