package com.conkeegs.truehardcore.registries.explosions;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.conkeegs.truehardcore.utils.TruestLogger;

import net.minecraft.world.entity.projectile.LargeFireball;

public class ExplosionRegistry {
    private static ExplosionRegistry instance;
    private Map<String, Double> entityMap;

    private static final Logger LOGGER = TruestLogger.getLogger();

    private ExplosionRegistry() {
        entityMap = new HashMap<>();

        this.addEntity(LargeFireball.class.getSimpleName(), 10D);
    }

    public static ExplosionRegistry getInstance() {
        if (instance == null) {
            instance = new ExplosionRegistry();
        }

        return instance;
    }

    public Map<String, Double> getAllEntities() {
        return entityMap;
    }

    private void addEntity(String entityName, Double explosionRadius) {
        entityMap.put(entityName, explosionRadius);
    }
}
