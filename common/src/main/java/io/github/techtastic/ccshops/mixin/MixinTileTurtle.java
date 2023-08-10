package io.github.techtastic.ccshops.mixin;

import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import io.github.techtastic.ccshops.turtle.ShopAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileTurtle.class)
public abstract class MixinTileTurtle {
    @Shadow public abstract ITurtleAccess getAccess();

    @Redirect(
            method = "createComputer",
            at = @At(
                    target = "Ldan200/computercraft/shared/computer/core/ServerComputer;addAPI(Ldan200/computercraft/api/lua/ILuaAPI;)V",
                    value = "INVOKE"
            ),
            remap = false
    )
    private void ccshops$addShopAPI(ServerComputer instance, ILuaAPI api) {
        instance.addAPI(api);
        instance.addAPI(new ShopAPI(this.getAccess()));
    }
}
