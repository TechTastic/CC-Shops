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
        return this.turtle.executeCommand(new TurtleBuyCommand(InteractDirection.FORWARD));
    }
    @LuaFunction
    public final MethodResult buyUp() {
        return this.turtle.executeCommand(new TurtleBuyCommand(InteractDirection.UP));
    }
    @LuaFunction
    public final MethodResult buyDown() {
        return this.turtle.executeCommand(new TurtleBuyCommand(InteractDirection.DOWN));
    }


    @LuaFunction
    public final MethodResult restock() {
        return this.turtle.executeCommand(new TurtleRestockCommand(InteractDirection.FORWARD));
    }
    @LuaFunction
    public final MethodResult restockUp() {
        return this.turtle.executeCommand(new TurtleRestockCommand(InteractDirection.UP));
    }
    @LuaFunction
    public final MethodResult restockDown() {
        return this.turtle.executeCommand(new TurtleRestockCommand(InteractDirection.DOWN));
    }


    @LuaFunction
    public final MethodResult changePrice() {
        return this.turtle.executeCommand(new TurtlePriceChangeCommand(InteractDirection.FORWARD));
    }
    @LuaFunction
    public final MethodResult changePriceUp() {
        return this.turtle.executeCommand(new TurtlePriceChangeCommand(InteractDirection.UP));
    }
    @LuaFunction
    public final MethodResult changePriceDown() {
        return this.turtle.executeCommand(new TurtlePriceChangeCommand(InteractDirection.DOWN));
    }


    @LuaFunction
    public final MethodResult profit() {
        return this.turtle.executeCommand(new TurtleProfitCommand(InteractDirection.FORWARD));
    }
    @LuaFunction
    public final MethodResult profitUp() {
        return this.turtle.executeCommand(new TurtleProfitCommand(InteractDirection.UP));
    }
    @LuaFunction
    public final MethodResult profitDown() {
        return this.turtle.executeCommand(new TurtleProfitCommand(InteractDirection.DOWN));
    }


    @LuaFunction
    public final MethodResult recall() {
        return this.turtle.executeCommand(new TurtleRecallCommand(InteractDirection.FORWARD));
    }
    @LuaFunction
    public final MethodResult recallUp() {
        return this.turtle.executeCommand(new TurtleRecallCommand(InteractDirection.UP));
    }
    @LuaFunction
    public final MethodResult recallDown() {
        return this.turtle.executeCommand(new TurtleRecallCommand(InteractDirection.DOWN));
    }


    public static class TurtleBuyCommand implements ITurtleCommand {
        private final InteractDirection direction;

        public TurtleBuyCommand(InteractDirection direction) {
            this.direction = direction;
        }

        @NotNull
        @Override
        public TurtleCommandResult execute(@NotNull ITurtleAccess iTurtleAccess) {
            SimpleShopTileEntity shop = getShopInDirection(iTurtleAccess, direction);
            return shop == null ? TurtleCommandResult.failure("Not a Shop") :
                    ((IShopAccess) shop).ccshops$tryBuyWithTurtle(iTurtleAccess, iTurtleAccess.getInventory().getItem(iTurtleAccess.getSelectedSlot()));
        }
    }

    public static class TurtleRestockCommand implements ITurtleCommand {
        private final InteractDirection direction;

        public TurtleRestockCommand(InteractDirection direction) {
            this.direction = direction;
        }

        @Override
        public @NotNull TurtleCommandResult execute(@NotNull ITurtleAccess iTurtleAccess) {
            SimpleShopTileEntity shop = getShopInDirection(iTurtleAccess, direction);
            IShopAccess access = (IShopAccess) shop;

            if (shop == null)
                return TurtleCommandResult.failure("Not a Shop!");
            if (!access.ccshops$getOwner().equals(iTurtleAccess.getOwningPlayer().getId()))
                return TurtleCommandResult.failure("Not your Shop!");

            return access.ccshops$restock(iTurtleAccess, iTurtleAccess.getSelectedSlot());
        }
    }

    public static class TurtlePriceChangeCommand implements ITurtleCommand {
        private final InteractDirection direction;

        public TurtlePriceChangeCommand(InteractDirection direction) {
            this.direction = direction;
        }

        @Override
        public @NotNull TurtleCommandResult execute(@NotNull ITurtleAccess iTurtleAccess) {
            SimpleShopTileEntity shop = getShopInDirection(iTurtleAccess, direction);
            IShopAccess access = (IShopAccess) shop;

            if (shop == null)
                return TurtleCommandResult.failure("Not a Shop!");
            if (!access.ccshops$getOwner().equals(iTurtleAccess.getOwningPlayer().getId()))
                return TurtleCommandResult.failure("Not your Shop!");
            if (access.ccshops$hasProfits())
                return TurtleCommandResult.failure("Has Unclaimed Profits!");

            shop.setCost(iTurtleAccess.getInventory().getItem(iTurtleAccess.getSelectedSlot()).copy());
            return TurtleCommandResult.success();
        }
    }

    public static class TurtleProfitCommand implements ITurtleCommand {
        private final InteractDirection direction;

        public TurtleProfitCommand(InteractDirection direction) {
            this.direction = direction;
        }

        @Override
        public @NotNull TurtleCommandResult execute(@NotNull ITurtleAccess iTurtleAccess) {
            SimpleShopTileEntity shop = getShopInDirection(iTurtleAccess, direction);
            IShopAccess access = (IShopAccess) shop;

            if (shop == null)
                return TurtleCommandResult.failure("Not a Shop!");
            if (!access.ccshops$getOwner().equals(iTurtleAccess.getOwningPlayer().getId()))
                return TurtleCommandResult.failure("Not your Shop!");
            if (!access.ccshops$hasProfits())
                return TurtleCommandResult.failure("Has No Profits!");

            return access.ccshops$dropGains(iTurtleAccess);
        }
    }

    public static class TurtleRecallCommand implements ITurtleCommand {
        private final InteractDirection direction;

        public TurtleRecallCommand(InteractDirection direction) {
            this.direction = direction;
        }

        @Override
        public @NotNull TurtleCommandResult execute(@NotNull ITurtleAccess iTurtleAccess) {
            SimpleShopTileEntity shop = getShopInDirection(iTurtleAccess, direction);
            IShopAccess access = (IShopAccess) shop;

            if (shop == null)
                return TurtleCommandResult.failure("Not a Shop!");
            if (!access.ccshops$getOwner().equals(iTurtleAccess.getOwningPlayer().getId()))
                return TurtleCommandResult.failure("Not your Shop!");
            if (!access.ccshops$hasStock())
                return TurtleCommandResult.failure("Has No Stock!");

            return access.ccshops$dropInv(iTurtleAccess);
        }
    }

    public static SimpleShopTileEntity getShopInDirection(ITurtleAccess turtle, InteractDirection direction) {
        Level level = turtle.getLevel();
        BlockPos pos = turtle.getPosition().relative(direction.toWorldDir(turtle));
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SimpleShopTileEntity shop)
            return shop;
        return null;
    }
}
