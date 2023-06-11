package com.conkeegs.truehardcore;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import com.conkeegs.truehardcore.registries.MobRegistry;
import com.conkeegs.truehardcore.utils.TruestLogger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TrueHardcore.MODID)
@Mod.EventBusSubscriber(modid = "truehardcore")
public class TrueHardcore {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "truehardcore";
    // custom logga
    private static final Logger LOGGER = TruestLogger.getLogger();

    private static final Map<String, MobRegistry.MobProperties> modifiedMobs = MobRegistry.getInstance().getAllMobs();

    private boolean shouldShutdownServer = false;

    public TrueHardcore() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        Explosion explosion = event.getExplosion();
        DamageSource explosionDamageSource = explosion.getDamageSource();
        Entity thingThatExploded = explosion.getExploder();

        if (thingThatExploded instanceof Creeper) {
            try {
                Field explosionRadiusField = Creeper.class.getDeclaredField("explosionRadius");
                explosionRadiusField.setAccessible(true);

                int newExplosionRadius = 10;
                explosionRadiusField.setInt((Creeper) thingThatExploded, newExplosionRadius);
            } catch (Exception e) {
                LOGGER.error("Error setting creeper explosion radius - ", e.getMessage());
            }
            Set<Player> playersHurt = explosion.getHitPlayers().keySet();

            for (Player player : playersHurt) {
                double distanceFromCreeper = thingThatExploded.position().distanceTo(player.position());

                if (distanceFromCreeper >= 0.0 && distanceFromCreeper <= 2.0) {
                    player.hurt(explosionDamageSource, 30.0F);
                } else {
                    player.hurt(explosionDamageSource, (float) (30.0 / distanceFromCreeper + 11.0));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        String entityClassName = entity.getClass().getSimpleName();

        if (modifiedMobs.containsKey(entityClassName) && entity instanceof LivingEntity) {
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
                    movementSpeedAttribute.setBaseValue(mobProperties.getRandomSpeed());
                }
            }

            AttributeInstance attackDamageAttribute = ((LivingEntity) entity).getAttribute(Attributes.ATTACK_DAMAGE);

            if (attackDamageAttribute != null && mobProperties.getDamage() != null) {
                attackDamageAttribute.setBaseValue(mobProperties.getDamage());
            }
        }
    }

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
    public void onServerTick(ServerTickEvent event) {
        if (event.phase == Phase.END && shouldShutdownServer) {
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
