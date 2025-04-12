package com.conkeegs.truehardcore.overrides.entities;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import tech.thatgravyboat.creeperoverhaul.common.entity.base.BaseCreeper;
import tech.thatgravyboat.creeperoverhaul.common.entity.base.CreeperType;
import tech.thatgravyboat.creeperoverhaul.common.utils.PlatformUtils;

public class CustomBaseCreeper extends BaseCreeper {
    public CustomBaseCreeper(EntityType<? extends Creeper> entityType, Level level, CreeperType type) {
        super(entityType, level, type);
    }

    public void explode() {
        if (!this.level().isClientSide) {
            Level.ExplosionInteraction interaction = PlatformUtils.getInteractionForCreeper(this);
            this.dead = true;
            Explosion explosion = this.level().explode(this, this.getX(), this.getY(), this.getZ(),
                    8.0F * (this.isPowered() ? 2.0F : 1.0F), interaction);
            this.type.getExplosionSound(this).ifPresent((s) -> {
                this.level().playSound((Player) null, this, s, SoundSource.BLOCKS, 4.0F,
                        (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F);
            });
            this.discard();
            if (!this.type.inflictingPotions().isEmpty()) {
                explosion.getHitPlayers().keySet().forEach((player) -> {
                    Collection<MobEffectInstance> inflictingPotions = this.type.inflictingPotions().stream()
                            .map(MobEffectInstance::new).toList();
                    Objects.requireNonNull(player);
                    inflictingPotions.forEach(player::addEffect);
                });
            }

            if (!this.type.replacer().isEmpty()) {
                Set<Map.Entry<Predicate<BlockState>, Function<RandomSource, BlockState>>> entries = this.type.replacer()
                        .entrySet();
                explosion.getToBlow().stream().map(BlockPos::below).forEach((pos) -> {
                    BlockState state = this.level().getBlockState(pos);
                    Iterator var4 = entries.iterator();

                    while (var4.hasNext()) {
                        Map.Entry<Predicate<BlockState>, Function<RandomSource, BlockState>> entry = (Map.Entry) var4
                                .next();
                        if (((Predicate) entry.getKey()).test(state)) {
                            BlockState newState = (BlockState) ((Function) entry.getValue()).apply(this.random);
                            if (newState != null) {
                                this.level().setBlock(pos, newState, 3);
                                break;
                            }
                        }
                    }

                });
            }

            Stream<MobEffectInstance> potions = Stream.concat(
                    this.getActiveEffects().stream().map(MobEffectInstance::new),
                    this.type.potionsWhenDead().stream().map(MobEffectInstance::new));
            this.summonCloudWithEffects(potions.toList());
        }
    }

    private void summonCloudWithEffects(Collection<MobEffectInstance> effects) {
        if (!effects.isEmpty()) {
            AreaEffectCloud cloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
            cloud.setRadius(2.5F);
            cloud.setRadiusOnUse(-0.5F);
            cloud.setWaitTime(10);
            cloud.setDuration(cloud.getDuration() / 2);
            cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
            Objects.requireNonNull(cloud);
            effects.forEach(cloud::addEffect);
            this.level().addFreshEntity(cloud);
        }

    }
}
