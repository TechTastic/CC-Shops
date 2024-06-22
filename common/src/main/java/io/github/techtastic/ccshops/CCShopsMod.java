package io.github.techtastic.ccshops;

import dan200.computercraft.impl.Peripherals;

public class CCShopsMod {
    public static final String MOD_ID = "ccshops";
    
    public static void init() {
        Peripherals.register(CCShopsExpectPlatform.getProvider());
    }
}
