package com.conkeegs.truehardcore.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public final class Utils {
    public static final void replaceEntity(EntityJoinLevelEvent event, Entity newEntity, Entity oldEntity,
            Level oldEntityLevel) {
        event.setCanceled(true);

        // 7.0?
        oldEntityLevel.addFreshEntity(newEntity);
        oldEntity.discard();
    }

    public static final void replaceEntity(LivingSpawnEvent event, Entity newEntity, Entity oldEntity,
            Level oldEntityLevel) {
        event.setCanceled(true);

        // 7.0?
        oldEntityLevel.addFreshEntity(newEntity);
        oldEntity.discard();
    }
}
