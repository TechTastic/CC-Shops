package io.github.techtastic.ccshops.forge;

import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class CCShopsExpectPlatformImpl {
    public static IPeripheralProvider getProvider() {
        return new CCShopsForgePeripheralProvider();
    }
}
