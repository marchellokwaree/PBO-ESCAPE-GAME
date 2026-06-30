package Main;

import java.util.Random;

public class TrapRandomizer {

    public static void randomize(String[][] map, double fireChance, double iceChance) {
        if (map == null || map.length == 0) {
            return;
        }

        int startRow = -1;
        int startCol = -1;

        // 1. Locate the player starting point 'S'
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if ("S".equals(map[i][j])) {
                    startRow = i;
                    startCol = j;
                    break;
                }
            }
            if (startRow != -1) {
                break;
            }
        }

        // 2. Randomly replace floor tiles ('0') with traps ('F' or 'I')
        Random rand = new Random();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if ("0".equals(map[i][j])) {
                    // Prevent placing traps within a 2-tile radius of the spawn point 'S'
                    if (startRow != -1 && startCol != -1) {
                        int rowDist = Math.abs(i - startRow);
                        int colDist = Math.abs(j - startCol);
                        if (rowDist <= 2 && colDist <= 2) {
                            continue;
                        }
                    }

                    double roll = rand.nextDouble();
                    if (roll < fireChance) {
                        map[i][j] = "F";
                    } else if (roll < fireChance + iceChance) {
                        map[i][j] = "I";
                    }
                }
            }
        }
    }
}
