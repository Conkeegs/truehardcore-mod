package com.conkeegs.truehardcore.registries.entities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.conkeegs.truehardcore.utils.TruestLogger;
import com.conkeegs.truehardcore.utils.Utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.AbstractArrow;
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
            Arrays.asList("entity.minecraft.zombie", "entity.minecraft.zombie_villager"));

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
            Entity oldEntity = event.getEntity();
            Level oldEntityLevel = oldEntity.level();

            // Utils.replaceEntity(event, new CustomEvokerFangs(
            // oldEntityLevel,
            // oldEntity.getX(),
            // oldEntity.getY(),
            // oldEntity.getZ(),
            // oldEntity.getYRot(),
            // 0,
            // null),
            // oldEntity,
            // oldEntityLevel);
        });
        this.addEntity("entity.minecraft.small_fireball", (EntityJoinLevelEvent event) -> {
            SmallFireball oldEntity = (SmallFireball) event.getEntity();
            Level oldEntityLevel = oldEntity.level();
            Blaze blaze = (Blaze) (oldEntity.getOwner());

            // if a blaze didn't shoot the fireball, ignore it
            if (blaze == null) {
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

            // Utils.replaceEntity(event, new CustomSmallFireball(
            // oldEntityLevel,
            // oldEntity.getX(),
            // oldEntity.getY(),
            // oldEntity.getZ(),
            // velocityX,
            // velocityY,
            // velocityZ),
            // oldEntity,
            // oldEntityLevel);
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

                Utils.modifyAttackDamage(zombie, 9.0D);

                // don't modify speed of baby zombies as they will become ungodly fast
                if (zombie.isBaby()) {
                    return;
                }

                Utils.modifySpeed(zombie, Utils.getRandomFromArrayList(zombieSpeeds));
            });
        }
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
