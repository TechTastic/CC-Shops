package io.github.techtastic.ccshops.forge;

import io.github.techtastic.ccshops.CCShopsExpectPlatform;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class CCShopsExpectPlatformImpl {
    /**
     * This is our actual method to {@link CCShopsExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
