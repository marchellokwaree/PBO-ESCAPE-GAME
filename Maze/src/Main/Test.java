package Main;

public class Test {
    public static void main(String[] args) {
        try {
            System.out.println("Instantiating GamePanel...");
            GamePanel gp = new GamePanel();
            System.out.println("Success!");
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(0);
    }
}
