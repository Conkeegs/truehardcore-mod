package com.conkeegs.truehardcore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import com.conkeegs.truehardcore.entities.creeper.CreeperExplosion;
import com.conkeegs.truehardcore.registries.EntityRegistry;
import com.conkeegs.truehardcore.registries.MobRegistry;
import com.conkeegs.truehardcore.utils.TruestLogger;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TrueHardcore.MODID)
@Mod.EventBusSubscriber(modid = "truehardcore")
public class TrueHardcore {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "truehardcore";
    // custom logga
    private static final Logger LOGGER = TruestLogger.getLogger();

    private static final Map<String, MobRegistry.MobProperties> modifiedMobs = MobRegistry.getInstance().getAllMobs();
    private static final Map<String, Consumer<Entity>> modifiedEntities = EntityRegistry.getInstance()
            .getAllEntities();

    private static boolean creeperExploded = false;
    private static boolean shouldShutdownServer = false;

    private static CreeperExplosionHandler creeperExplosion = null;

    private static class CreeperExplosionHandler {
        DamageSource explosionDamageSource;
        Creeper creeper;

        public CreeperExplosionHandler(DamageSource explosionDamageSource, Creeper creeper) {
            this.explosionDamageSource = explosionDamageSource;
            this.creeper = creeper;
        }

        public void handleExplosion() {
            float explosionRadius = 10F;

            CreeperExplosion customExplosion = new CreeperExplosion(
                    creeper.level,
                    creeper,
                    explosionDamageSource,
                    null,
                    creeper.getX(),
                    creeper.getY(),
                    creeper.getZ(),
                    explosionRadius,
                    false,
                    Explosion.BlockInteraction.DESTROY);

            customExplosion.explode();

            List<BlockPos> affectedBlocks = customExplosion.getToBlow();

            for (BlockPos blockPos : affectedBlocks) {
                creeper.level.removeBlock(blockPos, false);
            }

            customExplosion.finalizeExplosion(true);
            creeper.discard();
        }
    }

    public TrueHardcore() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // print mobs
        // ForgeRegistries.ENTITY_TYPES.getValues().stream()
        // .filter(entityType -> entityType.canSummon())
        // .forEach(entityType -> {
        // String entityName = entityType.getDescription().getString();
        // LOGGER.info(entityName);
        // });
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Start event) {
        Explosion explosion = event.getExplosion();
        Entity thingThatExploded = explosion.getExploder();

        if (thingThatExploded instanceof Creeper creeper) {
            event.setCanceled(true);

            creeperExplosion = new TrueHardcore.CreeperExplosionHandler(explosion.getDamageSource(), creeper);
            creeperExploded = true;
        }
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        String entityClassName = entity.getClass().getSimpleName();

        if (modifiedMobs.containsKey(entityClassName) && entity instanceof LivingEntity) {
            handleLivingEntitySpawn(entity, entityClassName);
        } else if (modifiedEntities.containsKey(entityClassName) && !(entity instanceof LivingEntity)) {
            handleEntitySpawn(entity, entityClassName);
        }
    }

    public static void handleEntitySpawn(Entity entity, String entityClassName) {
        Consumer<Entity> callback = modifiedEntities.get(entityClassName);

        callback.accept((AbstractArrow) entity);
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

            if (entity instanceof Zombie && ((Zombie) entity).isBaby()) {
                movementSpeedAttribute
                        .setBaseValue(((Zombie) entity).getAttribute(Attributes.MOVEMENT_SPEED).getValue());
            }
        }

        AttributeInstance attackDamageAttribute = ((LivingEntity) entity).getAttribute(Attributes.ATTACK_DAMAGE);

        if (attackDamageAttribute != null && mobProperties.getDamage() != null) {
            attackDamageAttribute.setBaseValue(mobProperties.getDamage());
        }
    }

    // @SubscribeEvent
    // public static void onEntityDamage(LivingDamageEvent event) {
    // Entity perpetrator = event.getSource().getDirectEntity();

    // if (perpetrator == null) {
    // return;
    // }

    // String perpetratorClassName = perpetrator.getClass().getSimpleName();

    // if (modifiedEntities.containsKey(perpetratorClassName) && perpetrator
    // instanceof Entity) {
    // float damage = modifiedEntities.get(perpetratorClassName);

    // event.setAmount(damage);
    // }
    // }

    // @SubscribeEvent
    // public void onPlayerDeath(LivingDeathEvent event) {
    // if (event.getEntity() instanceof ServerPlayer) {
    // // ServerPlayer playerWhoDied = (ServerPlayer) event.getEntity();
    // MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    // List<ServerPlayer> playerList = new
    // ArrayList<>(server.getPlayerList().getPlayers());

    // for (ServerPlayer player : playerList) {
    // player.connection
    // .disconnect(Component.literal(event.getSource().getLocalizedDeathMessage(player).getString()));
    // }

    // shouldShutdownServer = true;
    // }
    // }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onServerTick(ServerTickEvent event) {
        if (event.phase == Phase.END && shouldShutdownServer) {
            handleWorldDeletion(event);
        }

        if (creeperExploded) {
            creeperExplosion.handleExplosion();
            creeperExploded = false;
        }
    }

    public static void handleWorldDeletion(ServerTickEvent event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        try {
            server.stopServer();
        } catch (Exception exception) {
            if (!(exception instanceof FileNotFoundException)) {
                LOGGER.error("Error shutting down server - ", exception.getMessage());
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
                    LOGGER.error("Error deleting world folder - ", ioe.getMessage());
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
            LOGGER.error("Error reading server properties file - ", ioe.getMessage());

            return;
        }

        properties.setProperty("level-seed", String.valueOf(WorldOptions.randomSeed()));

        try (FileOutputStream outputStream = new FileOutputStream(propertiesPath)) {
            properties.store(outputStream, "Modified server properties");

            LOGGER.info("New world seed written successfully.");
        } catch (IOException ioe) {
            LOGGER.error("Error writing to server properties file - ", ioe.getMessage());
        }

        LOGGER.info("Server shut down successfully.");

        shouldShutdownServer = false;
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
