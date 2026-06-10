package Main;
import Entitiy.Entity;
import java.util.Random;
import Entitiy.FireSlime;
import Entitiy.Slime2;

import java.util.ArrayList;
public class MonsterSpawner {
    private GamePanel gp;
    private Random random = new Random();
    private ArrayList<Entity> monsters = new ArrayList<>();
    private final int maxMonsters = 15; // Jumlah maksimum monster yang bisa muncul
    private final int spawnInterval = 10000; // Interval spawn dalam milidetik (10 detik)
    private int spawnTimer = 0; // Timer untuk menghitung waktu spawn
    
    public MonsterSpawner(GamePanel gp) {
        this.gp = gp;
    }

    public void update() {
        spawnTimer++;
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0; // Reset timer setelah spawn
            if (gp.monsters.size() < maxMonsters) {
                SpawnMonsters(2); // Spawn 2 monster setiap interval
            }
        }
    }

    public void SpawnMonsters( int jumlah) {
        int maxMonsters = jumlah; // Jumlah maksimum monster yang bisa muncul
        int spawned = 0;

        while (spawned < maxMonsters) {
            int x = random.nextInt(gp.getMaxWorldCol() -1 ); 
            int y = random.nextInt(gp.getMaxWorldRow() -1 ); 

            // Cek apakah posisi spawn valid (tidak menabrak dinding atau pintu)
            if (gp.getMap()[y][x].trim().equals("0")) {
                int pixelX = x * gp.getTileSize();
                int pixelY = y * gp.getTileSize();
                
                // Cek apakah player sudah ada dan jarak spawn tidak terlalu dekat dengan player
                if (gp.player != null) {
                    if (Math.abs(pixelX - gp.player.x) > gp.getTileSize() * 3 || 
                        Math.abs(pixelY - gp.player.y) > gp.getTileSize() * 3) {

                        Random rng = new Random();
                        int rndm = rng.nextInt(2);
                        if(rndm == 0){
                            FireSlime newMonster = new FireSlime(pixelX, pixelY, 1, gp);
                            gp.monsters.add(newMonster);
                            spawned++;
                            System.out.println("Berhasil! Spawned FireSlime at Pixel: (" + pixelX + ", " + pixelY + ")");
                        }else if(rndm == 1){
                            Slime2 newMonster = new Slime2(pixelX, pixelY, 1, gp);
                            gp.monsters.add(newMonster);
                            spawned++;
                            System.out.println("Berhasil! Spawned Slime 2 at Pixel: (" + pixelX + ", " + pixelY + ")");
                        }

                    }
                }
                // System.out.println("Spawned FireSlime at: (" + x + ", " + y + ")");
            }
        }

    }
}
