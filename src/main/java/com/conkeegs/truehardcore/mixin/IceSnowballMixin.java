package com.conkeegs.truehardcore.mixin;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.conkeegs.truehardcore.utils.TruestLogger;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import twilightforest.entity.projectile.IceSnowball;

@Mixin(IceSnowball.class)
public class IceSnowballMixin {
    private static final Logger LOGGER = TruestLogger.getLogger();
    private static final float DAMAGE = 5.0F;
    private static final String TARGET_METHOD = "onHitEntity";
    private static final String TARGET_CALL = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z";

    // @Redirect(method = TARGET_METHOD, at = @At(value = "INVOKE", target =
    // TARGET_CALL, ordinal = 0), slice = @Slice(to = @At(value = "INVOKE", target =
    // "Lnet/minecraft/world/entity/LivingEntity;isAlliedTo(Lnet/minecraft/world/entity/Entity;)Z",
    // shift = At.Shift.BEFORE)))
    @Redirect(method = TARGET_METHOD, at = @At(value = "INVOKE", target = TARGET_CALL))
    private boolean doMoreDamageNoOwner(Entity target, DamageSource damageSource, float damage) {
        return target.hurt(damageSource, DAMAGE);
    }
}
