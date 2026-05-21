package Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Darah {
    private int currentHP;
    private final int maxHP;

    public Darah() {
        this.maxHP = 100;
        this.currentHP = maxHP;
    }

    public void update(int hp) {
        if (hp < 0) {
            hp = 0;
        }
        if (hp > maxHP) {
            hp = maxHP;
        }
        this.currentHP = hp;
    }

    public void draw(Graphics2D g2) {
        int x = 14;
        int y = 10;
        int width = 212;
        int height = 28;
        int radius = 12;

        // Background panel with transparansi agar tidak terlalu menutupi
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(x, y, width, height, radius, radius);

        // Border panel
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, width, height, radius, radius);

        int innerX = x + 4;
        int innerY = y + 4;
        int innerWidth = width - 8;
        int innerHeight = height - 8;

        // Health bar background
        g2.setColor(new Color(60, 60, 60, 200));
        g2.fillRoundRect(innerX, innerY, innerWidth, innerHeight, radius, radius);

        // Health fill
        int barWidth = (int) ((currentHP / (double) maxHP) * innerWidth);
        Color hpColor = currentHP > 50 ? new Color(220, 40, 40) : currentHP > 20 ? new Color(235, 150, 40) : new Color(200, 60, 60);
        g2.setColor(hpColor);
        g2.fillRoundRect(innerX, innerY, Math.max(barWidth, 2), innerHeight, radius, radius);

        // Inner border
        g2.setColor(new Color(255, 255, 255, 180));
        g2.drawRoundRect(innerX, innerY, innerWidth, innerHeight, radius, radius);

        // Text dan ikon
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(Color.WHITE);
        g2.drawString("HP", innerX + 8, innerY + innerHeight - 6);
        g2.drawString(currentHP + " / " + maxHP, innerX + innerWidth - 60, innerY + innerHeight - 6);
    }
}
