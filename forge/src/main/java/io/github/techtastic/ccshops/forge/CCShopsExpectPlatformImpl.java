package io.github.techtastic.ccshops.forge;

import dan200.computercraft.api.peripheral.IPeripheralProvider;
import dan200.computercraft.shared.peripheral.generic.data.ItemData;
import io.github.techtastic.ccshops.CCShopsExpectPlatform;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.HashMap;

public class CCShopsExpectPlatformImpl {
    /**
     * This is our actual method to {@link CCShopsExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static IPeripheralProvider getProvider() {
        return new CCShopsForgePeripheralProvider();
    }
}
