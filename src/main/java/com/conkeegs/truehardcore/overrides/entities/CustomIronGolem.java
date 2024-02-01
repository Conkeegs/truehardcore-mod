package com.conkeegs.truehardcore.overrides.entities;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;

public class CustomIronGolem extends IronGolem {
    private int attackAnimationTick;
    public float maxUpStep;

    public CustomIronGolem(EntityType<? extends IronGolem> p_28834_, Level p_28835_, double p_36927_, double p_36928_,
            double p_36929_, float p_36930_) {
        super(p_28834_, p_28835_);
        this.maxUpStep = 1.0F;

        this.setYRot(p_36930_ * (180F / (float) Math.PI));
        this.setPos(p_36927_, p_36928_, p_36929_);
    }

    public boolean doHurtTarget(Entity p_28837_) {
        this.attackAnimationTick = 10;
        this.level.broadcastEntityEvent(this, (byte) 4);
        float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = (int) f > 0 ? f / 2.0F + (float) this.random.nextInt((int) f) : f;
        boolean flag = p_28837_.hurt(DamageSource.mobAttack(this), f1);
        if (flag) {
            p_28837_.setDeltaMovement(p_28837_.getDeltaMovement().add(0.0D, 2, 0.0D));
            this.doEnchantDamageEffects(this, p_28837_);
        }

        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }
}
