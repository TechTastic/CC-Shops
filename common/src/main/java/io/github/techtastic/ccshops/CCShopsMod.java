package io.github.techtastic.ccshops;

import dan200.computercraft.api.ComputerCraftAPI;

public class CCShopsMod {
    public static final String MOD_ID = "ccshops";
    
    public static void init() {
        ComputerCraftAPI.registerPeripheralProvider(CCShopsExpectPlatform.getProvider());
    }
}
