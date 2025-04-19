package com.conkeegs.truehardcore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.phys.Vec3;

@Mixin(IronGolem.class)
public class IronGolemMixin {
    /**
     * Redirect the vertical knockback applied when the Iron Golem attacks.
     */
    @Redirect(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void modifyKnockback(Entity target, Vec3 motion) {
        IronGolem self = (IronGolem) (Object) this;

        target.setDeltaMovement(self.getLookAngle().scale(20.0D).add(0.0D, 1.5D, 0.0D));
    }
}
