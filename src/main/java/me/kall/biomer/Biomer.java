package me.kall.biomer;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Biomer.MOD_ID)
public final class Biomer {
    public static final String MOD_ID = "biomer";
    public static final Logger LOGGER = LogManager.getLogger(Biomer.class);

    public Biomer(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
    }
}
