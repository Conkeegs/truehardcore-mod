package com.conkeegs.truehardcore.registries.entities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.conkeegs.truehardcore.overrides.entities.CustomEvokerFangs;
import com.conkeegs.truehardcore.overrides.entities.CustomSmallFireball;
import com.conkeegs.truehardcore.utils.TruestLogger;
import com.conkeegs.truehardcore.utils.Utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

/**
 * Registry of entities that will have their properties modified upon spawning.
 */
public class EntityRegistry {
    /**
     * Singleton {@code EntityRegistry} instance.
     */
    private static EntityRegistry instance;
    /**
     * Map of entities, keyed by their description ids, so that spawn listeners can
     * tell which entities we want to modify.
     */
    private Map<String, Consumer<EntityJoinLevelEvent>> entityMap;
    /**
     * List of all the speeds a zombie can spawn with.
     */
    private static final ArrayList<Float> zombieSpeeds = new ArrayList<Float>(
            Arrays.asList(0.357F, 0.3F, 0.33F, 0.35F, 0.34F, 0.38F, 0.4F, 0.39F, 0.36F, 0.352F, 0.37F, 0.32F, 0.36F,
                    0.355F, 0.375F));
    /**
     * List of all the speeds a creeper can spawn with.
     */
    private static final ArrayList<Float> creeperSpeeds = new ArrayList<Float>(
            Arrays.asList(0.3F, 0.28F, 0.35F, 0.32F));
    private static final Logger LOGGER = TruestLogger.getLogger();
    /**
     * List of all the types of arrows we want to modify.
     */
    private static final ArrayList<String> arrowDescriptionIds = new ArrayList<String>(Arrays.asList(
            "entity.minecraft.arrow",
            "entity.minecraft.spectral_arrow",
            "entity.twilightforest.ice_arrow",
            "entity.twilightforest.seeker_arrow",
            "entity.quark.torch_arrow"));
    /**
     * List of all the types of zombies we want to modify.
     */
    private static final ArrayList<String> zombieDescriptionIds = new ArrayList<String>(
            Arrays.asList("entity.minecraft.zombie", "entity.minecraft.zombie_villager", "entity.minecraft.drowned",
                    "entity.minecraft.husk", "entity.minecraft.zombified_piglin"));
    /**
     * List of all the types of spiders we want to modify.
     */
    private static final ArrayList<String> spiderDescriptionIds = new ArrayList<String>(
            Arrays.asList("entity.minecraft.cave_spider", "entity.minecraft.spider"));
    /**
     * List of all the types of skeletons we want to modify.
     */
    private static final ArrayList<String> skeletonDescriptionIds = new ArrayList<String>(
            Arrays.asList("entity.minecraft.skeleton", "entity.minecraft.wither_skeleton", "entity.minecraft.stray"));

    /**
     * Private {@code EntityRegistry} singleton constructor.
     */
    private EntityRegistry() {
        entityMap = new HashMap<>();

        for (String arrowDescriptionId : arrowDescriptionIds) {
            this.addEntity(arrowDescriptionId, (EntityJoinLevelEvent event) -> {
                ((AbstractArrow) event.getEntity()).setBaseDamage(4.5D);
            });
        }

        this.addEntity("entity.minecraft.trident", (EntityJoinLevelEvent event) -> {
            ((ThrownTrident) event.getEntity()).setBaseDamage(11.0D);
        });
        this.addEntity("entity.minecraft.evoker_fangs", (EntityJoinLevelEvent event) -> {
            EvokerFangs oldEntity = (EvokerFangs) event.getEntity();
            Level oldEntityLevel = oldEntity.level();

            Utils.replaceEntity(event, new CustomEvokerFangs(
                    oldEntityLevel,
                    oldEntity.getX(),
                    oldEntity.getY(),
                    oldEntity.getZ(),
                    0.0F,
                    0,
                    oldEntity.getOwner()),
                    oldEntity,
                    oldEntityLevel);
        });
        this.addEntity("entity.minecraft.evoker", (EntityJoinLevelEvent event) -> {
            Utils.modifySpeed((Evoker) event.getEntity(), 0.55F);
        });
        this.addEntity("entity.minecraft.small_fireball", (EntityJoinLevelEvent event) -> {
            SmallFireball oldEntity = (SmallFireball) event.getEntity();

            // if a blaze didn't shoot the fireball, ignore it
            if (!((oldEntity.getOwner()) instanceof Blaze blaze)) {
                return;
            }

            LivingEntity blazeTarget = blaze.getTarget();

            if (blazeTarget == null) {
                LOGGER.error("Blaze target is null");

                return;
            }

            Vec3 target = blazeTarget.getEyePosition();
            double deltaX = target.x - oldEntity.getX();
            double deltaY = target.y - oldEntity.getY();
            double deltaZ = target.z - oldEntity.getZ();
            // Calculate the distance between blaze and player
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            // Normalize the direction vector to maintain constant speed
            double velocityX = deltaX / distance;
            double velocityY = deltaY / distance;
            double velocityZ = deltaZ / distance;
            Level oldEntityLevel = oldEntity.level();

            Utils.replaceEntity(event, new CustomSmallFireball(
                    oldEntityLevel,
                    oldEntity.getX(),
                    oldEntity.getY(),
                    oldEntity.getZ(),
                    velocityX,
                    velocityY,
                    velocityZ),
                    oldEntity,
                    oldEntityLevel);
        });
        this.addEntity("entity.minecraft.shulker_bullet", (EntityJoinLevelEvent event) -> {
            ShulkerBullet oldEntity = (ShulkerBullet) event.getEntity();
            Level oldEntityLevel = oldEntity.level();

            try {
                Field field = ShulkerBullet.class.getDeclaredField("f_37312_");

                field.setAccessible(true);

                Entity target = (Entity) field.get(oldEntity);

                // Utils.replaceEntity(event, new CustomShulkerBullet(
                // oldEntityLevel,
                // (Shulker) oldEntity.getOwner(),
                // target,
                // oldEntity.getMotionDirection().getAxis()),
                // oldEntity,
                // oldEntityLevel);
            } catch (Exception e) {
                LOGGER.error("Error replacing Shulker bullet - {}", e);
            }
        });
        this.addEntity("entity.minecraft.iron_golem", (EntityJoinLevelEvent event) -> {
            IronGolem oldEntity = (IronGolem) event.getEntity();
            Level oldEntityLevel = oldEntity.level();

            // Utils.replaceEntity(event, new CustomIronGolem(
            // (EntityType<? extends IronGolem>) oldEntity.getType(),
            // oldEntityLevel,
            // oldEntity.getX(),
            // oldEntity.getY(),
            // oldEntity.getZ(),
            // oldEntity.getYRot()),
            // oldEntity,
            // oldEntityLevel);
        });

        for (String zombieDescriptionId : zombieDescriptionIds) {
            this.addEntity(zombieDescriptionId, (EntityJoinLevelEvent event) -> {
                Zombie zombie = (Zombie) event.getEntity();

                Utils.modifyAttackDamage(zombie, (!(zombie instanceof ZombifiedPiglin)) ? 9.0D : 7.0D);

                // don't modify speed of baby zombies as they will become ungodly fast
                if (zombie.isBaby()) {
                    return;
                }

                Utils.modifySpeed(zombie, Utils.getRandomFromArrayList(zombieSpeeds));
            });
        }

        this.addEntity("entity.minecraft.blaze", (EntityJoinLevelEvent event) -> {
            Utils.modifyAttackDamage((Blaze) event.getEntity(), 11.0D);
        });

        for (String spiderDescString : spiderDescriptionIds) {
            this.addEntity(spiderDescString, (EntityJoinLevelEvent event) -> {
                Spider spider = (Spider) event.getEntity();

                Utils.modifyAttackDamage(spider, 8.0D);

                if (spider instanceof CaveSpider) {
                    return;
                }

                Utils.modifySpeed(spider, 0.33F);
            });
        }

        this.addEntity("entity.minecraft.creeper", (EntityJoinLevelEvent event) -> {
            Utils.modifySpeed((Creeper) event.getEntity(), Utils.getRandomFromArrayList(creeperSpeeds));
        });
        this.addEntity("entity.minecraft.elder_guardian", (EntityJoinLevelEvent event) -> {
            Utils.modifyAttackDamage((ElderGuardian) event.getEntity(), 12.0D);
        });
        this.addEntity("entity.minecraft.enderman", (EntityJoinLevelEvent event) -> {
            Utils.modifyAttackDamage((EnderMan) event.getEntity(), 9.0D);
        });
        this.addEntity("entity.minecraft.endermite", (EntityJoinLevelEvent event) -> {
            Utils.modifyAttackDamage((Endermite) event.getEntity(), 7.0D);
        });
        this.addEntity("entity.minecraft.guardian", (EntityJoinLevelEvent event) -> {
            Guardian guardian = (Guardian) event.getEntity();

            Utils.modifyAttackDamage(guardian, 10.0D);
            Utils.modifySpeed(guardian, 0.55F);
        });
        this.addEntity("entity.minecraft.hoglin", (EntityJoinLevelEvent event) -> {
            Hoglin hoglin = (Hoglin) event.getEntity();

            Utils.modifyAttackDamage(hoglin, 10.0D);
            Utils.modifySpeed(hoglin, 0.33F);
        });
        this.addEntity("entity.minecraft.panda", (EntityJoinLevelEvent event) -> {
            Panda panda = (Panda) event.getEntity();

            Utils.modifyAttackDamage(panda, 12.0D);
            Utils.modifySpeed(panda, 0.23F);
        });
        this.addEntity("entity.minecraft.piglin", (EntityJoinLevelEvent event) -> {
            Piglin piglin = (Piglin) event.getEntity();

            Utils.modifyAttackDamage(piglin, 7.0D);

            // don't modify speed of baby as they will become ungodly fast
            if (piglin.isBaby()) {
                return;
            }

            Utils.modifySpeed(piglin, Utils.getRandomFromArrayList(zombieSpeeds));
        });
        this.addEntity("entity.minecraft.piglin_brute", (EntityJoinLevelEvent event) -> {
            PiglinBrute piglinBrute = (PiglinBrute) event.getEntity();

            Utils.modifyAttackDamage(piglinBrute, 5.0D);

            // don't modify speed of baby as they will become ungodly fast
            if (piglinBrute.isBaby()) {
                return;
            }

            Utils.modifySpeed(piglinBrute, Utils.getRandomFromArrayList(zombieSpeeds));
        });
        this.addEntity("entity.minecraft.pillager", (EntityJoinLevelEvent event) -> {
            Utils.modifyAttackDamage((Pillager) event.getEntity(), 8.0D);
        });
        this.addEntity("entity.minecraft.polar_bear", (EntityJoinLevelEvent event) -> {
            PolarBear polarBear = (PolarBear) event.getEntity();

            Utils.modifyAttackDamage(polarBear, 10.0D);
            Utils.modifySpeed(polarBear, 0.3F);
        });
        this.addEntity("entity.minecraft.ravager", (EntityJoinLevelEvent event) -> {
            Ravager ravager = (Ravager) event.getEntity();

            Utils.modifyAttackDamage(ravager, 14.0D);
            Utils.modifySpeed(ravager, 0.35F);
        });
        this.addEntity("entity.minecraft.silverfish", (EntityJoinLevelEvent event) -> {
            Silverfish silverfish = (Silverfish) event.getEntity();

            Utils.modifyAttackDamage(silverfish, 4.0D);
            Utils.modifySpeed(silverfish, 0.3F);
        });
        this.addEntity("entity.minecraft.slime", (EntityJoinLevelEvent event) -> {
            Slime slime = (Slime) event.getEntity();

            Utils.modifyAttackDamage(slime, 8.0D);
            Utils.modifySpeed(slime, 0.6F);
        });

        for (String skeletonDescriptionId : skeletonDescriptionIds) {
            this.addEntity(skeletonDescriptionId, (EntityJoinLevelEvent event) -> {
                AbstractSkeleton skeleton = (AbstractSkeleton) event.getEntity();

                Utils.modifySpeed(skeleton, 0.3F);

                if (!(skeleton instanceof WitherSkeleton)) {
                    return;
                }

                Utils.modifyAttackDamage(skeleton, 7.3D);
            });
        }

        this.addEntity("entity.minecraft.vex", (EntityJoinLevelEvent event) -> {
            Utils.modifyAttackDamage((Vex) event.getEntity(), 6.5D);
        });
        this.addEntity("entity.minecraft.witch", (EntityJoinLevelEvent event) -> {
            Utils.modifySpeed((Witch) event.getEntity(), 0.32F);
        });
        this.addEntity("entity.minecraft.vindicator", (EntityJoinLevelEvent event) -> {
            Vindicator vindicator = (Vindicator) event.getEntity();

            // Utils.modifyAttackDamage(vindicator, 6.0D);
            Utils.modifySpeed(vindicator, 0.37F);
        });
        this.addEntity("entity.minecraft.wolf", (EntityJoinLevelEvent event) -> {
            Wolf wolf = (Wolf) event.getEntity();

            Utils.modifyAttackDamage(wolf, 10.0D);
            Utils.modifySpeed(wolf, 0.35F);
        });
        this.addEntity("entity.minecraft.zoglin", (EntityJoinLevelEvent event) -> {
            Zoglin zoglin = (Zoglin) event.getEntity();

            Utils.modifyAttackDamage(zoglin, 11.0D);
            Utils.modifySpeed(zoglin, 0.35F);
        });
    }

    /**
     * Get the singleton {@code EntityRegistry} instance.
     *
     * @return singleton {@code EntityRegistry} instance
     */
    public static EntityRegistry getInstance() {
        if (instance == null) {
            instance = new EntityRegistry();
        }

        return instance;
    }

    /**
     * Get the map of all entities to modify, keyed by description id.
     *
     * @return map of all entities to modify
     */
    public Map<String, Consumer<EntityJoinLevelEvent>> getAllEntities() {
        return entityMap;
    }

    /**
     * Add an entity to the entity map to mark it as an entity we want to modify.
     *
     * @param entityDescriptionId the unique description id to identify the entity
     * @param action              callback to execute after the entity spawns to
     *                            modify it
     */
    private void addEntity(String entityDescriptionId, Consumer<EntityJoinLevelEvent> action) {
        entityMap.put(entityDescriptionId, action);
    }
}
