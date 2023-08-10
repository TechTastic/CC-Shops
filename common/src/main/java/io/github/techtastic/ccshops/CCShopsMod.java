package io.github.techtastic.ccshops;

import com.google.common.base.Suppliers;
import dan200.computercraft.api.ComputerCraftAPI;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class CCShopsMod {
    public static final String MOD_ID = "ccshops";
    
    public static void init() {
        ComputerCraftAPI.registerPeripheralProvider(CCShopsExpectPlatform.getProvider());
    }
}
