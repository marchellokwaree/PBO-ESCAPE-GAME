package Entitiy.Activity;

public interface IAttackable {
    void takeDamage(int damageAmount);

    /**
     * Method untuk mengecek apakah entitas sudah mati.
     */
    boolean isDead();
    
}
