package io.github.techtastic.ccshops.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.generic.data.ItemData;
import io.github.techtastic.ccshops.util.IComputerHandler;
import io.github.techtastic.ccshops.util.IShopAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import wolforce.simpleshops.SimpleShopTileEntity;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class SimpleShopPeripheral implements IPeripheral {
    final SimpleShopTileEntity shop;
    final Level level;
    final BlockPos pos;

    public SimpleShopPeripheral(SimpleShopTileEntity shop) {
        this.shop = shop;
        this.level = shop.getLevel();
        this.pos = shop.getBlockPos();
    }

    @Override
    public @NotNull String getType() {
        return "shop";
    }

    @Override
    public boolean equals(IPeripheral iPeripheral) {
        return level.getBlockEntity(pos) instanceof SimpleShopTileEntity &&
                Objects.equals(level.getBlockEntity(pos), shop) &&
                iPeripheral instanceof SimpleShopPeripheral;
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        IPeripheral.super.attach(computer);
        ((IComputerHandler) shop).ccshops$attach(computer);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        IPeripheral.super.detach(computer);
        ((IComputerHandler) shop).ccshops$detach(computer);
    }

    @LuaFunction
    public HashMap<String, ?> getStockItem() {
        return getItemAsMap(shop.getOutputStack());
    }

    @LuaFunction
    public Integer getStockAmount() {
        return ((IShopAccess) shop).ccshops$getStockNr();
    }

    @LuaFunction
    public HashMap<String, ?> getCostItem() {
        return getItemAsMap(shop.getCost());
    }

    @LuaFunction
    public Integer getGainsAmount() {
        return ((IShopAccess) shop).ccshops$getGainsNr();
    }

    @LuaFunction
    public HashMap<String, ?> getBarItem() {
        return getItemAsMap(shop.getBar());
    }

    @LuaFunction
    public MethodResult getOwner() throws LuaException {
        if (level.isClientSide)
            throw new LuaException("Clientside");
        UUID uuid = ((IShopAccess) shop).ccshops$getOwner();
        Player owner = level.getPlayerByUUID(uuid);
        Entity placer = ((ServerLevel) level).getEntity(uuid);
        return MethodResult.of(owner == null ? placer == null ? null
                        : placer.getDisplayName().getString() : owner.getGameProfile().getName(), uuid.toString());
    }

    private HashMap<String, ?> getItemAsMap(ItemStack stack) {
        HashMap<String, Object> item = new HashMap<>();
        ItemData.fillBasic(item, stack);
        return item;
    }
}