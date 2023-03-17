package com.segoitch.randomcrits;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public final class RandomCrits extends JavaPlugin implements Listener {
    private double baseCriticalChance = getConfig().getDouble("baseCriticalChance");
    private double baseCriticalMultiplier = getConfig().getDouble("baseCriticalMultiplier");
    private boolean bowCritical = false;
    //boolean isAttack = false;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    /*@EventHandler
    public void randomCritical(EntityDamageByEntityEvent event) {
        isAttack = true;
        event.getDamager().sendMessage("Damage is: " + event.getDamage());
        Entity damagee = event.getEntity();
        if (event.getDamager() instanceof Player) {
            Player player = (Player)event.getDamager();
            if(event.isCritical() && isAttack) {
                event.setCancelled(true);
                player.attack(damagee);
                isAttack = false;
                event.getDamager().sendMessage("Final damage: " + event.getDamage());
            }
        }
    }*/

    boolean isPlayerAttackCritical(Entity damager) {
        if (damager instanceof Player) {
            Player player = (Player) damager;
            if (damager.getFallDistance() > 0.0F
                    && !player.isOnGround() && !player.isClimbing() && !player.isInWater()
                    && !player.hasPotionEffect(PotionEffectType.BLINDNESS)
                    && player.getPassengers().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void randomCritical(EntityDamageByEntityEvent event) {
        double damage = event.getDamage() * baseCriticalMultiplier;
        Entity damager = event.getDamager();
        Entity damagee = event.getEntity();

        if(Math.random() <= baseCriticalChance && !isPlayerAttackCritical(damager) && !bowCritical) {
            event.setDamage(damage);
            damagee.playEffect(EntityEffect.HURT);
            damagee.getWorld().spawnParticle(Particle.CRIT, damagee.getLocation(), 16);
            damagee.getWorld().playSound(damagee, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1 , 1);
        }
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            if(event.getForce() >= 1) {
                bowCritical = true;
            }
        }
        else {
            bowCritical = false;
        }
    }
}