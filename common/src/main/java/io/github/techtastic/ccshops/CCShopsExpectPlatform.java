package io.github.techtastic.ccshops;

import dan200.computercraft.api.peripheral.IPeripheralProvider;
import dev.architectury.injectables.annotations.ExpectPlatform;

public class CCShopsExpectPlatform {
    @ExpectPlatform
    public static IPeripheralProvider getProvider() {
        throw new AssertionError();
    }
}
