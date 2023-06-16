package com.conkeegs.truehardcore.registries.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.conkeegs.truehardcore.overrides.entities.CustomEvokerFangs;
import com.conkeegs.truehardcore.overrides.entities.CustomLargeFireball;
import com.conkeegs.truehardcore.utils.TruestLogger;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;

public class EntityRegistry {
    private static EntityRegistry instance;
    private Map<String, Consumer<Map<Entity, Double>>> itemMap;

    private static final Logger LOGGER = TruestLogger.getLogger();

    private EntityRegistry() {
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

        this.addEntity(LargeFireball.class.getSimpleName(), (Map<Entity, Double> test) -> {
            Entity ent = test.keySet().iterator().next();
            Ghast ghast = ((Ghast) ((LargeFireball) ent).getOwner());

            if (ghast != null && ghast.getTarget() != null) {
                Player ghastTarget = (Player) ghast.getTarget();
                double deltaX = ghastTarget.getX() - ghast.getX();
                double deltaY = ghastTarget.getY() - ghast.getY();
                double deltaZ = ghastTarget.getZ() - ghast.getZ();
                // Calculate the distance between blaze and player
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                // Normalize the direction vector to maintain constant speed
                double velocityX = deltaX / distance;
                double velocityY = deltaY / distance;
                double velocityZ = deltaZ / distance;
                Level entityLevel = ent.level;

                CustomLargeFireball customLargeFireball = new CustomLargeFireball(
                        entityLevel,
                        ghast,
                        velocityX,
                        velocityY,
                        velocityZ,
                        2,
                        Float.parseFloat(test.get(ent).toString()));

                entityLevel.addFreshEntity(customLargeFireball);
                ent.discard();
            }
        });
    }

    public static EntityRegistry getInstance() {
        if (instance == null) {
            instance = new EntityRegistry();
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
