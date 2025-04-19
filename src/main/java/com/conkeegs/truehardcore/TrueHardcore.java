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
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import com.conkeegs.truehardcore.overrides.objects.CustomExplosion;
import com.conkeegs.truehardcore.registries.entities.EntityRegistry;
import com.conkeegs.truehardcore.registries.explosions.ExplosionRegistry;
import com.conkeegs.truehardcore.utils.TruestLogger;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Mod that makes Minecraft sooooo hard...eeeeeveryone deet...
 */
// The value here should match an entry in the META-INF/mods.toml file
@Mod(TrueHardcore.MODID)
@Mod.EventBusSubscriber(modid = "truehardcore")
public class TrueHardcore {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "truehardcore";
    // custom logga
    private static final Logger LOGGER = TruestLogger.getLogger();
    private static final Map<String, Consumer<EntityJoinLevelEvent>> modifiedEntities = EntityRegistry.getInstance()
            .getAllEntities();
    private static final Map<String, Float> modifiedExplosions = ExplosionRegistry.getInstance().getAllEntities();
    private static boolean shouldShutdownServer = false;
    private static CustomExplosion customExplosion = null;
    private static MinecraftServer server;

    /**
     * Truehardcore constructor.
     */
    public TrueHardcore() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        // EntityRegister.ENTITIES.register(bus);
    }

    /**
     * Handle server starting.
     *
     * @param event server start event
     */
    @SubscribeEvent
    public static void handleServerStarted(ServerStartedEvent event) {
        TrueHardcore.server = event.getServer();

        // sleeping not allowed, so we don't want phantoms spawning
        event.getServer().overworld().getGameRules().getRule(GameRules.RULE_DOINSOMNIA).set(false, server);
    }

    /**
     * Handle entity spawn events in client and server.
     *
     * @param event entity spawn event
     */
    public static void handleEntityJoinLevel(EntityJoinLevelEvent event) {
        String entityDescriptionId = event.getEntity().getType().getDescriptionId();

        // if it's an entity we want to modify
        if (modifiedEntities.containsKey(entityDescriptionId)) {
            modifiedEntities.get(entityDescriptionId).accept(event);
        }
    }

    /**
     * Handle sleep events.
     *
     * @param event sleep event
     */
    @SubscribeEvent
    public static void onSleep(SleepingTimeCheckEvent event) {
        // don't allow sleeping...
        event.setResult(Result.DENY);
    }

    /**
     * Handle explosion detonation events.
     *
     * @param event explosion detonation event
     */
    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Start event) {
        Explosion explosion = event.getExplosion();
        Entity thingThatExploded = explosion.getExploder();

        if (thingThatExploded == null) {
            LOGGER.error("Explosion detonation had a null exploder.");

            return;
        }

        String thingThatExplodedClassName = thingThatExploded.getClass().getSimpleName();

        // make sure entity that exploded is something we want to assign a custom
        // explosion to
        if (!modifiedExplosions.containsKey(thingThatExplodedClassName)) {
            return;
        }

        // cancel the explosion and replace it with out custom one
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

    /**
     * Handle entity spawn events.
     *
     * @param event entity spawn event
     */
    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        handleEntityJoinLevel(event);
    }

    /**
     * Handle living entity death events.
     *
     * @param event the living entity death event
     */
    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            // ServerPlayer playerWhoDied = (ServerPlayer) event.getEntity();
            List<ServerPlayer> playerList = new ArrayList<ServerPlayer>(
                    TrueHardcore.server.getPlayerList().getPlayers());

            // disconnect everyone and make sure they can see the reason of the death
            for (ServerPlayer player : playerList) {
                player.connection
                        .disconnect(Component.literal(event.getSource().getLocalizedDeathMessage(player).getString()));
            }

            playerList.clear();
            TrueHardcore.server.overworld().disconnect();

            shouldShutdownServer = true;
        }
    }

    /**
     * Handle server tick events.
     *
     * @param event the server tick event
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void handleTick(ServerTickEvent event) {
        // make sure we're at the end of the current tick and only then handle the
        // world's
        // deletion
        if (event.phase == Phase.END && shouldShutdownServer) {
            handleWorldDeletion(event);
        }
    }

    /**
     * Handle world deletion after a player dies.
     *
     * @param event the server tick event to delete the world at the end of
     */
    public static void handleWorldDeletion(ServerTickEvent event) {
        try {
            TrueHardcore.server.stopServer();
            Util.shutdownExecutors();
            SkullBlockEntity.clear();
        } catch (Exception exception) {
            if (!(exception instanceof FileNotFoundException)) {
                LOGGER.error("Error shutting down server - {}", exception.getMessage());
            }
        }

        List<String> worldsToDelete = Arrays.asList("world", "world_nether", "world_the_end");

        // delete all world folders in "worldsToDelete"
        for (String worldName : worldsToDelete) {
            File worldFolder = new File(worldName);

            if (!worldFolder.exists()) {
                LOGGER.error("World directory '{}' could not be deleted since it doesn't exist", worldName);

                continue;
            }

            try {
                FileUtils.deleteDirectory(worldFolder);

                LOGGER.info("World '{}' deleted successfully.", worldFolder.getName());
            } catch (IOException ioe) {
                LOGGER.error("Error deleting world folder - '{}'", ioe.getMessage());
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

        // reset the overworld seed to something random in the server properties file
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

    /**
     * Handle slime spawn events.
     *
     * @param event the slime spawn event
     */
    // @SubscribeEvent
    // public static void onSlimeSpawn(MobSpawnEvent.FinalizeSpawn event) {
    // Entity oldEntity = event.getEntity();

    // if (!event.isCancelable()) {
    // LOGGER.error("Cannot cancel MobSpawnEvent.FinalizeSpawn event");

    // return;
    // }

    // // if (oldEntity instanceof BaseCreeper baseCreeper && !(oldEntity instanceof
    // // CustomBaseCreeper)) {
    // // CustomBaseCreeper newEntity = null;
    // // Level oldEntityLevel = oldEntity.level();

    // // EntityType grum = (EntityType<? extends BaseCreeper>)
    // baseCreeper.getType();
    // // CreeperType grumBum = baseCreeper.type;

    // // newEntity = new CustomBaseCreeper((EntityType<? extends BaseCreeper>)
    // // baseCreeper.getType(), oldEntityLevel,
    // // baseCreeper.type);

    // // Utils.replaceEntity(event, newEntity,
    // // oldEntity,
    // // oldEntityLevel);

    // // newEntity.setYRot(oldEntity.getYRot() * (180F / (float) Math.PI));
    // // newEntity.setPos(oldEntity.getX(), oldEntity.getY(), oldEntity.getZ());

    // // return;
    // // }

    // if (oldEntity instanceof Slime && !(oldEntity instanceof CustomSlime)) {
    // String oldEntityClassName = oldEntity.getClass().getSimpleName();
    // CustomSlime newEntity = null;
    // Level oldEntityLevel = oldEntity.level();

    // if (oldEntityClassName.equals("Slime")) {
    // newEntity = new CustomSlime(((Slime) oldEntity).getType(), oldEntityLevel);
    // } else if (oldEntityClassName.equals("MagmaCube")) {
    // newEntity = new CustomMagmaCube((EntityType<? extends MagmaCube>)
    // oldEntity.getType(), oldEntityLevel);
    // }

    // if (newEntity == null) {
    // LOGGER.error("Custom slime or magmacube is null, cannot replace default
    // slime");

    // return;
    // }

    // Utils.replaceEntity(event, newEntity,
    // oldEntity,
    // oldEntityLevel);

    // newEntity.setYRot(oldEntity.getYRot() * (180F / (float) Math.PI));
    // newEntity.setPos(oldEntity.getX(), oldEntity.getY(), oldEntity.getZ());
    // newEntity.setSize(new Random().nextInt(20) + 1, true);

    // return;
    // }
    // }

    @Mod.EventBusSubscriber(modid = TrueHardcore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public class ModBusEvents {
        // private static final Logger LOGGER = TruestLogger.getLogger();

        // @SubscribeEvent
        // public static void newEntityAttributes(EntityAttributeCreationEvent event) {
        // event.put(EntityRegister.CUSTOM_ENDER_DRAGON.get(),
        // CustomEnderDragon.createAttributes().build());
        // }

        // @SubscribeEvent
        // public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event)
        // {
        // event.register(EntityRegister.CUSTOM_ENDER_DRAGON.get(),
        // SpawnPlacements.Type.ON_GROUND,
        // Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules,
        // SpawnPlacementRegisterEvent.Operation.OR);
        // }
    }

    // You can use EventBusSubscriber to automatically register all static methods
    // in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientForgeBusEvents {
        // @SubscribeEvent
        // public static void onEntitySpawn(EntityJoinLevelEvent event) {
        // LOGGER.info("joined: {}", event.getEntity().getType().getDescriptionId());

        // handleEntityJoinLevel(event);
        // }

        @SubscribeEvent
        public static void onHurt(LivingHurtEvent event) {
            LOGGER.info("Entity {} received {} damage", event.getEntity().getType().getDescriptionId(),
                    event.getAmount());
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        // @SubscribeEvent
        // public static void registerRenderers(EntityRenderersEvent.RegisterRenderers
        // event) {
        // LOGGER.info("loaded renderers");

        // event.registerEntityRenderer(EntityRegister.CUSTOM_ENDER_DRAGON.get(),
        // EnderDragonRenderer::new);
        // }
    }
}
