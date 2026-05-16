package Entitiy;

import java.awt.Graphics2D;
import java.awt.Image;
import Main.GamePanel;
import Main.KeyHandler; 

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;
    Image img;

    public Player(GamePanel gp, KeyHandler keyH, Image img, int x, int y) {
        super(x, y, 4); // Set speed jadi 4 biar enak tesnya
        this.gp = gp;
        this.keyH = keyH;
        this.img = img;
    }

    public void update() {
        int nextX = x;
        int nextY = y;

        if (keyH.upPressed) nextY -= speed;
        if (keyH.downPressed) nextY += speed;
        if (keyH.leftPressed) nextX -= speed;
        if (keyH.rightPressed) nextX += speed;

        if (!gp.collidesWithWall(nextX, nextY)) {
            x = nextX;
            y = nextY;
        } else {
            // Jika gerakan diagonal tertahan, biarkan gerakan sumbu tunggal jika memungkinkan
            if (nextX != x && !gp.collidesWithWall(nextX, y)) {
                x = nextX;
            }
            if (nextY != y && !gp.collidesWithWall(x, nextY)) {
                y = nextY;
            }
        }
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(img, x, y, gp.getTileSize(), gp.getTileSize(), null);
    }
}