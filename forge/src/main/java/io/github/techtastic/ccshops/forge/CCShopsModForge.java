package io.github.techtastic.ccshops.forge;

import dan200.computercraft.api.ComputerCraftAPI;
import dev.architectury.platform.forge.EventBuses;
import io.github.techtastic.ccshops.CCShopsMod;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CCShopsMod.MOD_ID)
public class CCShopsModForge {
    public CCShopsModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(CCShopsMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        CCShopsMod.init();

        ComputerCraftAPI.registerPeripheralProvider(new CCShopsPeripheralProvider());
    }
}
