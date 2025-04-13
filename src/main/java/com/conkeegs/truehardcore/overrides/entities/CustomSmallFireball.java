package com.conkeegs.truehardcore.overrides.entities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class CustomSmallFireball extends SmallFireball {
    public CustomSmallFireball(Level p_37367_, double p_37368_, double p_37369_, double p_37370_, double p_37371_,
            double p_37372_, double p_37373_) {
        super(p_37367_, p_37368_, p_37369_, p_37370_, p_37371_, p_37372_, p_37373_);
    }

    @Override
    protected void onHitEntity(EntityHitResult p_37386_) {
        super.onHitEntity(p_37386_);
        if (!this.level().isClientSide) {
            Entity entity = p_37386_.getEntity();
            Entity entity1 = this.getOwner();
            int i = entity.getRemainingFireTicks();
            entity.setSecondsOnFire(5);
            if (!entity.hurt(this.damageSources().fireball(this, entity1), 8.0F)) {
                entity.setRemainingFireTicks(i);
            } else if (entity1 instanceof LivingEntity) {
                this.doEnchantDamageEffects((LivingEntity) entity1, entity);
            }

        }
    }
}
