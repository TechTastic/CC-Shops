package io.github.techtastic.ccshops.turtle;

import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.turtle.core.InteractDirection;
import io.github.techtastic.ccshops.util.IShopAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

    @LuaFunction
    public final MethodResult buy() {
        return this.turtle.executeCommand(new TurtleShopCommand(InteractDirection.FORWARD));
    }

    @LuaFunction
    public final MethodResult buyUp() {
        return this.turtle.executeCommand(new TurtleShopCommand(InteractDirection.UP));
    }

    @LuaFunction
    public final MethodResult buyDown() {
        return this.turtle.executeCommand(new TurtleShopCommand(InteractDirection.DOWN));
    }

    public static class TurtleShopCommand implements ITurtleCommand {
        private final InteractDirection direction;

        public TurtleShopCommand(InteractDirection direction) {
            this.direction = direction;
        }

        @NotNull
        @Override
        public TurtleCommandResult execute(@NotNull ITurtleAccess iTurtleAccess) {
            Level level = iTurtleAccess.getLevel();
            BlockPos pos = iTurtleAccess.getPosition().relative(this.direction.toWorldDir(iTurtleAccess));
            BlockEntity be = level.getBlockEntity(pos);

            if (be instanceof SimpleShopTileEntity shop)
                return ((IShopAccess) shop).ccshops$tryBuyWithTurtle(iTurtleAccess,
                    iTurtleAccess.getInventory().getItem(iTurtleAccess.getSelectedSlot()));
            return TurtleCommandResult.failure("Not a Shop");
        }
    }
}
