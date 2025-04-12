package com.conkeegs.truehardcore.overrides.objects;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.conkeegs.truehardcore.utils.TruestLogger;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CustomExplosion extends Explosion {
    private final Level level;
    private final double x;
    private final double y;
    private final double z;
    @Nullable
    private final Entity source;
    private final float radius;
    private final ExplosionDamageCalculator damageCalculator;
    private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new ExplosionDamageCalculator();
    private final ObjectArrayList<BlockPos> toBlow = new ObjectArrayList<>();
    private final Map<Player, Vec3> hitPlayers = Maps.newHashMap();
    private final Explosion.BlockInteraction blockInteraction;
    // properties that we need to override because they aren't visible
    private final boolean fire;
    private final DamageSource damageSource;
    private final Vec3 position;
    private final RandomSource random = RandomSource.create();
    private static final Logger LOGGER = TruestLogger.getLogger();

    public CustomExplosion(Level p_46051_, @Nullable Entity p_46052_, @Nullable DamageSource p_46053_,
            @Nullable ExplosionDamageCalculator p_46054_, double p_46055_, double p_46056_, double p_46057_,
            float p_46058_, boolean p_46059_, Explosion.BlockInteraction p_46060_) {
        super(
                p_46051_,
                p_46052_,
                p_46053_,
                p_46054_,
                p_46055_,
                p_46056_,
                p_46057_,
                p_46058_,
                p_46059_,
                p_46060_);

        this.level = p_46051_;
        this.source = p_46052_;
        this.radius = p_46058_;
        this.x = p_46055_;
        this.y = p_46056_;
        this.z = p_46057_;
        this.fire = p_46059_;
        this.blockInteraction = p_46060_;
        this.damageSource = p_46053_ == null ? p_46051_.damageSources().explosion(this) : p_46053_;
        this.damageCalculator = p_46054_ == null ? this.makeDamageCalculator(p_46052_) : p_46054_;
        this.position = new Vec3(this.x, this.y, this.z);
    }

    private ExplosionDamageCalculator makeDamageCalculator(@Nullable Entity p_46063_) {
        return (ExplosionDamageCalculator) (p_46063_ == null ? EXPLOSION_DAMAGE_CALCULATOR
                : new EntityBasedExplosionDamageCalculator(p_46063_));
    }

    @Override
    public void explode() {
        this.level.gameEvent(this.source, GameEvent.EXPLODE, new Vec3(this.x, this.y, this.z));
        Set<BlockPos> set = Sets.newHashSet();
        int i = 16;

        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (double) ((float) j / 15.0F * 2.0F - 1.0F);
                        double d1 = (double) ((float) k / 15.0F * 2.0F - 1.0F);
                        double d2 = (double) ((float) l / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f = this.radius * (0.7F + this.level.random.nextFloat() * 0.6F);
                        double d4 = this.x;
                        double d6 = this.y;
                        double d8 = this.z;

                        for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = BlockPos.containing(d4, d6, d8);
                            BlockState blockstate = this.level.getBlockState(blockpos);
                            FluidState fluidstate = this.level.getFluidState(blockpos);
                            if (!this.level.isInWorldBounds(blockpos)) {
                                break;
                            }

                            Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance(this,
                                    this.level, blockpos, blockstate, fluidstate);
                            if (optional.isPresent()) {
                                f -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F && this.damageCalculator.shouldBlockExplode(this, this.level, blockpos,
                                    blockstate, f)) {
                                set.add(blockpos);
                            }

                            d4 += d0 * (double) 0.3F;
                            d6 += d1 * (double) 0.3F;
                            d8 += d2 * (double) 0.3F;
                        }
                    }
                }
            }
        }

        this.toBlow.addAll(set);
        float f2 = this.radius * 2.0F;
        int k1 = Mth.floor(this.x - (double) f2 - 1.0D);
        int l1 = Mth.floor(this.x + (double) f2 + 1.0D);
        int i2 = Mth.floor(this.y - (double) f2 - 1.0D);
        int i1 = Mth.floor(this.y + (double) f2 + 1.0D);
        int j2 = Mth.floor(this.z - (double) f2 - 1.0D);
        int j1 = Mth.floor(this.z + (double) f2 + 1.0D);
        List<Entity> list = this.level.getEntities(this.source,
                new AABB((double) k1, (double) i2, (double) j2, (double) l1, (double) i1, (double) j1));
        net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.level, this, list, f2);
        Vec3 vec3 = new Vec3(this.x, this.y, this.z);

        for (int k2 = 0; k2 < list.size(); ++k2) {
            Entity entity = list.get(k2);
            if (!entity.ignoreExplosion()) {
                double d12 = Math.sqrt(entity.distanceToSqr(vec3)) / (double) f2;
                if (d12 <= 1.0D) {
                    double d5 = entity.getX() - this.x;
                    double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
                    double d9 = entity.getZ() - this.z;
                    double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
                    if (d13 != 0.0D) {
                        d5 /= d13;
                        d7 /= d13;
                        d9 /= d13;
                        double d14 = (double) getSeenPercent(vec3, entity);
                        double d10 = (1.0D - d12) * d14;

                        double rawDistance = d13;
                        double seenPercent = d14; // exposure
                        double baseDamage = 19.0;
                        double dropPerBlock = 2.5;

                        double effectiveDistance = Math.max(1.0, rawDistance); // don't go below 1 block
                        double damage = Math.max(0.0, baseDamage - (effectiveDistance - 1.0) * dropPerBlock);

                        // factor in exposure like vanilla
                        damage *= seenPercent;

                        LOGGER.info("DAMAGE GRUM: {}", (float) damage);
                        LOGGER.info("DISTANCE GRUM: {}", effectiveDistance);

                        entity.hurt(this.getDamageSource(), (float) damage);
                        double d11;
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingentity = (LivingEntity) entity;
                            d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingentity, d10);
                        } else {
                            d11 = d10;
                        }

                        d5 *= d11;
                        d7 *= d11;
                        d9 *= d11;
                        Vec3 vec31 = new Vec3(d5, d7, d9);
                        entity.setDeltaMovement(entity.getDeltaMovement().add(vec31));
                        if (entity instanceof Player) {
                            Player player = (Player) entity;
                            if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                                this.hitPlayers.put(player, vec31);
                            }
                        }
                    }
                }
            }
        }
    }

    public void finalizeExplosion(boolean p_46076_) {
        if (this.level.isClientSide) {
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F,
                    (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
        }

        if (p_46076_) {
            this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
        }

        ObjectArrayList<Pair<ItemStack, BlockPos>> objectarraylist = new ObjectArrayList<>();
        boolean flag1 = this.getIndirectSourceEntity() instanceof Player;
        Util.shuffle(this.toBlow, this.level.random);

        for (BlockPos blockpos : this.toBlow) {
            BlockState blockstate = this.level.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (!blockstate.isAir()) {
                BlockPos blockpos1 = blockpos.immutable();
                this.level.getProfiler().push("explosion_blocks");
                if (blockstate.canDropFromExplosion(this.level, blockpos, this)) {
                    Level $$9 = this.level;
                    if ($$9 instanceof ServerLevel) {
                        ServerLevel serverlevel = (ServerLevel) $$9;
                        BlockEntity blockentity = blockstate.hasBlockEntity() ? this.level.getBlockEntity(blockpos)
                                : null;
                        LootParams.Builder lootparams$builder = (new LootParams.Builder(serverlevel))
                                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos))
                                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity)
                                .withOptionalParameter(LootContextParams.THIS_ENTITY, this.source);
                        if (this.blockInteraction == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
                            lootparams$builder.withParameter(LootContextParams.EXPLOSION_RADIUS, this.radius);
                        }

                        blockstate.spawnAfterBreak(serverlevel, blockpos, ItemStack.EMPTY, flag1);
                        blockstate.getDrops(lootparams$builder).forEach((p_46074_) -> {
                            addBlockDrops(objectarraylist, p_46074_, blockpos1);
                        });
                    }
                }

                blockstate.onBlockExploded(this.level, blockpos, this);
                this.level.getProfiler().pop();
            }
        }

        for (Pair<ItemStack, BlockPos> pair : objectarraylist) {
            Block.popResource(this.level, pair.getSecond(), pair.getFirst());
        }

        for (BlockPos blockpos2 : this.toBlow) {
            if (this.random.nextInt(3) == 0 && this.level.getBlockState(blockpos2).isAir()
                    && this.level.getBlockState(blockpos2.below()).isSolidRender(this.level, blockpos2.below())) {
                this.level.setBlockAndUpdate(blockpos2, BaseFireBlock.getState(this.level, blockpos2));
            }
        }
    }

    private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> p_46068_, ItemStack p_46069_,
            BlockPos p_46070_) {
        int i = p_46068_.size();

        for (int j = 0; j < i; ++j) {
            Pair<ItemStack, BlockPos> pair = p_46068_.get(j);
            ItemStack itemstack = pair.getFirst();
            if (ItemEntity.areMergable(itemstack, p_46069_)) {
                ItemStack itemstack1 = ItemEntity.merge(itemstack, p_46069_, 16);
                p_46068_.set(j, Pair.of(itemstack1, pair.getSecond()));
                if (p_46069_.isEmpty()) {
                    return;
                }
            }
        }

        p_46068_.add(Pair.of(p_46069_, p_46070_));
    }

    @Override
    public List<BlockPos> getToBlow() {
        return this.toBlow;
    }

    public void handleExplosion() {
        Entity source = this.source;

        if (source == null) {
            LOGGER.error("Explosion source is null, cannot finalize custom explosion");

            return;
        }

        this.explode();
        this.finalizeExplosion(true);
        source.discard();
    }
}
