package com.conkeegs.truehardcore.registries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Arrow;

public class EntityRegistry {
    private static EntityRegistry instance;
    private Map<String, Consumer<Entity>> itemMap;

    private EntityRegistry() {
        itemMap = new HashMap<>();

        this.addEntity(Arrow.class.getSimpleName(), (Entity arrow) -> {
            ((Arrow) arrow).setBaseDamage(50.0D);
        });
    }

    public static EntityRegistry getInstance() {
        if (instance == null) {
            instance = new EntityRegistry();
        }

        return instance;
    }

    public Map<String, Consumer<Entity>> getAllEntities() {
        return itemMap;
    }

    private void addEntity(String entityName, Consumer<Entity> action) {
        itemMap.put(entityName, action);
    }
}
