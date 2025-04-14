package com.conkeegs.truehardcore.overrides.entities;

import org.slf4j.Logger;

import com.conkeegs.truehardcore.utils.TruestLogger;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/**
 * Custom small fireball that does more damage.
 */
public class CustomSmallFireball extends SmallFireball {
    private static final Logger LOGGER = TruestLogger.getLogger();

    /**
     * Custom small fireball constructor.
     *
     * @param fireballLevel the level the fireball is in
     * @param targetX       target's x position
     * @param targetY       target's y position
     * @param targetZ       target's z position
     * @param directionX    target's x direction
     * @param directionY    target's y direction
     * @param directionZ    target's z direction
     */
    public CustomSmallFireball(Level fireballLevel, double targetX, double targetY, double targetZ, double directionX,
            double directionY, double directionZ) {
        super(fireballLevel, targetX, targetY, targetZ, directionX, directionY, directionZ);
    }

    /**
     * Determine what happens after fireball hits its target.
     */
    @Override
    protected void onHitEntity(EntityHitResult p_37386_) {
        if (!this.level().isClientSide) {
            Entity entity = p_37386_.getEntity();
            Entity entity1 = this.getOwner();
            int i = entity.getRemainingFireTicks();
            entity.setSecondsOnFire(5);
            if (!entity.hurt(this.damageSources().fireball(this, entity1), 11.0F)) {
                entity.setRemainingFireTicks(i);
            } else if (entity1 instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity) entity1, entity);
            }

        }
    }
}
