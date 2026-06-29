package Entitiy;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import Entitiy.Activity.IAttackable;
import Main.GamePanel;
import Main.SoundManager;

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
    private final int attackDelay = 12; // delay untuk animasi serangan
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

    // Variabel untuk pergerakan AI
    public int actionLockCounter = 0; // Timer untuk ganti status
    public int actionTargetDuration = 60; // Target waktu (berubah-ubah nanti)
    public boolean isMoving = false; // Penanda apakah sedang jalan atau diam
    public String direction = "DOWN"; // Arah saat ini

    public boolean isChasing = false; // Penanda apakah sedang mengejar
    public int defaultSpeed; // Menyimpan kecepatan asli slime saat santai

    public Slime3(int x, int y, int speed, GamePanel gp) {
        super(x, y, speed, 50); // HP 50 untuk FireSlime
        this.gp = gp;
        this.defaultSpeed = speed;
        this.Activitynow = 0; // Mulai dengan status idle
        hitbox = new Rectangle();
        hitbox.x = 8;       // offset dari kiri sprite
        hitbox.y = 4;       // offset dari atas sprite
        hitbox.width = gp.getTileSize() - 16;  // lebar hitbox
        hitbox.height = gp.getTileSize() - 4; // tinggi hitbox

        attackHitbox = new Rectangle(); // area damage dari tengah slime
        attackHitbox.width = gp.getTileSize() * 2;  // UBAH JADI 2 TILE
        attackHitbox.height = gp.getTileSize() * 2; // UBAH JADI 2 TILE

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
            // --- 1. DETEKSI PLAYER DI AREA 5x5 ---
            if (getAggroArea().intersects(gp.player.getHitbox())) {
                isChasing = true;
                this.speed = gp.player.speed; 
            } else {
                isChasing = false;
                this.speed = defaultSpeed; 
            }

            // --- 2. CABANG AI (MENGEJAR vs BERSANTAI) ---
            if (isChasing) {
                // LOGIKA MENGEJAR PLAYER
                int nextX = x;
                int nextY = y;
                
                if (x < gp.player.x) { direction = "RIGHT"; nextX += speed; }
                if (x > gp.player.x) { direction = "LEFT"; nextX -= speed; }
                if (y < gp.player.y) { direction = "DOWN"; nextY += speed; }
                if (y > gp.player.y) { direction = "UP"; nextY -= speed; }

                if (!gp.collidesWithWall(nextX, y, hitbox) && !gp.collidesWithClosedGate(nextX, y, hitbox)) {
                    x = nextX;
                }
                if (!gp.collidesWithWall(x, nextY, hitbox) && !gp.collidesWithClosedGate(x, nextY, hitbox)) {
                    y = nextY;
                }

            } else {
                // LOGIKA RANDOM WANDER (ISTIRAHAT & JALAN ACAK)
                actionLockCounter++;
                if (actionLockCounter >= actionTargetDuration) {
                    isMoving = !isMoving; 
                    actionLockCounter = 0; 

                    java.util.Random random = new java.util.Random();
                    if (isMoving) {
                        int randomDetik = random.nextInt(4) + 1;
                        actionTargetDuration = randomDetik * 60;
                        
                        int i = random.nextInt(100) + 1;
                        if (i <= 25) { direction = "UP"; }
                        else if (i > 25 && i <= 50) { direction = "DOWN"; }
                        else if (i > 50 && i <= 75) { direction = "LEFT"; }
                        else { direction = "RIGHT"; }
                    } else {
                        actionTargetDuration = 500; // Istirahat 2 detik
                    }
                }

                if (isMoving) {
                    int nextX = x;
                    int nextY = y;
                    switch (direction) {
                        case "UP": nextY -= speed; break;
                        case "DOWN": nextY += speed; break;
                        case "LEFT": nextX -= speed; break;
                        case "RIGHT": nextX += speed; break;
                    }

                    boolean nabrakX = gp.collidesWithWall(nextX, y, hitbox) || gp.collidesWithClosedGate(nextX, y, hitbox);
                    boolean nabrakY = gp.collidesWithWall(x, nextY, hitbox) || gp.collidesWithClosedGate(x, nextY, hitbox);

                    if (!nabrakX) {
                        x = nextX;
                    }
                    if (!nabrakY) {
                        y = nextY;
                    }

                    if (nabrakX || nabrakY) {
                        java.util.Random random = new java.util.Random();
                        int i = random.nextInt(100) + 1;
                        if (i <= 25) { direction = "UP"; }
                        else if (i > 25 && i <= 50) { direction = "DOWN"; }
                        else if (i > 50 && i <= 75) { direction = "LEFT"; }
                        else { direction = "RIGHT"; }
                    }
                }
            }

            // --- 3. LOGIKA ANIMASI JALAN & IDLE ---
            // Berada di posisi paling bawah Activitynow == 0 agar selalu dieksekusi!
            animationCounter++;
            if (animationCounter >= animationDelay) {
                currentFrame = (currentFrame + 1) % animationFrames.length;
                animationCounter = 0;
            }

        } else if (Activitynow == 2) {
            // Logika animasi mati 
            disapearCounter++;
            if (disapearCounter >= disapearDelay) {
                disapearFrame++;
                if (disapearFrame >= disapearAnimation.length) {
                    disapearFrame = disapearAnimation.length - 1; 
                    this.readyToRemove = true; 
                }
                disapearCounter = 0;
            }
        } else if (Activitynow == 1) {
            // Logika animasi serangan
            attackCounter++;
            if (attackCounter >= attackDelay) {
                attackFrame++;
                if (attackFrame >= attackAnimation.length) {
                    attackFrame = 0; 
                    Activitynow = 0; 
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
            
            // Ambil langsung dari logika aslinya agar visual 100% cocok dengan serangan
            Rectangle attackArea = getAttackHitboxArea();
            
            // Konversi dari koordinat dunia (World) ke koordinat layar (Screen)
            int attackDrawX = attackArea.x - camX;
            int attackDrawY = attackArea.y - camY;
            
            g2.fillRect(attackDrawX, attackDrawY, attackArea.width, attackArea.height);
        }

        // --- DEBUG: GAMBAR RADAR 5x5 SLIME (HIJAU) ---
        g2.setColor(new java.awt.Color(0, 255, 0, 80)); // Hijau sangat pudar
        Rectangle radar = getAggroArea();
        g2.fillRect(radar.x - camX, radar.y - camY, radar.width, radar.height);

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
    /**
     * Mendapatkan area jangkauan serangan Slime (ukuran 2x2 tile)
     * Posisi dihitung agar persis di tengah badan slime
     */
    public Rectangle getAttackHitboxArea() {
        // 1. Cari titik koordinat TEPAT DI TENGAH badan (hitbox merah) Slime
        int centerX = this.x + hitbox.x + (hitbox.width / 2);
        int centerY = this.y + hitbox.y + (hitbox.height / 2);
        
        // 2. Tarik ke kiri dan ke atas sejauh SETENGAH dari ukuran area serangan
        // Agar titik tengah area serangan menyatu dengan titik tengah Slime
        int areaX = centerX - (attackHitbox.width / 2);
        int areaY = centerY - (attackHitbox.height / 2);
        
        return new Rectangle(areaX, areaY, attackHitbox.width, attackHitbox.height);
    }


    public Rectangle getAggroArea() {
        int areaWidth = gp.getTileSize() * 7;
        int areaHeight = gp.getTileSize() * 7;
        
        // Geser X dan Y sejauh 2 tile ke kiri dan ke atas agar pas di tengah
        int areaX = this.x - (gp.getTileSize() * 3);
        int areaY = this.y - (gp.getTileSize() * 3);
        
        return new Rectangle(areaX, areaY, areaWidth, areaHeight);
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

        // 1. TRIGGER ANIMASI: Cek apakah Player masuk ke area serangan (3x3)
        if (this.getAttackHitboxArea().intersects(player.getHitbox())) {
            
            // --- TAMBAHAN SISTEM ANTI TEMBUS TEMBOK (LINE OF SIGHT) ---
            // Ambil titik tengah (center) dari badan Slime
            int slimeCenterX = this.x + hitbox.x + (hitbox.width / 2);
            int slimeCenterY = this.y + hitbox.y + (hitbox.height / 2);
            
            // Ambil titik tengah (center) dari badan Player
            int playerCenterX = player.x + player.hitbox.x + (player.hitbox.width / 2);
            int playerCenterY = player.y + player.hitbox.y + (player.hitbox.height / 2);

            // Cari koordinat tepat di tengah-tengah antara Slime dan Player
            int midX = (slimeCenterX + playerCenterX) / 2;
            int midY = (slimeCenterY + playerCenterY) / 2;

            // Buat kotak bayangan kecil (8x8 pixel) di titik tengah tersebut
            Rectangle lineOfSight = new Rectangle(-4, -4, 8, 8);

            // Cek apakah di tengah-tengah mereka ada tembok atau gerbang tertutup
            if (gp.collidesWithWall(midX, midY, lineOfSight) || gp.collidesWithClosedGate(midX, midY, lineOfSight)) {
                // Terhalang tembok! Hentikan fungsi di sini agar Slime tidak menyerang
                return; 
            }
            // ----------------------------------------------------------

            // Jika tidak terhalang tembok, Slime bersiap menyerang
            if (this.Activitynow == 0) {
                this.Activitynow = 1;      // Mulai serang
                this.attackFrame = 0;
                this.attackCounter = 0;
                this.hasDealtDamage = false; // Reset penanda pukulan
                SoundManager.play("/Assets/Sound/Slime3Nembak.wav");
            }
        }

        // 2. TRIGGER DAMAGE: Berikan damage TEPAT di pertengahan animasi (misal Frame ke-5)
        if (this.Activitynow == 1 && this.attackFrame == 5 && !this.hasDealtDamage) {
            
            // Cek lagi, apakah player MASIH ada di area saat pukulan mendarat?
            if (this.getAttackHitboxArea().intersects(player.getHitbox())) {
                
                // --- CEK TEMBOK LAGI SAAT DAMAGE MASUK ---
                // (Untuk berjaga-jaga jika player lari bersembunyi ke balik tembok saat animasi memukul diputar)
                int sCX = this.x + hitbox.x + (hitbox.width / 2);
                int sCY = this.y + hitbox.y + (hitbox.height / 2);
                int pCX = player.x + player.hitbox.x + (player.hitbox.width / 2);
                int pCY = player.y + player.hitbox.y + (player.hitbox.height / 2);
                int mX = (sCX + pCX) / 2;
                int mY = (sCY + pCY) / 2;
                Rectangle los = new Rectangle(-4, -4, 8, 8);
                
                if (!gp.collidesWithWall(mX, mY, los) && !gp.collidesWithClosedGate(mX, mY, los)) {
                    
                    // Pemain benar-benar terkena pukulan tanpa halangan tembok
                    player.terimaDamage(damage); 
                    System.out.println("BAM! Pukulan Slime mendarat!");
                    
                    this.hasDealtDamage = true; 
                    player.damageCooldown = 30; 
                }
            }
        }
    }
}
