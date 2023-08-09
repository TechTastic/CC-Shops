package io.github.techtastic.ccshops.forge.util;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public interface IShopAccess {
    UUID ccshops$getOwner();

    int ccshops$getStockNr();

    int ccshops$getGainsNr();

    TurtleCommandResult ccshops$tryBuyWithTurtle(ITurtleAccess turtle, ItemStack input);
}
