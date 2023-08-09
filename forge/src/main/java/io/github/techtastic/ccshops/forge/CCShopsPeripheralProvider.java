package io.github.techtastic.ccshops.forge;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import io.github.techtastic.ccshops.forge.peripheral.SimpleShopPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import wolforce.simpleshops.SimpleShopTileEntity;

public class CCShopsPeripheralProvider implements IPeripheralProvider {
    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        BlockEntity comp = level.getBlockEntity(blockPos.relative(direction.getOpposite()));
        BlockEntity be = level.getBlockEntity(blockPos);
        if (!(comp instanceof TileTurtle) && be instanceof SimpleShopTileEntity shop)
            return LazyOptional.of(() -> new SimpleShopPeripheral(shop));
        return LazyOptional.empty();
    }
}