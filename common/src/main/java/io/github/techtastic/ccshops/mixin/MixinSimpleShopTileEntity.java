package io.github.techtastic.ccshops.mixin;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.peripheral.generic.data.ItemData;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import io.github.techtastic.ccshops.util.IComputerHandler;
import io.github.techtastic.ccshops.util.ICreativeAccess;
import io.github.techtastic.ccshops.util.IShopAccess;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wolforce.simpleshops.SimpleShopTileEntity;
import wolforce.simpleshops.utils.Util;

import java.util.*;

@Mixin(SimpleShopTileEntity.class)
public abstract class MixinSimpleShopTileEntity implements IShopAccess, IComputerHandler {
    @Unique
    private final List<IComputerAccess> ccshops$computers = new ArrayList<>();

    @Unique
    private boolean ccshops$successfulBuy = false;

    @Shadow private UUID owner;

    @Shadow private int gainsNr;

    @Shadow abstract int getStockNr();

    @Shadow public abstract ItemStack getCost();

    @Shadow public abstract ItemStack getOutputStack();

    @Shadow private int invNr;

    @Shadow protected abstract void sendUpdate();

    @Override
    public UUID ccshops$getOwner() {
        return owner;
    }

    @Override
    public void ccshops$setOwnerByUuid(UUID uuid) {
        this.owner = uuid;
        this.sendUpdate();
    }

    @Override
    public int ccshops$getStockNr() {
        return getStockNr();
    }

    public void ccshops$setStockNr(int invNr) {
        this.invNr = invNr;
        this.sendUpdate();
    }

    @Override
    public int ccshops$getGainsNr() {
        return gainsNr;
    }

    public void ccshops$setGainsNr(int gainsNr) {
        this.gainsNr = gainsNr;
        this.sendUpdate();
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

    @Override
    public TurtleCommandResult ccshops$tryBuyWithTurtle(ITurtleAccess turtle, ItemStack input) {
        HashMap<String, Object> item = new HashMap<>();
        ItemData.fillBasic(item, input);
        String label = ((TileTurtle) Objects.requireNonNull(turtle.getLevel().getBlockEntity(turtle.getPosition()))).getLabel();

        boolean isCreative = ((ICreativeAccess) SimpleShopTileEntity.class.cast(this).getBlockState().getBlock()).ccshops$isCreative();

        if (this.getStockNr() <= 0 && !isCreative) {
            ccshops$fireEvent("buy_attempt", label, item, false, false);
            return TurtleCommandResult.failure("Out of Stock");
        }

        ItemStack result = this.getOutputStack();
        ItemStack cost = this.getCost();
        int resultSlot = ccshops$hasOpenSpace(turtle, result);

        if (!input.sameItem(cost)) {
            ccshops$fireEvent("buy_attempt", label, item, isCreative, false);
            return TurtleCommandResult.failure("Incorrect Payment Item");
        } else if (input.getCount() < cost.getCount()) {
            ccshops$fireEvent("buy_attempt", label, item, isCreative, false);
            return TurtleCommandResult.failure("Insufficient Funds");
        } else if (resultSlot == -1) {
            ccshops$fireEvent("buy_attempt", label, item, isCreative, false);
            return TurtleCommandResult.failure("Not Enough Space");
        }

        ItemStack change = Util.setCount(input, input.getCount() - cost.getCount());
        if (!isCreative)
            invNr -= result.getCount();

        this.gainsNr += cost.getCount();

        Container inv = turtle.getInventory();
        int selected = turtle.getSelectedSlot();
        inv.setItem(selected, change);

        ItemStack current = inv.getItem(resultSlot);
        if (current.equals(ItemStack.EMPTY))
            inv.setItem(resultSlot, result);
        else {
            current.grow(result.getCount());
        }

        this.sendUpdate();

        ccshops$fireEvent("buy_attempt", label, item, isCreative, true);

        return TurtleCommandResult.success();
    }

    @Unique
    private int ccshops$hasOpenSpace(ITurtleAccess turtle, ItemStack buyable) {
        Container inv = turtle.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack test = inv.getItem(i);
            System.err.println("Slot: " + i + "\nItem: " + test);
            if (test.isEmpty() ||
                    (test.is(buyable.getItem()) &&
                            test.getCount() <= test.getMaxStackSize() - buyable.getCount()) &&
                            turtle.getSelectedSlot() != i)
                return i;
        }
        return -1;
    }
}
