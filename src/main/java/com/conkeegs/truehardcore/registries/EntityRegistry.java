package com.conkeegs.truehardcore.registries;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.entity.Entity;

public class EntityRegistry {
    private static EntityRegistry instance;
    private Map<String, Class<? extends Entity>> itemMap;

    private EntityRegistry() {
        itemMap = new HashMap<>();

        // this.addEntity(Zombie.class.getSimpleName(), Zombie.class);
    }

    public static EntityRegistry getInstance() {
        if (instance == null) {
            instance = new EntityRegistry();
        }

        return instance;
    }

    public Map<String, Class<? extends Entity>> getAllEntities() {
        return itemMap;
    }

    private void addEntity(String entityName, Class<? extends Entity> clazz) {
        itemMap.put(entityName, clazz);
    }
}
