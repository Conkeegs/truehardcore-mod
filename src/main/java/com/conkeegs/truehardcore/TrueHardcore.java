package com.conkeegs.truehardcore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import com.conkeegs.truehardcore.overrides.entities.CustomMagmaCube;
import com.conkeegs.truehardcore.overrides.entities.CustomSlime;
import com.conkeegs.truehardcore.overrides.objects.CustomExplosion;
import com.conkeegs.truehardcore.registries.entities.EntityRegistry;
import com.conkeegs.truehardcore.registries.explosions.ExplosionRegistry;
import com.conkeegs.truehardcore.registries.mobs.MobRegistry;
import com.conkeegs.truehardcore.utils.TruestLogger;
import com.conkeegs.truehardcore.utils.Utils;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TrueHardcore.MODID)
@Mod.EventBusSubscriber(modid = "truehardcore")
public class TrueHardcore {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "truehardcore";
    // custom logga
    private static final Logger LOGGER = TruestLogger.getLogger();

    private static final Map<String, MobRegistry.MobProperties> modifiedMobs = MobRegistry.getInstance().getAllMobs();
    private static final Map<String, Consumer<EntityJoinLevelEvent>> modifiedEntities = EntityRegistry.getInstance()
            .getAllEntities();
    private static final Map<String, Float> modifiedExplosions = ExplosionRegistry.getInstance().getAllEntities();

    private static boolean shouldShutdownServer = false;

    private static CustomExplosion customExplosion = null;

    private static MinecraftServer server;

    public TrueHardcore() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void handleServerStarted(ServerStartedEvent event) {
        server = event.getServer();

        server.overworld().getGameRules().getRule(GameRules.RULE_DOINSOMNIA).set(false, server);

        // print mobs
        ForgeRegistries.ENTITY_TYPES.getValues().stream()
                .filter(entityType -> entityType.canSummon())
                .forEach(entityType -> {
                    String entityName = entityType.getDescription().getString();
                    LOGGER.info(entityName);
                });

        LOGGER.error("This is a testing error log from truehardcore");
    }

    @SubscribeEvent
    public static void onSleep(SleepingTimeCheckEvent event) {
        event.setResult(Result.DENY);
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Start event) {
        Explosion explosion = event.getExplosion();
        Entity thingThatExploded = explosion.getExploder();

        if (thingThatExploded == null) {
            return;
        }

        String thingThatExplodedClassName = thingThatExploded.getClass().getSimpleName();

        LOGGER.info("Thing that exploded: '{}'", thingThatExplodedClassName);

        if (modifiedExplosions.containsKey(thingThatExplodedClassName)) {
            event.setCanceled(true);

            customExplosion = new CustomExplosion(
                    thingThatExploded.level(),
                    thingThatExploded,
                    explosion.getDamageSource(),
                    null,
                    thingThatExploded.getX(),
                    thingThatExploded.getY(),
                    thingThatExploded.getZ(),
                    modifiedExplosions.get(thingThatExplodedClassName),
                    false,
                    Explosion.BlockInteraction.DESTROY);

            customExplosion.handleExplosion();

            customExplosion = null;
        }
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        String entityClassName = entity.getClass().getSimpleName();

        if (modifiedMobs.containsKey(entityClassName)) {
            handleLivingEntitySpawn(entity, entityClassName);
        } else if (modifiedEntities.containsKey(entityClassName)) {
            handleEntitySpawn(event, entityClassName);
        }
    }

    public static void handleLivingEntitySpawn(Entity entity, String entityClassName) {
        MobRegistry.MobProperties mobProperties = modifiedMobs.get(entityClassName);
        Float mobSpeed = mobProperties.getSpeed();
        AttributeInstance movementSpeedAttribute = ((LivingEntity) entity).getAttribute(Attributes.MOVEMENT_SPEED);

        if (movementSpeedAttribute != null) {
            if (mobSpeed != null) {
                movementSpeedAttribute.setBaseValue(mobSpeed);
            }

            if (mobProperties.getRandomSpeeds() != null) {
                movementSpeedAttribute.setBaseValue(mobProperties.getRandomSpeed());
            }

            if (entity instanceof Zombie zombie && zombie.isBaby()) {
                AttributeInstance zombieAttributeInstance = zombie.getAttribute(Attributes.MOVEMENT_SPEED);

                if (zombieAttributeInstance == null) {
                    LOGGER.error("Zombie movement speed attribute is null, cannot set custom zombie speed");

                    return;
                }

                movementSpeedAttribute
                        .setBaseValue(zombieAttributeInstance.getValue());
            } else if (entity instanceof Piglin piglin && piglin.isBaby()) {
                AttributeInstance piglinAttributeInstance = piglin.getAttribute(Attributes.MOVEMENT_SPEED);

                if (piglinAttributeInstance == null) {
                    LOGGER.error("Piglin movement speed attribute is null, cannot set custom piglin speed");

                    return;
                }

                movementSpeedAttribute
                        .setBaseValue(piglinAttributeInstance.getValue());
            }

            if (entity instanceof Wolf wolf) {
                AttributeInstance wolfAttributeInstance = wolf.getAttribute(Attributes.MAX_HEALTH);

                if (wolfAttributeInstance == null) {
                    LOGGER.error("Wolf health attribute is null, cannot set custom wolf health");

                    return;
                }

                wolfAttributeInstance.setBaseValue(20.0D);
            }
        }

        AttributeInstance attackDamageAttribute = ((LivingEntity) entity).getAttribute(Attributes.ATTACK_DAMAGE);

        if (attackDamageAttribute != null && mobProperties.getDamage() != null) {
            attackDamageAttribute.setBaseValue(mobProperties.getDamage());
        }
    }

    public static void handleEntitySpawn(EntityJoinLevelEvent event, String entityClassName) {
        Consumer<EntityJoinLevelEvent> callback = modifiedEntities.get(entityClassName);

        callback.accept(event);
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            // ServerPlayer playerWhoDied = (ServerPlayer) event.getEntity();
            List<ServerPlayer> playerList = new ArrayList<ServerPlayer>(server.getPlayerList().getPlayers());

            for (ServerPlayer player : playerList) {
                player.connection
                        .disconnect(Component.literal(event.getSource().getLocalizedDeathMessage(player).getString()));
            }

            playerList.clear();
            server.overworld().disconnect();

            shouldShutdownServer = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void handleTick(ServerTickEvent event) {
        if (event.phase == Phase.END && shouldShutdownServer) {
            handleWorldDeletion(event);
        }
    }

    public static void handleWorldDeletion(ServerTickEvent event) {
        try {
            server.stopServer();
            Util.shutdownExecutors();
            SkullBlockEntity.clear();
        } catch (Exception exception) {
            if (!(exception instanceof FileNotFoundException)) {
                LOGGER.error("Error shutting down server - {}", exception.getMessage());
            }
        }

        List<String> worldsToDelete = Arrays.asList("world", "world_nether", "world_the_end");

        for (String worldName : worldsToDelete) {
            File worldFolder = new File(worldName);

            if (worldFolder != null && worldFolder.exists()) {
                try {
                    FileUtils.deleteDirectory(worldFolder);

                    LOGGER.info("World '{}' deleted successfully.", worldFolder.getName());
                } catch (IOException ioe) {
                    LOGGER.error("Error deleting world folder - '{}'", ioe.getMessage());
                }
            } else {
                LOGGER.info("World folder '{}' not found. Skipping it.", worldName);
            }
        }

        String propertiesPath = "server.properties";
        Properties properties = new Properties();

        try (FileInputStream inputStream = new FileInputStream(propertiesPath)) {
            properties.load(inputStream);
        } catch (IOException ioe) {
            LOGGER.error("Error reading server properties file - {}", ioe.getMessage());

            return;
        }

        properties.setProperty("level-seed", String.valueOf(RandomSupport.generateUniqueSeed()));

        try (FileOutputStream outputStream = new FileOutputStream(propertiesPath)) {
            properties.store(outputStream, "Modified server properties");

            LOGGER.info("New world seed written successfully.");
        } catch (IOException ioe) {
            LOGGER.error("Error writing to server properties file - {}", ioe.getMessage());
        }

        LOGGER.info("Server shut down successfully.");

        shouldShutdownServer = false;
    }

    @SubscribeEvent
    public static void onSlimeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        Entity oldEntity = event.getEntity();

        if (!event.isCancelable()) {
            LOGGER.error("Cannot cancel MobSpawnEvent.FinalizeSpawn event");

            return;
        }

        if (oldEntity instanceof Slime && !(oldEntity instanceof CustomSlime)) {
            String oldEntityClassName = oldEntity.getClass().getSimpleName();
            CustomSlime newEntity = null;
            Level oldEntityLevel = oldEntity.level();

            if (oldEntityClassName.equals("Slime")) {
                newEntity = new CustomSlime(((Slime) oldEntity).getType(), oldEntityLevel);
            } else if (oldEntityClassName.equals("MagmaCube")) {
                newEntity = new CustomMagmaCube((EntityType<? extends MagmaCube>) oldEntity.getType(), oldEntityLevel);
            }

            if (newEntity == null) {
                LOGGER.error("Custom slime or magmacube is null, cannot replace default slime");

                return;
            }

            Utils.replaceEntity(event, newEntity,
                    oldEntity,
                    oldEntityLevel);

            newEntity.setYRot(oldEntity.getYRot() * (180F / (float) Math.PI));
            newEntity.setPos(oldEntity.getX(), oldEntity.getY(), oldEntity.getZ());
            newEntity.setSize(new Random().nextInt(20) + 1, true);
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods
    // in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
