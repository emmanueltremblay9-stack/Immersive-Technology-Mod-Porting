package mctmods.immersivetechnology.common.multiblocks.helper;

import blusunrize.immersiveengineering.api.fluid.IFluidPipe;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import mctmods.immersivetechnology.core.util.capability.CapabilityReference;
import blusunrize.immersiveengineering.common.blocks.metal.FluidPipeBlockEntity;
import mctmods.immersivetechnology.common.fluids.helper.ITMarkableFluidTank;
import mctmods.immersivetechnology.core.util.ITFluidStacks;
import mctmods.immersivetechnology.core.util.ITUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.List;

public interface ITPressurizedFluidOutput<State extends IMultiblockState> {
    List<BlockPos> getOutputPositions();
    Direction getOutputDirection(IMultiblockContext<State> ctx);
    List<ITMarkableFluidTank> getOutputTanks(State state);
    List<CapabilityReference<IFluidHandler>> getFluidOutputs(State state);

    default List<RelativeBlockFace> getOutputFacings() { return null; }

    default int getTransferSpeed() { return Integer.MAX_VALUE; }

    default boolean shouldPumpOutputs(IMultiblockContext<State> ctx) { return true; }

    default boolean pumpOutputs(IMultiblockContext<State> ctx) {
        State state = ctx.getState();
        if (!shouldPumpOutputs(ctx)) return false;
        boolean dirty = false;
        Level level = ctx.getLevel().getRawLevel();
        List<BlockPos> outputPositions = getOutputPositions();
        Direction singleOutputDir = getOutputDirection(ctx);
        List<RelativeBlockFace> facings = getOutputFacings();
        List<ITMarkableFluidTank> tanks = getOutputTanks(state);
        List<CapabilityReference<IFluidHandler>> refs = getFluidOutputs(state);
        for (int i = 0; i < tanks.size(); i++) {
            ITMarkableFluidTank tank = tanks.get(i);
            if (tank.getFluidAmount() == 0) continue;
            CapabilityReference<IFluidHandler> ref = refs.get(i);
            if (!ref.isPresent()) continue;
            IFluidHandler handler = ref.get();
            BlockPos portAbs = ctx.getLevel().toAbsolute(outputPositions.get(i));
            Direction outputDir = singleOutputDir;
            if (facings != null && !facings.isEmpty()) outputDir = ctx.getLevel().toAbsolute(facings.get(i));
            assert outputDir != null;
            BlockPos externalAbs = portAbs.relative(outputDir);
            BlockEntity adjTE = level.getBlockEntity(externalAbs);
            boolean isPipe = adjTE instanceof FluidPipeBlockEntity;
            FluidStack fs = tank.getFluid().copy();
            if (fs == null) continue;
            int transferSpeed = getTransferSpeed();
            if (transferSpeed != Integer.MAX_VALUE && !isPipe) fs = ITUtils.copyFluidStackWithAmount(fs, Math.min(transferSpeed, fs.getAmount()), false);
            boolean hadTag = ITFluidStacks.contains(fs, IFluidPipe.NBT_PRESSURIZED);
            if (isPipe && !hadTag) ITFluidStacks.putBoolean(fs, IFluidPipe.NBT_PRESSURIZED, true);
            int accepted = handler.fill(fs, FluidAction.SIMULATE);
            if (!hadTag) ITFluidStacks.remove(fs, IFluidPipe.NBT_PRESSURIZED);
            if (accepted <= 0) continue;
            FluidStack toFill = ITUtils.copyFluidStackWithAmount(fs, Math.min(fs.getAmount(), accepted), false);
            if (isPipe) ITFluidStacks.putBoolean(toFill, IFluidPipe.NBT_PRESSURIZED, true);
            int drained = handler.fill(toFill, FluidAction.EXECUTE);
            tank.drain(drained, FluidAction.EXECUTE);
            dirty = true;
        }
        if (dirty) ctx.markMasterDirty();
        return dirty;
    }
}
