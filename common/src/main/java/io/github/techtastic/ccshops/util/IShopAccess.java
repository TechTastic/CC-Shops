package io.github.techtastic.ccshops.util;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public interface IShopAccess {
    UUID ccshops$getOwner();

    void ccshops$setOwnerByUuid(UUID uuid);

    int ccshops$getStockNr();

    void ccshops$setStockNr(int amount);

    int ccshops$getGainsNr();

    void ccshops$setGainsNr(int amount);

    TurtleCommandResult ccshops$tryBuyWithTurtle(ITurtleAccess turtle, ItemStack input);
}
