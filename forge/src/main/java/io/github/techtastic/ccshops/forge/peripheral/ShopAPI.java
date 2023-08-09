package io.github.techtastic.ccshops.forge.peripheral;

import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.turtle.core.InteractDirection;
import io.github.techtastic.ccshops.forge.util.IShopAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import wolforce.simpleshops.SimpleShopTileEntity;

public class ShopAPI implements ILuaAPI {
    private final ITurtleAccess turtle;

    public ShopAPI(ITurtleAccess turtle) {
        this.turtle = turtle;
    }

    @Override
    public String[] getNames() {
        return new String[] {"shop"};
    }

    public final MethodResult testLevel(ITurtleCommand command) {
        return this.turtle.getLevel().isClientSide ?
                MethodResult.of("clientside") : this.turtle.executeCommand(command);
    }

    @LuaFunction
    public final MethodResult buy() throws LuaException {
        return testLevel(new TurtleShopCommand(getShopFromSide(InteractDirection.FORWARD)));
    }

    @LuaFunction
    public final MethodResult buyUp() throws LuaException {
        return testLevel(new TurtleShopCommand(getShopFromSide(InteractDirection.UP)));
    }

    @LuaFunction
    public final MethodResult buyDown() throws LuaException {
        return testLevel(new TurtleShopCommand(getShopFromSide(InteractDirection.DOWN)));
    }

    private SimpleShopTileEntity getShopFromSide(InteractDirection interact) throws LuaException {
        ServerLevel level = (ServerLevel) this.turtle.getLevel();
        BlockPos pos = this.turtle.getPosition().relative(interact.toWorldDir(this.turtle));

        if (level.getBlockEntity(pos) instanceof SimpleShopTileEntity shop)
            return shop;
        return null;
    }

    public static class TurtleShopCommand implements ITurtleCommand {
        private final SimpleShopTileEntity shop;

        public TurtleShopCommand(SimpleShopTileEntity shop) {
            this.shop = shop;
        }

        @NotNull
        @Override
        public TurtleCommandResult execute(@NotNull ITurtleAccess iTurtleAccess) {
            if (this.shop == null)
                return TurtleCommandResult.failure("Not a Shop");

            return ((IShopAccess) this.shop).ccshops$tryBuyWithTurtle(iTurtleAccess,
                    iTurtleAccess.getInventory().getItem(iTurtleAccess.getSelectedSlot()));
        }
    }
}
