package Entitiy;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import Entitiy.Activity.IAttackable;
import Main.GamePanel;

public class Slime3 extends Entity implements IAttackable {
    private BufferedImage bufferedImage;
    private BufferedImage[] animationFrames = new BufferedImage[6]; // untuk jalan dan idle
    private BufferedImage[] disapearAnimation = new BufferedImage[10]; // untuk animasi mati
    private BufferedImage[] attackAnimation = new BufferedImage[9]; // untuk animasi serangan
    private int currentFrame = 0;
    private int disapearFrame = 0;
    private int attackFrame = 0;
    private int animationCounter = 0; // untuk animasi jalan dan idle
    private int disapearCounter = 0; // untuk animasi mati
    private int attackCounter = 0; // untuk animasi serangan
    private final int animationDelay = 10; // delay untuk animasi jalan dan idle
    private final int disapearDelay = 10; // delay untuk animasi mati
    private final int attackDelay = 20; // delay untuk animasi serangan
    private int width = 32;
    private int height = 32;
    GamePanel gp;
    public int Activitynow = 0; // 0 = idle, 1 = attack, 2 = die
    public Rectangle hitbox;
    public Rectangle attackHitbox;
    public boolean readyToRemove = false; // Penanda untuk dihapus dari ArrayList
    private final int damage = 10;
    public boolean hasDealtDamage = false; // Mencegah damage berkali-kali dalam 1 pukulan
    public int currentHp = 50;

    public Slime3(int x, int y, int speed, GamePanel gp) {
        super(x, y, speed, 50); // HP 50 untuk FireSlime
        this.gp = gp;
        this.Activitynow = 0; // Mulai dengan status idle
        hitbox = new Rectangle();
        hitbox.x = 8;       // offset dari kiri sprite
        hitbox.y = 4;       // offset dari atas sprite
        hitbox.width = gp.getTileSize() - 16;  // lebar hitbox
        hitbox.height = gp.getTileSize() - 4; // tinggi hitbox

        attackHitbox = new Rectangle(); // area damage dari tengah slime
        attackHitbox.width = gp.getTileSize() * 3; 
        attackHitbox.height = gp.getTileSize() * 3; // tinggi area serangan sebesar 3 tile

        try {
            int spriteWidth = 64; // Lebar setiap sprite
            int spriteHeight = 64; // Tinggi setiap sprite
            int rowY = 0; // Y untuk baris pertama
            this.bufferedImage = loadBufferedImage("/Assets/Enemy/PNG/Slime3/With_Shadow/Slime3_Idle_with_shadow.png");
            for (int i = 0; i < animationFrames.length; i++) {
                int colX = i * spriteWidth; // X untuk setiap kolom
                animationFrames[i] = bufferedImage.getSubimage(colX, rowY, spriteWidth, spriteHeight);
                System.out.println("tes slime idle");
            }
            // load disappear animation

            this.bufferedImage = loadBufferedImage("/Assets/Enemy/PNG/Slime3/With_Shadow/Slime3_Death_with_shadow.png");
            for (int i = 0; i < disapearAnimation.length; i++) {
                int colX = i * spriteWidth; // X untuk setiap kolom
                disapearAnimation[i] = bufferedImage.getSubimage(colX, rowY, spriteWidth, spriteHeight);
            }

            // loaad attack animation
            this.bufferedImage = loadBufferedImage("/Assets/Enemy/PNG/Slime3/With_Shadow/Slime3_Attack_with_shadow.png");
            for (int i = 0; i < attackAnimation.length; i++) {
                int colX = i * spriteWidth; // X untuk setiap kolom
                attackAnimation[i] = bufferedImage.getSubimage(colX, rowY, spriteWidth, spriteHeight);
            }
        } catch (Exception e) {
            System.out.println("Error loading animation sheet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void update() {
        if (Activitynow == 0) {
            // Logika pergerakan atau perilaku slime di sini
            animationCounter++;
            if (animationCounter >= animationDelay) {
                currentFrame = (currentFrame + 1) % animationFrames.length;
                animationCounter = 0;
            }
        } else if (Activitynow == 2) {
            // Logika animasi mati (diperbaiki agar menggunakan counter delay)
            disapearCounter++;
            if (disapearCounter >= disapearDelay) {
                disapearFrame++;
                // Jika sudah mencapai frame terakhir animasi mati
                if (disapearFrame >= disapearAnimation.length) {
                    disapearFrame = disapearAnimation.length - 1; // tetap di frame terakhir
                    this.readyToRemove = true; // Tandai bahwa slime sudah siap dihapus dari map
                }
                disapearCounter = 0;
            }
        } else if (Activitynow == 1) {
            // Logika animasi serangan
            attackCounter++;
            if (attackCounter >= attackDelay) {
                attackFrame++;
                if (attackFrame >= attackAnimation.length) {
                    attackFrame = 0; // kembali ke frame pertama setelah selesai
                    Activitynow = 0; // kembali ke idle setelah serangan selesai
                }
                attackCounter = 0;
            }
        }
    }

    public void draw(Graphics2D g2) {
        int camX = gp.getCameraXInt();
        int camY = gp.getCameraYInt();
        int screenX = x - camX;
        int screenY = y - camY;

        // --- TRIK KOTAK MERAH (DEBUG) ---
        // Ini akan menggambar kotak merah terang sebagai pengganti slime
        g2.setColor(new java.awt.Color(255, 0, 0, 150)); // Merah semi-transparan
        g2.fillRect(screenX, screenY, width, height);

        if (Activitynow == 1) { // GAMBAR HITBOX ATTACK AREA SLIME, HAPUS AJA KALAU GAME SUDAH JADI
            g2.setColor(new java.awt.Color(255, 165, 0, 150)); // Oranye semi-transparan
            
            // Hitung posisi X dan Y agar area 3x3 ini berada persis di tengah Slime
            int attackDrawX = screenX - gp.getTileSize();
            int attackDrawY = screenY - gp.getTileSize();
            
            g2.fillRect(attackDrawX, attackDrawY, attackHitbox.width, attackHitbox.height);
        }

        // Hanya gambar jika masuk ke dalam pandangan monitor
        if (x + width > camX &&
            x - width < camX + gp.screenWidth &&
            y + height > camY &&
            y - height < camY + gp.screenHeight) {

            if (Activitynow == 0) {
                if (animationFrames[currentFrame] != null) {
                    g2.drawImage(animationFrames[currentFrame], screenX - 16, screenY - 16, 64, 64, null);
                }
            } else if (Activitynow == 2) {
                if (disapearAnimation[disapearFrame] != null) {
                    g2.drawImage(disapearAnimation[disapearFrame], screenX - 16, screenY - 16, 64, 64, null);
                }
            } else if (Activitynow == 1) {
                if (attackAnimation[attackFrame] != null) {
                    g2.drawImage(attackAnimation[attackFrame], screenX - 16, screenY - 16, 64, 64, null);
                }
            }
        }
    }

    @Override
    public void takeDamage(int damageAmount) {
        if (Activitynow == 2) {
            return;
        }

        this.currentHp -= damageAmount;
        System.out.println("Slime 3 terkena serangan! Sisa HP: " + this.currentHp);

        // Jika HP habis, ubah state menjadi mati
        if (this.currentHp <= 0) {
            this.currentHp = 0;
            this.Activitynow = 2; // memicu animasi mati disapearAnimation
        }
    }

    @Override
    public boolean isDead() {
        return Activitynow == 2;
    }

    public Rectangle getHitboxArea() {
        return new Rectangle(x + hitbox.x, y + hitbox.y, hitbox.width, hitbox.height);
    }

    /**
     * Mendapatkan area jangkauan serangan Slime (ukuran 3x3 tile)
     */
    public Rectangle getAttackHitboxArea() {
        // Geser X dan Y sejauh 1 tile ke kiri dan ke atas
        // agar Slime (yang ukurannya 1 tile) berada tepat di tengah area 3x3 ini
        int areaX = this.x - gp.getTileSize();
        int areaY = this.y - gp.getTileSize();
        
        return new Rectangle(areaX, areaY, attackHitbox.width, attackHitbox.height);
    }

    /**
     * Mengecek apakah slime menabrak player.
     * Jika menabrak, slime akan menyerang dan player menerima damage.
     */
    public void checkPlayerCollision(Player player) {
        // Jika slime sudah mati, hentikan pengecekan
        if (this.Activitynow == 2) {
            return;
        }

        // 1. TRIGGER ANIMASI: Jika player masuk area, Slime bersiap menyerang
        if (this.getAttackHitboxArea().intersects(player.getHitbox())) {
            if (this.Activitynow == 0) {
                this.Activitynow = 1;      // Mulai serang
                this.attackFrame = 0;
                this.attackCounter = 0;
                this.hasDealtDamage = false; // Reset penanda pukulan
            }
        }

        // 2. TRIGGER DAMAGE: Berikan damage TEPAT di pertengahan animasi (misal Frame ke-5)
        // Angka 5 bisa kamu ganti (0-9) sesuai gambar frame mana yang paling pas terlihat memukul
        if (this.Activitynow == 1 && this.attackFrame == 5 && !this.hasDealtDamage) {
            
            // Cek lagi, apakah player MASIH ada di area saat pukulan mendarat?
            // (Ini memberi kesempatan player untuk menghindar dengan mundur)
            if (this.getAttackHitboxArea().intersects(player.getHitbox())) {
                
                player.darah.takeDamage(damage); 
                System.out.println("BAM! Pukulan Slime mendarat!");
                
                this.hasDealtDamage = true; // Kunci agar damage tidak dobel di frame yang sama
                
                // Efek kebal singkat untuk player (hanya 30 frame / 0.5 detik)
                // Karena delay serangan asli sekarang murni diatur oleh durasi animasi Slime
                player.damageCooldown = 30; 
            }
        }
    }
}
