package com.conkeegs.truehardcore.utils;

import java.util.ArrayList;
import java.util.Random;

import org.slf4j.Logger;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Util functions for truehardcore.
 */
public final class Utils {
    private static final Logger LOGGER = TruestLogger.getLogger();

    /**
     * Replace an entity after a {@code EntityJoinLevelEvent} fires.
     *
     * @param event          {@code EntityJoinLevelEvent} event
     * @param newEntity      the entity to replace {@code oldEntity} with
     * @param oldEntity      the entity to be replaced by {@code newEntity}
     * @param oldEntityLevel the world that {@code oldEntity} spawned in
     */
    public static final void replaceEntity(EntityJoinLevelEvent event, Entity newEntity, Entity oldEntity,
            Level oldEntityLevel) {
        event.setCanceled(true);

        // 7.0?
        oldEntityLevel.addFreshEntity(newEntity);
        oldEntity.discard();
    }

    /**
     * Replace an entity after a {@code MobSpawnEvent} fires.
     *
     * @param event          {@code MobSpawnEvent} event
     * @param newEntity      the entity to replace {@code oldEntity} with
     * @param oldEntity      the entity to be replaced by {@code newEntity}
     * @param oldEntityLevel the world that {@code oldEntity} spawned in
     */
    public static final void replaceEntity(MobSpawnEvent event, Entity newEntity, Entity oldEntity,
            Level oldEntityLevel) {
        event.setCanceled(true);

        // 7.0?
        oldEntityLevel.addFreshEntity(newEntity);
        oldEntity.discard();
    }

    /**
     * Print all entity description ids to console.
     */
    public static final void printAllEntities() {
        ForgeRegistries.ENTITY_TYPES.getValues().stream()
                .filter(entityType -> entityType.canSummon())
                .forEach(entityType -> {
                    LOGGER.info(entityType.getDescriptionId());
                });
    }

    /**
     * Attempts to modify the damage attribute of a living entity.
     *
     * @param entity the living entity to modify
     * @param damage the damage to set
     */
    public static final void modifyAttackDamage(LivingEntity entity, double damage) {
        AttributeInstance damageAttribute = entity.getAttribute(Attributes.ATTACK_DAMAGE);

        if (damageAttribute == null) {
            LOGGER.error("Could not modify damage attribute for entity: '{}'", entity.getType().getDescriptionId());

            return;
        }

        damageAttribute.setBaseValue(damage);
    }

    /**
     * Attempts to modify the speed attribute of a living entity.
     *
     * @param entity the living entity to modify
     * @param speed  the speed to set
     */
    public static final void modifySpeed(LivingEntity entity, float speed) {
        AttributeInstance speedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);

        if (speedAttribute == null) {
            LOGGER.error("Could not modify speed attribute for entity: '{}'", entity.getType().getDescriptionId());

            return;
        }

        speedAttribute.setBaseValue(speed);
    }

    /**
     * Get a random value from an {@code ArrayList}. Mostly useful for getting
     * random mob speeds or damages.
     *
     * @param <T>       the type of object that {@code arrayList} holds
     * @param arrayList the {@code ArrayList} to choose from
     * @return random value from {@code arrayList}
     */
    public static final <T> T getRandomFromArrayList(ArrayList<T> arrayList) {
        return arrayList.get(new Random().nextInt(arrayList.size()));
    }
}
