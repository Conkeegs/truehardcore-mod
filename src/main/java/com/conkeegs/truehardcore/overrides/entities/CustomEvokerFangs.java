package com.conkeegs.truehardcore.overrides.entities;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;

public class CustomEvokerFangs extends EvokerFangs {
   public CustomEvokerFangs(EntityType<? extends EvokerFangs> p_36923_, Level p_36924_) {
      super(p_36923_, p_36924_);
   }

   public CustomEvokerFangs(Level p_36926_, double p_36927_, double p_36928_, double p_36929_, float p_36930_,
         int p_36931_, LivingEntity p_36932_) {
      super(p_36926_, p_36927_, p_36928_, p_36929_, p_36930_, p_36931_, p_36932_);
   }

   private void dealDamageTo(LivingEntity p_36945_) {
      LivingEntity livingentity = this.getOwner();
      if (p_36945_.isAlive() && !p_36945_.isInvulnerable() && p_36945_ != livingentity) {
         if (livingentity == null) {
            p_36945_.hurt(DamageSource.MAGIC, 7.0F);
         } else {
            if (livingentity.isAlliedTo(p_36945_)) {
               return;
            }

            p_36945_.hurt(DamageSource.indirectMagic(this, livingentity), 6.0F);
         }

      }
   }
}
