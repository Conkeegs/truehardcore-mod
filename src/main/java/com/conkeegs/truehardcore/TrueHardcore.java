package com.conkeegs.truehardcore;

import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TrueHardcore.MODID)
public class TrueHardcore {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "truehardcore";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under
    // the "truehardcore" namespace
    // public static final DeferredRegister<Block> BLOCKS =
    // DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // // Create a Deferred Register to hold Items which will all be registered
    // under
    // // the "truehardcore" namespace
    // public static final DeferredRegister<Item> ITEMS =
    // DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // Creates a new Block with the id "truehardcore:example_block", combining the
    // namespace and path
    // public static final RegistryObject<Block> EXAMPLE_BLOCK =
    // BLOCKS.register("example_block",
    // () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    // // Creates a new BlockItem with the id "truehardcore:example_block",
    // combining
    // // the namespace and path
    // public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM =
    // ITEMS.register("example_block",
    // () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    public TrueHardcore() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the Deferred Register to the mod event bus so blocks get registered
        // BLOCKS.register(modEventBus);
        // // Register the Deferred Register to the mod event bus so items get
        // registered
        // ITEMS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer playerWhoDied = (ServerPlayer) event.getEntity();
            MinecraftServer server = playerWhoDied.getServer();

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.connection.disconnect(Component.empty());
            }

            // Replace "yourWorldFolderName" with the actual name of your world folder
            File worldFolder = server.getWorldPath(LevelResource.ROOT).toFile();

            if (worldFolder != null && worldFolder.exists()) {
                try {
                    FileUtils.deleteDirectory(worldFolder);

                    System.out.printf("%s deleted successfully.", worldFolder.getName());
                } catch (IOException e) {
                    System.out.printf("TRUEHARDCORE:");
                }
            } else {
                System.out.println("World folder not found.");
            }
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods
    // in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            // LOGGER.info("HELLO FROM CLIENT SETUP");
            // LOGGER.info("MINECRAFT NAME >> {}",
            // Minecraft.getInstance().getUser().getName());
        }
    }
}
