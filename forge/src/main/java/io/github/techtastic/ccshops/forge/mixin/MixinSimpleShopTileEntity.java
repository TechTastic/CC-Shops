package io.github.techtastic.ccshops.forge.mixin;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.shared.peripheral.generic.data.ItemData;
import io.github.techtastic.ccshops.forge.util.IComputerHandler;
import io.github.techtastic.ccshops.forge.util.IShopAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wolforce.simpleshops.SimpleShopTileEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Mixin(SimpleShopTileEntity.class)
public abstract class MixinSimpleShopTileEntity implements IShopAccess, IComputerHandler {
    @Unique
    private final List<IComputerAccess> ccshops$computers = new ArrayList<>();

    @Unique
    private boolean ccshops$successfulBuy = false;

    @Shadow private UUID owner;

    @Shadow private int gainsNr;

    @Shadow abstract int getStockNr();

    @Override
    public UUID ccshops$getOwner() {
        return owner;
    }

    @Override
    public int ccshops$getStockNr() {
        return getStockNr();
    }

    @Override
    public int ccshops$getGainsNr() {
        return gainsNr;
    }

    @Override
    public void ccshops$attach(IComputerAccess access) {
        ccshops$computers.add(access);
    }

    @Override
    public void ccshops$detach(IComputerAccess access) {
        ccshops$computers.remove(access);
    }

    @Override
    public void ccshops$fireEvent(String string, Object... objects) {
        ccshops$computers.forEach(computer -> computer.queueEvent(string, objects));
    }

    @Inject(method = "tryBuy",
            at = @At(
                    target = "Lwolforce/simpleshops/SimpleShopTileEntity;getOutputStack()Lnet/minecraft/world/item/ItemStack;",
                    value = "INVOKE"
            ),
            remap = false
    )
    private void ccshops$isBuySuccessful(Player player, ItemStack input, boolean isCreative, CallbackInfo ci) {
        ccshops$successfulBuy = true;
    }

    @Inject(method = "tryBuy", at = @At("TAIL"), remap = false)
    private void ccshops$fireEventUponBuying(Player player, ItemStack input, boolean isCreative, CallbackInfo ci) {
        HashMap<String, Object> item = new HashMap<>();
        ItemData.fillBasic(item, input);

        ccshops$fireEvent(
                "buy_attempt",
                player.getGameProfile().getName(),
                item,
                isCreative,
                ccshops$successfulBuy
        );
        ccshops$successfulBuy = false;
    }
}
