package com.conkeegs.truehardcore.registries.explosions;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.LargeFireball;

public class ExplosionRegistry {
    private static ExplosionRegistry instance;
    private Map<String, Float> entityMap;

    private ExplosionRegistry() {
        entityMap = new HashMap<>();

        this.addEntity(Creeper.class.getSimpleName(), 10F);
        this.addEntity(LargeFireball.class.getSimpleName(), 10F);
    }

    public static ExplosionRegistry getInstance() {
        if (instance == null) {
            instance = new ExplosionRegistry();
        }

        return instance;
    }

    public Map<String, Float> getAllEntities() {
        return entityMap;
    }

    private void addEntity(String entityName, Float explosionRadius) {
        entityMap.put(entityName, explosionRadius);
    }
}
