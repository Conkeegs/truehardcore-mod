package com.conkeegs.truehardcore.registries.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.conkeegs.truehardcore.overrides.entities.CustomEvokerFangs;
import com.conkeegs.truehardcore.utils.TruestLogger;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

public class EntityRegistry {
    private static EntityRegistry instance;
    private Map<String, Consumer<EntityJoinLevelEvent>> entityMap;

    private static final Logger LOGGER = TruestLogger.getLogger();

    private EntityRegistry() {
        entityMap = new HashMap<>();

        this.addEntity(Arrow.class.getSimpleName(), (EntityJoinLevelEvent event) -> {
            // 4.5
            ((Arrow) event.getEntity()).setBaseDamage(4.5D);
        });

        this.addEntity(EvokerFangs.class.getSimpleName(), (EntityJoinLevelEvent event) -> {
            event.setCanceled(true);

            Entity entity = event.getEntity();
            Level entityLevel = entity.level;
            // 7.0?

            CustomEvokerFangs customEvokerFangs = new CustomEvokerFangs(
                    entityLevel,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    entity.getYRot(),
                    0,
                    null);

            entityLevel.addFreshEntity(customEvokerFangs);
            entity.discard();
        });
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
