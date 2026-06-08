package Entitiy;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import Main.GamePanel;

public class FireSlime extends Entity {
    private BufferedImage bufferedImage;
    private BufferedImage[] animationFrames = new BufferedImage[6]; // untuk jalan dan idle
    private BufferedImage[] disapearAnimation = new BufferedImage[10]; // untuk animasi mati
    private BufferedImage[] attackAnimation = new BufferedImage[10]; // untuk animasi serangan
    private int currentFrame = 0;
    private int disapearFrame = 0;
    private int attackFrame = 0;
    private int animationCounter = 0; // untuk animasi jalan dan idle
    private int disapearCounter = 0; // untuk animasi mati
    private int attackCounter = 0; // untuk animasi serangan
    private final int animationDelay = 20;
    private final int disapearDelay = 10;
    private final int attackDelay = 15;
    private int width = 32;
    private int height = 32;
    GamePanel gp;
    public int Activitynow = 0; // 0 = idle, 1 = attack, 2 = die
    public Rectangle hitbox;
    public Rectangle attackHitbox;

    public FireSlime(int x, int y, int speed, GamePanel gp) {
        super(x, y, speed, 50); // HP 50 untuk FireSlime
        this.gp = gp;
        this.Activitynow = 0; // Mulai dengan status idle
        hitbox = new Rectangle();
        hitbox.x = 8;       // offset dari kiri sprite
        hitbox.y = 4;       // offset dari atas sprite
        hitbox.width = gp.getTileSize() - 16;  // lebar hitbox
        hitbox.height = gp.getTileSize() - 4; // tinggi hitbox

        attackHitbox = new Rectangle(); // area damage dari tengah slime
        attackHitbox.width = gp.getTileSize() * 3; // area serangan sebesar 3 tile
        attackHitbox.height = gp.getTileSize() * 3; // tinggi area serangan sebesar 3 tile

        try {
            int spriteWidth = 64; // Lebar setiap sprite
            int spriteHeight = 64; // Tinggi setiap sprite
            int rowY = 0; // Y untuk baris pertama
            this.bufferedImage = loadBufferedImage("/Assets/Enemy/PNG/Slime1/With_Shadow/Slime1_Idle_with_shadow.png");
            for (int i = 0; i < animationFrames.length; i++) {
                int colX = i * spriteWidth; // X untuk setiap kolom
                animationFrames[i] = bufferedImage.getSubimage(colX, rowY, spriteWidth, spriteHeight);
                System.out.println("tes slime idle");
            }
            // load disappear animation

            this.bufferedImage = loadBufferedImage("/Assets/Enemy/PNG/Slime1/With_Shadow/Slime1_Death_with_shadow.png");
            for (int i = 0; i < disapearAnimation.length; i++) {
                int colX = i * spriteWidth; // X untuk setiap kolom
                disapearAnimation[i] = bufferedImage.getSubimage(colX, rowY, spriteWidth, spriteHeight);
            }

            // loaad attack animation
            this.bufferedImage = loadBufferedImage("/Assets/Enemy/PNG/Slime1/With_Shadow/Slime1_Attack_with_shadow.png");
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
            // Logika animasi mati
            disapearFrame++;
            if (disapearFrame >= disapearAnimation.length) {
                disapearFrame = disapearAnimation.length - 1; // tetap di frame terakhir
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



}
