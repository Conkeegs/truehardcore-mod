package com.conkeegs.truehardcore;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TrueHardcore.MODID)
public class TrueHardcore {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "truehardcore";
    // custom logga
    private static final Logger LOGGER = LoggerFactory.getLogger("[TRUEHARDCORE]");

    private boolean shouldShutdownServer = false;
    public TrueHardcore() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            // ServerPlayer playerWhoDied = (ServerPlayer) event.getEntity();
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

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            // ServerPlayer playerWhoDied = (ServerPlayer) event.getEntity();
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            List<ServerPlayer> playerList = new ArrayList<>(server.getPlayerList().getPlayers());

            for (ServerPlayer player : playerList) {
                player.connection
                        .disconnect(Component.literal(event.getSource().getLocalizedDeathMessage(player).getString()));
            }

            shouldShutdownServer = true;
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
