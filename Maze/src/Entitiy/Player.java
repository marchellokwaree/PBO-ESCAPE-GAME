package Entitiy;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import Main.Darah;
import Main.GamePanel;
import Main.KeyHandler;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;
    Image currentImage;
    BufferedImage bufferedImage;
    public Darah darah;
    // Variabel Animasi
    int spriteCounter = 0;
    int spriteNum = 1;
    boolean hadapKiri = false;
    public String direction = "DOWN"; // Default menghadap bawah
    public int baseDamage = 10;
    public int baseDefense = 0;
    public int attackDamage = 10;
    public int defense = 0;
    public int damageCooldown = 0;
    public int normalSpeed = 2;
    public int slowSpeed = 1;
    public int slowEffectCounter = 0; // Counter untuk efek slow
    public int screenX;
    public int screenY;
    public final int defaultScreenX;
    public final int defaultScreenY;
    BufferedImage[] walkImages = new BufferedImage[8]; // Array untuk menyimpan gambar berjalan
    Rectangle hitbox;

    public boolean isAttacking = false;
    public int attackCooldown = 0; // Cooldown antar serangan

    // Slash effect variables
    BufferedImage[] slashImages = new BufferedImage[5];
    public boolean isSlashActive = false;
    public int slashCounter = 0;
    public int slashFrame = 0;
    public String slashDirection = "DOWN";

    // Constructor disesuaikan dengan GamePanel kamu (5 parameter)
    public Player(GamePanel gp, KeyHandler keyH, Image playerImg, int x, int y) {
        super(x, y, 3); // speed 2
        this.gp = gp;
        this.keyH = keyH;
        this.currentImage = playerImg;
        try {
            this.bufferedImage = loadBufferedImage("/Assets/ASSET/ASSETKARAKTER/AnimationSheet.png");
            int spriteWidth = 24; // Lebar setiap sprite
            int spriteHeight = 24; // Tinggi setiap sprite
            int row2Y = (1 * spriteHeight) + 1; // Y untuk baris kedua
            if (this.bufferedImage != null) {
                for (int i = 0; i < 8; i++) {
                    int colx = i * spriteWidth; // X untuk setiap kolom
                    walkImages[i] = bufferedImage.getSubimage(colx, row2Y, spriteWidth, spriteHeight);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading animation sheet: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            BufferedImage slashSheet = loadBufferedImage("/Assets/Slash.png");
            if (slashSheet != null) {
                for (int i = 0; i < 5; i++) {
                    slashImages[i] = slashSheet.getSubimage(i * 64, 0, 64, 64);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading slash spritesheet: " + e.getMessage());
            e.printStackTrace();
        }

        // Sesuaikan hitbox dengan ukuran tile 24x24.
        // Hitbox lebih kecil agar tidak menabrak dinding di bawah/sekitar sprite.
        hitbox = new Rectangle();
        hitbox.x = 8; // offset dari kiri sprite
        hitbox.y = 4; // offset dari atas sprite
        hitbox.width = gp.getTileSize() - 16; // lebar hitbox
        hitbox.height = gp.getTileSize() - 4; // tinggi hitbox
        // Mengunci posisi default pemain tepat di tengah jendela game
        this.defaultScreenX = (gp.screenWidth / 2) - (gp.getTileSize() / 2);
        this.defaultScreenY = (gp.screenHeight / 2) - (gp.getTileSize() / 2);

        // Posisi layar saat ini
        this.screenX = defaultScreenX;
        this.screenY = defaultScreenY;

        this.darah = new Darah(gp);
    }

    public Rectangle getHitbox() {
        return new Rectangle(x + hitbox.x, y + hitbox.y, hitbox.width, hitbox.height);
    }

    public void update() {
        updateEffects();
        int nextX = x;
        int nextY = y;
        // --- KAMERA CLAMPING (Mencegah kamera keluar map) ---
        screenX = defaultScreenX;
        screenY = defaultScreenY;

        // Batas Kiri
        if (x < defaultScreenX) {
            screenX = x;
        }
        // Batas Kanan
        else if (x > gp.worldWidth - (gp.screenWidth - defaultScreenX)) {
            screenX = gp.screenWidth - (gp.worldWidth - x);
        }

        // Batas Atas
        if (y < defaultScreenY) {
            screenY = y;
        }
        // Batas Bawah
        else if (y > gp.worldHeight - (gp.screenHeight - defaultScreenY)) {
            screenY = gp.screenHeight - (gp.worldHeight - y);
        }
        boolean moving = false;

        // Cek Input dan tentukan gambar (Bisa dikembangkan per arah jika ada asetnya)
        // Cek Input dan tentukan gambar
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            moving = true;
            if (keyH.upPressed) {
                nextY -= speed;
                direction = "UP"; // Simpan arah Atas
            }
            if (keyH.downPressed) {
                nextY += speed;
                direction = "DOWN"; // Simpan arah Bawah
            }
            if (keyH.leftPressed) {
                nextX -= speed;
                hadapKiri = true; // (Biarkan ini, untuk membalik gambar sprite)
                direction = "LEFT"; // Simpan arah Kiri
            }
            if (keyH.rightPressed) {
                nextX += speed;
                hadapKiri = false; // (Biarkan ini, untuk membalik gambar sprite)
                direction = "RIGHT"; // Simpan arah Kanan
            }
        }
        // LOGIKA ANIMASI: Berganti antara spriteNum 1 dan 2 saat bergerak
        if (moving) {
            spriteCounter++;
            if (spriteCounter > 12) { // Kecepatan ganti kaki
                if (spriteNum == 1) {
                    spriteNum = 2;
                } else if (spriteNum == 2) {
                    spriteNum = 3;
                } else if (spriteNum == 3) {
                    spriteNum = 4;
                } else if (spriteNum == 4) {
                    spriteNum = 5;
                } else if (spriteNum == 5) {
                    spriteNum = 6;
                } else if (spriteNum == 6) {
                    spriteNum = 7;
                } else if (spriteNum == 7) {
                    spriteNum = 8;
                } else {
                    spriteNum = 1; // Kembali ke posisi diam setelah langkah ke-8
                }
                spriteCounter = 0;
            }

            if (spriteNum >= 1 && spriteNum <= 8) {
                currentImage = walkImages[spriteNum - 1]; // Ganti gambar sesuai dengan spriteNum
            }
        } else {
            spriteNum = 1; // Kembali ke posisi diam jika berhenti
        }

        // Collision Check
        if (!gp.collidesWithWall(nextX, y, this.hitbox) && !gp.collidesWithClosedGate(nextX, y, this.hitbox)) {
            x = nextX;
        }
        if (!gp.collidesWithWall(x, nextY, this.hitbox) && !gp.collidesWithClosedGate(x, nextY, this.hitbox)) {
            y = nextY;
        }
    }

    public void updateEffects() {
        if (slowEffectCounter > 0) {
            slowEffectCounter--;
            if (slowEffectCounter == 0) {
                speed = normalSpeed;
            }
        }
        if (damageCooldown > 0) {
            damageCooldown--;
        }
        // Tambahan logika attack cooldown:
        if (attackCooldown > 0) {
            attackCooldown--;
        } else {
            isAttacking = false;
        }

        // Update slash animation
        if (isSlashActive) {
            slashCounter++;
            if (slashCounter > 4) { // Ganti frame setiap 4 update
                slashFrame++;
                if (slashFrame >= 5) {
                    isSlashActive = false;
                    slashFrame = 0;
                }
                slashCounter = 0;
            }
        }
    }

    public void applySlow(int durationFrames) {
        slowEffectCounter = durationFrames;
        speed = slowSpeed;
    }

    public void draw(Graphics2D g2) {
        int camX = gp.getCameraXInt();
        int camY = gp.getCameraYInt();
        int drawY = y - camY;
        Image walkingImage = null;

        // EFEK VISUAL JALAN:
        // Jika sedang melangkah (spriteNum 2), gambar naik sedikit agar terlihat
        // seperti melangkah
        // Jika kamu sudah punya 2 gambar berbeda, kamu bisa mengganti gambarnya di
        // sini.
        if (spriteNum == 2) {
            drawY -= 4;
            // Ganti ke gambar berjalan
            walkingImage = walkImages[1]; // Contoh: potongan gambar untuk langkah kedua
        } else {
            walkingImage = currentImage; // Gambar diam
        }

        // --- DEBUG: GAMBAR KOTAK PUKULAN ---
        // --- DEBUG: GAMBAR KOTAK PUKULAN (Letakkan di akhir method draw) ---
        g2.setColor(new java.awt.Color(0, 0, 255, 150)); // INI HITBOX ATTACK PLAYER, HAPUS KALAU SUDAH JADI

        int attackDrawX = x - camX;
        int attackDrawY = y - camY;

        switch (direction) {
            case "UP":
                attackDrawY -= gp.getTileSize() - 8;
                break;
            case "DOWN":
                attackDrawY += gp.getTileSize() - 8;
                break;
            case "LEFT":
                attackDrawX -= gp.getTileSize() - 8;
                break;
            case "RIGHT":
                attackDrawX += gp.getTileSize() - 8;
                break;
        }

        // SWITCH DAN DEKLARASI VARIABEL JANGAN DIHAPUS

        g2.fillRect(attackDrawX, attackDrawY, gp.getTileSize(), gp.getTileSize()); // INI HITBOX ATTACK PLAYER, HAPUS
                                                                                   // KALAU SUDAH JADI

        AffineTransform originalTransform = g2.getTransform();

        int width = gp.getTileSize();
        int height = gp.getTileSize();
        int drawX = x - camX;

        // gambar menghadap ke arah kiri
        if (hadapKiri) {
            g2.translate(drawX + width, drawY); // Sesuaikan posisi setelah flip
            g2.scale(-1, 1); // Flip horizontal
            if (walkingImage != null) {
                g2.drawImage(walkingImage, 0, 0, width, height, null);
            }

        }
        if (!hadapKiri) {
            if (walkingImage != null) {
                g2.drawImage(walkingImage, drawX, drawY, gp.getTileSize(), gp.getTileSize(), null);
            }

        }
        g2.setTransform(originalTransform);

        // Render slash effect
        if (isSlashActive && slashImages[slashFrame] != null) {
            double centerX = x - camX + gp.getTileSize() / 2.0;
            double centerY = y - camY + gp.getTileSize() / 2.0;
            double offset = gp.getTileSize();

            switch (slashDirection) {
                case "UP":
                    centerY -= offset;
                    break;
                case "DOWN":
                    centerY += offset;
                    break;
                case "LEFT":
                    centerX -= offset;
                    break;
                case "RIGHT":
                    centerX += offset;
                    break;
            }

            int slashSize = 64;
            AffineTransform slashTransform = g2.getTransform();
            g2.translate(centerX, centerY);

            double angle = 0;
            switch (slashDirection) {
                case "RIGHT":
                    angle = 0;
                    break;
                case "DOWN":
                    angle = Math.toRadians(90);
                    break;
                case "LEFT":
                    angle = Math.toRadians(180);
                    break;
                case "UP":
                    angle = Math.toRadians(270);
                    break;
            }
            g2.rotate(angle);

            g2.drawImage(slashImages[slashFrame], -slashSize / 2, -slashSize / 2, slashSize, slashSize, null);
            g2.setTransform(slashTransform);
        }
    }

    /**
     * Fungsi untuk menyerang musuh.
     * Panggil fungsi ini dari GamePanel saat pemain menekan tombol serang (misal:
     * SPASI).
     */
    public void attackEnemies(java.util.ArrayList<Entity> monsters) {
        // Cek apakah serangan masih dalam masa cooldown
        if (attackCooldown > 0) {
            return; // Hentikan eksekusi, player belum boleh menyerang
        }

        // Jika berhasil menyerang, reset cooldown menjadi 60 frame (1 detik)
        attackCooldown = 60;

        // Trigger slash animation
        isSlashActive = true;
        slashFrame = 0;
        slashCounter = 0;
        slashDirection = direction;

        // 1. Buat ukuran kotak pukulan (besarnya 1 tile)
        Rectangle attackArea = new Rectangle();
        attackArea.width = gp.getTileSize() - 8;
        attackArea.height = gp.getTileSize() - 8;

        // 2. Tentukan posisi Kotak berdasarkan Arah
        switch (direction) {
            case "UP":
                attackArea.x = this.x;
                attackArea.y = this.y - attackArea.height; // Muncul di atas pemain
                break;
            case "DOWN":
                attackArea.x = this.x;
                attackArea.y = this.y + gp.getTileSize(); // Muncul di bawah pemain
                break;
            case "LEFT":
                attackArea.x = this.x - attackArea.width; // Muncul di kiri pemain
                attackArea.y = this.y;
                break;
            case "RIGHT":
                attackArea.x = this.x + gp.getTileSize(); // Muncul di kanan pemain
                attackArea.y = this.y;
                break;
        }

        // 3. Cek apakah ada monster yang terkena kotak pukulan ini
        for (Entity monster : monsters) {
            if (monster instanceof FireSlime) {
                FireSlime slime = (FireSlime) monster;

                if (attackArea.intersects(slime.getHitboxArea())) {
                    slime.takeDamage(this.attackDamage);
                    System.out.println("Berhasil memukul FireSlime dari arah: " + direction);
                }
            }

            if (monster instanceof Slime2) {
                Slime2 slime = (Slime2) monster;

                if (attackArea.intersects(slime.getHitboxArea())) {
                    slime.takeDamage(this.attackDamage);
                    System.out.println("Berhasil memukul Slime 2 dari arah: " + direction);
                }
            }

            if (monster instanceof Slime3) {
                Slime3 slime = (Slime3) monster;

                if (attackArea.intersects(slime.getHitboxArea())) {
                    slime.takeDamage(this.attackDamage);
                    System.out.println("Berhasil memukul Slime 3 dari arah: " + direction);
                }
            }
        }
    }

    /**
     * Method sentral untuk menerima damage dari sumber manapun (Monster/Trap)
     * Menggunakan sistem Defense persentase (Damage Reduction %)
     */
    public void terimaDamage(int damageAsli) {
        // 1. Batasi defense maksimal 100% agar damage tidak jadi minus (malah nge-heal)
        int persenDef = Math.min(this.defense, 100);

        // 2. Hitung rumus persentase: Damage Asli dikurangi (Persentase dari Damage
        // Asli)
        int damageAkhir = damageAsli - (damageAsli * persenDef / 100);

        // 3. Pastikan minimal damage adalah 0
        damageAkhir = Math.max(0, damageAkhir);

        // 4. Kurangi HP pemain
        this.darah.takeDamage(damageAkhir);

        System.out.println("Terkena Hit! Base Damage: " + damageAsli + " | Blocked: " + persenDef + "% | Damage Masuk: "
                + damageAkhir);
    }
}