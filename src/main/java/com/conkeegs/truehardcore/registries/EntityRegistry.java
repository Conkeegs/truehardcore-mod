package com.conkeegs.truehardcore.registries;

import java.util.HashMap;
import java.util.Map;

public class EntityRegistry {
    private static EntityRegistry instance;
    private Map<String, Float> itemMap;

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

    public Map<String, Float> getAllEntities() {
        return itemMap;
    }

    private void addEntity(String entityName, Float damage) {
        itemMap.put(entityName, damage);
    }
}
