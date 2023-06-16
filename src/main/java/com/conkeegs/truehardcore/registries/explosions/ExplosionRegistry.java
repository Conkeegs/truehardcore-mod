package com.conkeegs.truehardcore.registries.explosions;

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

public class ExplosionRegistry {
    private static ExplosionRegistry instance;
    private Map<String, Consumer<Map<Entity, Double>>> itemMap;

    private static final Logger LOGGER = TruestLogger.getLogger();

    private ExplosionRegistry() {
        itemMap = new HashMap<>();

        this.addEntity(Arrow.class.getSimpleName(), (Map<Entity, Double> test) -> {
            Entity ent = test.keySet().iterator().next();
            // 4.5

            ((Arrow) ent).setBaseDamage(test.get(ent));
        });

        this.addEntity(EvokerFangs.class.getSimpleName(), (Map<Entity, Double> test) -> {
            Entity ent = test.keySet().iterator().next();
            Level entityLevel = ent.level;
            // 7.0?

            CustomEvokerFangs customEvokerFangs = new CustomEvokerFangs(
                    entityLevel,
                    ent.getX(),
                    ent.getY(),
                    ent.getZ(),
                    ent.getYRot(),
                    0,
                    null);

            entityLevel.addFreshEntity(customEvokerFangs);
            ent.discard();
        });
    }

    public static ExplosionRegistry getInstance() {
        if (instance == null) {
            instance = new ExplosionRegistry();
        }

        return instance;
    }

    public Map<String, Consumer<Map<Entity, Double>>> getAllEntities() {
        return itemMap;
    }

    private void addEntity(String entityName, Consumer<Map<Entity, Double>> action) {
        itemMap.put(entityName, action);
    }
}
