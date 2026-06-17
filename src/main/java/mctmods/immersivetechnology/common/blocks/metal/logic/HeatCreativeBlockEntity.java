package mctmods.immersivetechnology.common.blocks.metal.logic;

import mctmods.immersivetechnology.api.convergence.HeatCapabilities;
import mctmods.immersivetechnology.api.convergence.capability.IHeatProvider;
import mctmods.immersivetechnology.common.blocks.helper.ITBaseBlockEntity;
import mctmods.immersivetechnology.core.registration.ITBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HeatCreativeBlockEntity extends ITBaseBlockEntity {
    private final IHeatProvider provider = new Provider();

    public HeatCreativeBlockEntity(BlockPos pos, BlockState state) { super(ITBlockEntities.HEAT_CREATIVE.get(), pos, state); }

    public IHeatProvider getHeatProvider(@Nullable Direction side) { return provider; }

    private static class Provider implements IHeatProvider {
        @Override public double getHeatLevel() { return HeatCapabilities.MAX_HEAT; }
    }

    @Override public void readCustomNBT(CompoundTag nbt, boolean descPacket) {}

    @Override public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {}
}
