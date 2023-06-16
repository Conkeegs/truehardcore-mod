package com.conkeegs.truehardcore.registries.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.conkeegs.truehardcore.overrides.entities.CustomEvokerFangs;
import com.conkeegs.truehardcore.overrides.entities.CustomSmallFireball;
import com.conkeegs.truehardcore.utils.TruestLogger;

import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

public class EntityRegistry {
    private static EntityRegistry instance;
    private Map<String, Consumer<EntityJoinLevelEvent>> entityMap;

    private static final Logger LOGGER = TruestLogger.getLogger();

    private EntityRegistry() {
        entityMap = new HashMap<>();

        this.addEntity(Arrow.class.getSimpleName(), (EntityJoinLevelEvent event) -> {
            ((Arrow) event.getEntity()).setBaseDamage(4.5D);
        });

        this.addEntity(ThrownTrident.class.getSimpleName(), (EntityJoinLevelEvent event) -> {
            ((ThrownTrident) event.getEntity()).setBaseDamage(11.0D);
        });

        this.addEntity(EvokerFangs.class.getSimpleName(), (EntityJoinLevelEvent event) -> {
            Entity oldEntity = event.getEntity();
            Level oldEntityLevel = oldEntity.level;

            replaceEntity(event, new CustomEvokerFangs(
                    oldEntityLevel,
                    oldEntity.getX(),
                    oldEntity.getY(),
                    oldEntity.getZ(),
                    oldEntity.getYRot(),
                    0,
                    null),
                    oldEntity,
                    oldEntityLevel);
        });

        this.addEntity(SmallFireball.class.getSimpleName(), (EntityJoinLevelEvent event) -> {
            Entity oldEntity = event.getEntity();
            Level oldEntityLevel = oldEntity.level;
            Vec3i motionVector = oldEntity.getMotionDirection().getNormal();

            replaceEntity(event, new CustomSmallFireball(
                    oldEntityLevel,
                    oldEntity.getX(),
                    oldEntity.getY(),
                    oldEntity.getZ(),
                    Double.parseDouble(Integer.toString(motionVector.getX())),
                    Double.parseDouble(Integer.toString(motionVector.getY())),
                    Double.parseDouble(Integer.toString(motionVector.getZ()))),
                    oldEntity,
                    oldEntityLevel);
        });
    }

    public static void replaceEntity(EntityJoinLevelEvent event, Entity newEntity, Entity oldEntity,
            Level oldEntityLevel) {
        event.setCanceled(true);

        // 7.0?
        oldEntityLevel.addFreshEntity(newEntity);
        oldEntity.discard();
    }

    public static EntityRegistry getInstance() {
        if (instance == null) {
            instance = new EntityRegistry();
        }

        return instance;
    }

    public Map<String, Consumer<EntityJoinLevelEvent>> getAllEntities() {
        return entityMap;
    }

    private void addEntity(String entityName, Consumer<EntityJoinLevelEvent> action) {
        entityMap.put(entityName, action);
    }
}
