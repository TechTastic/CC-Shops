package io.github.techtastic.ccshops.forge;

import dev.architectury.platform.forge.EventBuses;
import io.github.techtastic.ccshops.CCShopsMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.logging.Logger;

@Mod(CCShopsMod.MOD_ID)
public class CCShopsModForge {
    public static final Logger LOGGER = Logger.getLogger(CCShopsMod.MOD_ID);

    public CCShopsModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(CCShopsMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        CCShopsMod.init();
    }
}
