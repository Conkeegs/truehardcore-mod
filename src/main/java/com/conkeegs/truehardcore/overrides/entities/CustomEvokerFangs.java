package com.conkeegs.truehardcore.overrides.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;

public class CustomEvokerFangs extends EvokerFangs {
   public CustomEvokerFangs(EntityType<? extends EvokerFangs> p_36923_, Level p_36924_) {
      super(p_36923_, p_36924_);
   }

   public CustomEvokerFangs(Level fangLevel, double fangX, double fangY, double fangZ, float p_36930_,
         int p_36931_, LivingEntity p_36932_) {
      super(fangLevel, fangX, fangY, fangZ, p_36930_, p_36931_, p_36932_);
   }

   private void dealDamageTo(LivingEntity p_36945_) {
      LivingEntity livingentity = this.getOwner();
      if (p_36945_.isAlive() && !p_36945_.isInvulnerable() && p_36945_ != livingentity) {
         if (livingentity == null) {
            p_36945_.hurt(this.damageSources().magic(), 8.0F);
         } else {
            if (livingentity.isAlliedTo(p_36945_)) {
               return;
            }

            p_36945_.hurt(this.damageSources().indirectMagic(this, livingentity), 8.0F);
         }

      }
   }
}
