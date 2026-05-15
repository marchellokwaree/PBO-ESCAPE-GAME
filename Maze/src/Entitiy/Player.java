package Entitiy;

import java.awt.Image;
import Main.GamePanel;

public class Player extends Entity {
    GamePanel gp;
    Image img;

    public Player(Image img, int x, int y) {
        super(x, y, 0); // speed default 0
        this.img = img;
    }
}
