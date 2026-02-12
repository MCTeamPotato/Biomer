package me.kall.biomer;

import me.kall.biomer.config.BiomeArgs;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(Biomer.MOD_ID)
@Mod.EventBusSubscriber(modid = Biomer.MOD_ID)
public final class Biomer {
    public static final String MOD_ID = "biomer";
    public static final Logger LOGGER = LogManager.getLogger(Biomer.class);

    public Biomer(@NotNull FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();

    }

    @SubscribeEvent
    public static void initBiomeArgs(@NotNull ServerAboutToStartEvent event) {
        BiomeArgs.gameBiomeArgs = new BiomeArgs(event.getServer());
        Path biomeJson = FMLPaths.CONFIGDIR.get().resolve("biomer.json");
        if (!biomeJson.toFile().exists()) {
            try {
                Files.writeString(biomeJson, BiomeArgs.gameBiomeArgs.toString());
            } catch (IOException exception) {
                LOGGER.error("Failed to create biomer.json", exception);
            }
        } else {
            try {
                BiomeArgs fileBiomeArgs = BiomeArgs.fromString(Files.readString(biomeJson));
                
            } catch (IOException exception) {
                LOGGER.error("Failed to read biomer.json", exception);
            }
        }
    }
}
