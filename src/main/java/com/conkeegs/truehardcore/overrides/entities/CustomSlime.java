package com.conkeegs.truehardcore.overrides.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CustomSlime extends Slime {
    public CustomSlime(EntityType<? extends Slime> p_33588_, Level p_33589_) {
        super(p_33588_, p_33589_);
    }

    @Override
    protected int getJumpDelay() {
        return 5;
    }

    @Override
    protected void jumpFromGround() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, 1, vec3.z);
        this.hasImpulse = true;
    }
}
