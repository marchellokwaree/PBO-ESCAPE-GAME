package Main;

import java.awt.Graphics2D;
import Item.Item;
import Item.Lantern;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import Item.DamageItem;
import Item.DefenseItem;

public class Inventory {
    BufferedImage image;
    int x, y;
    int width, height;
    BufferedImage spriteSheet;
    boolean isVisible = true;
    public Item[] slots = new Item[4];
    public Item lanternSlot;
    public Item equippedWeapon;
    public Item equippedArmor;
    public Item equippedLantern;
    private GamePanel gp;

    public Inventory(GamePanel gp) {
        this.gp = gp;
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

        // Dedicated lantern slot on the left side of the inventory UI
        int lanternSize = 28;
        int lanternX = this.x + 14;
        int lanternY = this.y + 7;
        if (lanternSlot != null) {
            if (lanternSlot == equippedLantern) {
                g2.setColor(new java.awt.Color(0, 255, 255, 150));
                g2.fillRect(lanternX, lanternY, lanternSize, lanternSize);
            }
            lanternSlot.draw(g2, lanternX, lanternY, lanternSize, lanternSize);
        }

        // Hanya menggambar 4 kotak di tas (sebelah kanan)
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != null) {
                int itemSize = 28;

                // Gunakan angka offset X dan Y yang sudah kamu sesuaikan sebelumnya
                int offsetX = this.x + 68;
                int offsetY = this.y + 7;
                int jarakAntarSlot = i * 34;

                int slotX = offsetX + jarakAntarSlot;
                int slotY = offsetY;

                // ===== LOGIKA HIGHLIGHT WARNA BACKGROUND =====
                // Cek apakah item di kotak ini adalah senjata yang sedang dipakai?
                if (slots[i] == equippedWeapon) {
                    // Beri background Kuning (dengan efek transparan / Alpha 150)
                    g2.setColor(new java.awt.Color(255, 255, 0, 150));
                    g2.fillRect(slotX, slotY, itemSize, itemSize);
                }
                // Cek apakah item di kotak ini adalah armor yang sedang dipakai?
                else if (slots[i] == equippedArmor) {
                    // Beri background Oranye (dengan efek transparan / Alpha 150)
                    g2.setColor(new java.awt.Color(255, 165, 0, 150));
                    g2.fillRect(slotX, slotY, itemSize, itemSize);
                } else if (slots[i] == equippedLantern) {
                    // Beri background Cyan (dengan efek transparan / Alpha 150)
                    g2.setColor(new java.awt.Color(0, 255, 255, 150));
                    g2.fillRect(slotX, slotY, itemSize, itemSize);
                }
                // ==============================================

                // Gambar ikon item DI ATAS warna background tersebut
                slots[i].draw(g2, slotX, slotY, itemSize, itemSize);
            }
        }

        int textX = this.x + 5;
        int textY = this.y - 25; // Minus berarti posisinya naik ke atas kotak UI

        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 13));

        // 1. Teks untuk Senjata (Weapon)
        if (equippedWeapon != null && equippedWeapon instanceof DamageItem) {
            DamageItem weapon = (DamageItem) equippedWeapon;

            String teksSenjata = "Senjata: " + weapon.name + " (+ " + weapon.bonusDamage + " DMG)";

            // Bayangan Hitam (Drop Shadow) agar teks terbaca jelas di atas latar map
            g2.setColor(java.awt.Color.BLACK);
            g2.drawString(teksSenjata, textX + 2, textY + 2);

            // Teks Utama (Warna Kuning)
            g2.setColor(java.awt.Color.YELLOW);
            g2.drawString(teksSenjata, textX, textY);

            // MENGATUR TUMPUKAN:
            // Tambah nilai Y agar jika pemain memakai Armor juga,
            // teks armornya akan tergambar di bawah teks senjata ini (Tumpuk atas bawah).
            textY += 20;
        }

        // 2. Teks untuk Baju Besi (Armor)
        if (equippedArmor != null && equippedArmor instanceof DefenseItem) {
            DefenseItem armor = (DefenseItem) equippedArmor;

            String teksArmor = "Pelindung: " + armor.name + " (+ " + armor.bonusDef + "% Reduksi)";

            // Bayangan Hitam (Drop Shadow)
            g2.setColor(java.awt.Color.BLACK);
            g2.drawString(teksArmor, textX + 2, textY + 2);

            // Teks Utama (Warna Oranye)
            g2.setColor(new java.awt.Color(255, 165, 0)); // Oranye Solid
            g2.drawString(teksArmor, textX, textY);
            textY += 20;
        }

        if (equippedLantern != null && equippedLantern instanceof Lantern) {
            Lantern lantern = (Lantern) equippedLantern;
            String teksLantern = "Lampu: " + lantern.name + " (+" + lantern.bonusSafeVision + " safe, +"
                    + lantern.bonusRange + " range)";

            g2.setColor(java.awt.Color.BLACK);
            g2.drawString(teksLantern, textX + 2, textY + 2);

            g2.setColor(new java.awt.Color(0, 255, 255));
            g2.drawString(teksLantern, textX, textY);
        }

        // (Kode yang menggambar item di kotak terpisah sebelah kiri SUDAH DIHAPUS)
    }

    public boolean addItem(Item item) {
        if (item instanceof Lantern) {
            if (lanternSlot == null) {
                lanternSlot = item;
                equippedLantern = item;
                if (gp != null) {
                    gp.lanternCollected = true;
                    if (gp.player != null) {
                        gp.player.attackDamage = gp.player.baseDamage;
                        gp.player.defense = gp.player.baseDefense;
                        gp.safeVisionTiles = gp.baseSafeVisionTiles;
                        gp.visionRangeTiles = gp.baseVisionRangeTiles;
                        if (equippedWeapon != null) {
                            equippedWeapon.use(gp.player);
                        }
                        if (equippedArmor != null) {
                            equippedArmor.use(gp.player);
                        }
                        if (equippedLantern != null) {
                            equippedLantern.use(gp.player);
                        }
                    }
                }
                System.out.println(item.name + " otomatis masuk lantern slot dan di-equip.");
                return true;
            }
            System.out.println("Lantern sudah ada di inventory.");
            return false;
        }

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
