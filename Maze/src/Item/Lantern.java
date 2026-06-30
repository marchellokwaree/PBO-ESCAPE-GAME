package Item;

import Entitiy.Player;
import Main.GamePanel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class Lantern extends Item {
    public int bonusSafeVision;
    public int bonusRange;
    private BufferedImage[] animationFrames = new BufferedImage[4];
    private int currentFrame = 0;
    private int animationCounter = 0;
    private final int animationDelay = 6;
    private BufferedImage spriteSheet;

    public Lantern(String name, int safeVisionBonus, int rangeBonus) {
        this.name = name;
        this.bonusSafeVision = safeVisionBonus;
        this.bonusRange = rangeBonus;
        try {
            this.spriteSheet = loadBufferedImage("/Assets/ASSET/Lantern/Lantern.png");
            int spriteWidth = 43;
            int spriteHeight = 39;
            int rowY = 1 * spriteHeight;
            for (int i = 0; i < animationFrames.length; i++) {
                int colX = i * spriteWidth;
                animationFrames[i] = spriteSheet.getSubimage(colX, rowY, spriteWidth, spriteHeight);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAnimation() {
        if (animationFrames == null || animationFrames.length == 0) {
            return;
        }
        animationCounter++;
        if (animationCounter >= animationDelay) {
            animationCounter = 0;
            currentFrame++;
            if (currentFrame >= animationFrames.length) {
                currentFrame = 0;
            }
        }
    }

    public BufferedImage getAnimatedFrame() {
        if (animationFrames != null && animationFrames.length > 0) {
            updateAnimation();
            return animationFrames[currentFrame];
        }
        return this.image;
    }

    @Override
    public void draw(Graphics2D g2, int x, int y, int width, int height) {
        BufferedImage frame = (animationFrames != null && animationFrames.length > 0) ? animationFrames[currentFrame]
                : this.image;
        if (frame != null) {
            g2.drawImage(frame, x, y, width, height, null);
        }
    }

    @Override
    public void use(Player p) {
        GamePanel gp = p.getGamePanel();
        if (gp != null) {
            gp.safeVisionTiles += bonusSafeVision;
            gp.visionRangeTiles += bonusRange;
            System.out.println(
                    this.name + " di-equip! Safe Vision +" + bonusSafeVision + ", Vision Range +" + bonusRange);
        }
    }

    protected BufferedImage loadBufferedImage(String path) {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream != null) {
                return ImageIO.read(stream);
            }
            File file = resolveFile(path);
            return ImageIO.read(file);
        } catch (IOException e) {
            System.err.println("Failed to load player buffered image: " + path + " -> " + e.getMessage());
            return null;
        }
    }

    protected File resolveFile(String path) {
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
}
