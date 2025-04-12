package com.conkeegs.truehardcore.registries.entities;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;

// import com.conkeegs.truehardcore.overrides.entities.CustomEvokerFangs;
// import com.conkeegs.truehardcore.overrides.entities.CustomIronGolem;
// import com.conkeegs.truehardcore.overrides.entities.CustomShulkerBullet;
// import com.conkeegs.truehardcore.overrides.entities.CustomSmallFireball;
import com.conkeegs.truehardcore.utils.TruestLogger;
import com.conkeegs.truehardcore.utils.Utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
            Level oldEntityLevel = oldEntity.level();

            // Utils.replaceEntity(event, new CustomEvokerFangs(
            // oldEntityLevel,
            // oldEntity.getX(),
            // oldEntity.getY(),
            // oldEntity.getZ(),
            // oldEntity.getYRot(),
            // 0,
            // null),
            // oldEntity,
            // oldEntityLevel);
        });

        this.addEntity(SmallFireball.class.getSimpleName(), (EntityJoinLevelEvent event) -> {
            SmallFireball oldEntity = (SmallFireball) event.getEntity();
            Level oldEntityLevel = oldEntity.level();
            Blaze blaze = (Blaze) (oldEntity.getOwner());

            if (blaze == null) {
                LOGGER.error("Blaze null upon spawn");

                return;
            }

            LivingEntity blazeTarget = blaze.getTarget();

            if (blazeTarget == null) {
                LOGGER.error("Blaze target is null");

                return;
            }

            Vec3 target = blazeTarget.getEyePosition();
            double deltaX = target.x - oldEntity.getX();
            double deltaY = target.y - oldEntity.getY();
            double deltaZ = target.z - oldEntity.getZ();
            // Calculate the distance between blaze and player
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            // Normalize the direction vector to maintain constant speed
            double velocityX = deltaX / distance;
            double velocityY = deltaY / distance;
            double velocityZ = deltaZ / distance;

            // Utils.replaceEntity(event, new CustomSmallFireball(
            // oldEntityLevel,
            // oldEntity.getX(),
            // oldEntity.getY(),
            // oldEntity.getZ(),
            // velocityX,
            // velocityY,
            // velocityZ),
            // oldEntity,
            // oldEntityLevel);
        });

        this.addEntity(ShulkerBullet.class.getSimpleName(), (EntityJoinLevelEvent event) -> {
            ShulkerBullet oldEntity = (ShulkerBullet) event.getEntity();
            Level oldEntityLevel = oldEntity.level();

            try {
                Field field = ShulkerBullet.class.getDeclaredField("f_37312_");

                field.setAccessible(true);

                Entity target = (Entity) field.get(oldEntity);

                // Utils.replaceEntity(event, new CustomShulkerBullet(
                // oldEntityLevel,
                // (Shulker) oldEntity.getOwner(),
                // target,
                // oldEntity.getMotionDirection().getAxis()),
                // oldEntity,
                // oldEntityLevel);
            } catch (Exception e) {
                LOGGER.error("Error replacing Shulker bullet - {}", e);
            }
        });

        this.addEntity(IronGolem.class.getSimpleName(), (EntityJoinLevelEvent event) -> {
            IronGolem oldEntity = (IronGolem) event.getEntity();
            Level oldEntityLevel = oldEntity.level();

            // Utils.replaceEntity(event, new CustomIronGolem(
            // (EntityType<? extends IronGolem>) oldEntity.getType(),
            // oldEntityLevel,
            // oldEntity.getX(),
            // oldEntity.getY(),
            // oldEntity.getZ(),
            // oldEntity.getYRot()),
            // oldEntity,
            // oldEntityLevel);
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
