package io.github.techtastic.ccshops.forge;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import dan200.computercraft.shared.computer.blocks.CommandComputerBlockEntity;
import io.github.techtastic.ccshops.peripheral.SimpleShopCommandPeripheral;
import io.github.techtastic.ccshops.peripheral.SimpleShopPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import wolforce.simpleshops.SimpleShopTileEntity;

public class CCShopsForgePeripheralProvider implements IPeripheralProvider {
    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        BlockEntity be = level.getBlockEntity(blockPos);
        if (be instanceof SimpleShopTileEntity shop)
            return LazyOptional.of(() -> isCommandComputer(level, blockPos.relative(direction)) ?
                    new SimpleShopCommandPeripheral(shop) : new SimpleShopPeripheral(shop));
        return LazyOptional.empty();
    }

    private boolean isCommandComputer(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof CommandComputerBlockEntity;
    }
}