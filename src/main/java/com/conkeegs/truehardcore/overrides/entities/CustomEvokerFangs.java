package com.conkeegs.truehardcore.overrides.entities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;

/**
 * Custom evoker fangs to increase damage.
 */
public class CustomEvokerFangs extends EvokerFangs {
   /**
    * Custom evoker fangs constructor.
    *
    * @param fangLevel        the level the fangs are in
    * @param fangX            fang x position
    * @param fangY            fang y position
    * @param fangZ            fang z position
    * @param fangYRot         fang y rotation
    * @param warmupDelayTicks fang warmupDelayTicks field
    * @param owner            fang owner (evoker)
    */
   public CustomEvokerFangs(Level fangLevel, double fangX, double fangY, double fangZ, float fangYRot,
         int warmupDelayTicks, LivingEntity owner) {
      super(fangLevel, fangX, fangY, fangZ, fangYRot, warmupDelayTicks, owner);
   }

   /**
    * Handle dealing damage to entities from fangs.
    *
    * @param target entity to hurt
    */
   private void dealDamageTo(LivingEntity target) {
      LivingEntity livingentity = this.getOwner();
      if (target.isAlive() && !target.isInvulnerable() && target != livingentity) {
         if (livingentity == null) {
            target.hurt(this.damageSources().magic(), 8.0F);
         } else {
            if (livingentity.isAlliedTo(target)) {
               return;
            }

            target.hurt(this.damageSources().indirectMagic(this, livingentity), 8.0F);
         }

      }
   }
}
