package io.github.techtastic.ccshops.peripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import io.github.techtastic.ccshops.util.IShopAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import wolforce.simpleshops.SimpleShopTileEntity;
import wolforce.simpleshops.StockBarItem;

import java.util.Map;
import java.util.UUID;

public class SimpleShopCommandPeripheral extends SimpleShopPeripheral {
    public SimpleShopCommandPeripheral(SimpleShopTileEntity shop) {
        super(shop);
    }

    @LuaFunction
    public final void setStockItem(IArguments args) throws LuaException {
        this.shop.setOutputStack(getItemStackFromData(args.getTable(0)));
    }

    @LuaFunction
    public final void setStockAmount(int amount) {
        ((IShopAccess) this.shop).ccshops$setStockNr(amount);
    }

    @LuaFunction
    public final void setCostItem(IArguments args) throws LuaException {
        this.shop.setCost(getItemStackFromData(args.getTable(0)));
    }

    @LuaFunction
    public final void setGainsAmount(int amount) {
        ((IShopAccess) this.shop).ccshops$setGainsNr(amount);
    }

    @LuaFunction
    public final void setBarItem(IArguments args) throws LuaException {
        ItemStack stack = getItemStackFromData(args.getTable(0));
        if (stack.getItem() instanceof StockBarItem)
            this.shop.setBar(stack);
        throw new LuaException("Not a Bar");
    }

    @LuaFunction
    public final void setOwner(String uuid) throws LuaException {
        if (level.isClientSide)
            throw new LuaException("Clientside");

        UUID realUUID = UUID.fromString(uuid);
        Player player = this.level.getPlayerByUUID(realUUID);
        Entity entity = ((ServerLevel) this.level).getEntity(realUUID);

        if (player != null || entity != null)
            ((IShopAccess) this.shop).ccshops$setOwnerByUuid(realUUID);
        throw new LuaException("Invalid UUID");
    }

    private ItemStack getItemStackFromData(Map<?, ?> data) throws LuaException {
        if (!data.containsKey("name"))
            throw new LuaException("Invalid Item Data, needs name");

        String name = (String) data.get("name");
        Item newItem = BuiltInRegistries.ITEM.get(new ResourceLocation(name));

        if (newItem.equals(ItemStack.EMPTY.getItem()) && !name.equals("minecraft:air"))
            throw new LuaException("Invalid Item Name, use namespace:id format");

        int count = 1;
        if (data.containsKey("count"))
            count = (int) (double) data.get("count");

        ItemStack result = new ItemStack(newItem, count);

        // For other ItemStack related things in the future

        return result;
    }
}
