package io.github.techtastic.ccshops.forge.mixin;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.peripheral.generic.data.ItemData;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import io.github.techtastic.ccshops.forge.util.IComputerHandler;
import io.github.techtastic.ccshops.forge.util.ICreativeAccess;
import io.github.techtastic.ccshops.forge.util.IShopAccess;
import net.minecraft.core.NonNullList;
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
import wolforce.utils.stacks.UtilItemStack;

import java.util.*;
import java.util.stream.Stream;

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

    @Override
    public TurtleCommandResult ccshops$tryBuyWithTurtle(ITurtleAccess turtle, ItemStack input) {
        HashMap<String, Object> item = new HashMap<>();
        ItemData.fillBasic(item, input);

        boolean isCreative = ((ICreativeAccess) SimpleShopTileEntity.class.cast(this).getBlockState().getBlock()).ccshops$isCreative();

        if (this.getStockNr() > 0 || isCreative) {
            ItemStack cost = this.getCost();
            int resultSlot = ccshops$hasOpenSpace(turtle, this.getOutputStack());
            if (input.sameItem(cost) && input.getCount() >= cost.getCount() && resultSlot != -1) {
                ItemStack result = this.getOutputStack();
                ItemStack change = UtilItemStack.setCount(input, input.getCount() - cost.getCount());
                if (!isCreative) {
                    invNr -= result.getCount();
                }

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

                ccshops$fireEvent("buy_attempt",
                        ((TileTurtle) Objects.requireNonNull(turtle.getLevel().getBlockEntity(turtle.getPosition()))).getLabel(),
                        item, isCreative, true);

                return TurtleCommandResult.success();
            }

            ccshops$fireEvent("buy_attempt",
                    ((TileTurtle) Objects.requireNonNull(turtle.getLevel().getBlockEntity(turtle.getPosition()))).getLabel(),
                    item, isCreative, false);

            if (!input.sameItem(cost))
                return TurtleCommandResult.failure("Incorrect Payment Item");
            else if (input.getCount() < cost.getCount())
                return TurtleCommandResult.failure("Insufficient Funds");
            else
                return TurtleCommandResult.failure("Not Enough Space");
        }

        ccshops$fireEvent("buy_attempt",
                ((TileTurtle) Objects.requireNonNull(turtle.getLevel().getBlockEntity(turtle.getPosition()))).getLabel(),
                item, false, false);

        return TurtleCommandResult.failure("Out of Stock");
    }

    @Unique
    private int ccshops$hasOpenSpace(ITurtleAccess turtle, ItemStack buyable) {
        Container inv = turtle.getInventory();
        NonNullList<ItemStack> trueInv = NonNullList.createWithCapacity(inv.getContainerSize());
        for (int i = 0; i < inv.getContainerSize() - 1; i++) {
            trueInv.add(i, inv.getItem(i));
        }

        if (inv.hasAnyOf(Set.of(ItemStack.EMPTY.getItem())))
            return trueInv.indexOf(ItemStack.EMPTY);
        else if (inv.hasAnyOf(Set.of(buyable.getItem()))) {
            Stream<ItemStack> stacks = trueInv.stream().filter(stack -> stack.is(buyable.getItem()) &&
                    stack.getCount() <= stack.getMaxStackSize() - buyable.getCount());
            return stacks.findFirst().isPresent() ? trueInv.indexOf(stacks.findFirst().get()) : -1;
        }
        else
            return -1;
    }
}
