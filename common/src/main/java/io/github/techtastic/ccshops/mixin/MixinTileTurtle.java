package io.github.techtastic.ccshops.mixin;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity;
import io.github.techtastic.ccshops.turtle.ShopAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TurtleBlockEntity.class)
public abstract class MixinTileTurtle {
    @Shadow public abstract ITurtleAccess getAccess();

    @Inject(
            method = "createComputer",
            at = @At("RETURN"),
            remap = false
    )
    private void ccshops$addShopAPI(int id, CallbackInfoReturnable<ServerComputer> cir) {
        ServerComputer instance = cir.getReturnValue();
        try {
            var computerField = instance.getClass().getDeclaredField("computer");
            computerField.setAccessible(true);
            var trueComputer = computerField.get(instance);

            var computerClass = trueComputer.getClass();
            var addApiMethod = computerClass.getDeclaredMethod("addApi");
            addApiMethod.setAccessible(true);

            addApiMethod.invoke(new ShopAPI(getAccess()));
        } catch (Exception ignored) {}
    }
}
