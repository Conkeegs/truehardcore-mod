package com.conkeegs.truehardcore.overrides.entities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/**
 * Custom large fireball that does more damage.
 */
public class CustomLargeFireball extends LargeFireball {
    /**
     * Custom large fireball constructor.
     *
     * @param fireballWorld         the level the fireball is in
     * @param fireballTarget        the fireball's target entity
     * @param targetX               target's x position
     * @param targetY               target's y position
     * @param targetZ               target's z position
     * @param defaultExplosionPower fireball's default explosion power
     */
    public CustomLargeFireball(Level fireballWorld, LivingEntity fireballTarget, double targetX, double targetY,
            double targetZ, int defaultExplosionPower) {
        super(fireballWorld, fireballTarget, targetX, targetY, targetZ, defaultExplosionPower);
    }

    /**
     * Determine what happens after fireball hits its target.
     */
    protected void onHitEntity(EntityHitResult p_37216_) {
        super.onHitEntity(p_37216_);
        if (!this.level().isClientSide) {
            Entity entity = p_37216_.getEntity();
            Entity entity1 = this.getOwner();
            entity.hurt(this.damageSources().fireball(this, entity1), 13.0F);
            if (entity1 instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity) entity1, entity);
            }

        }
    }
}
