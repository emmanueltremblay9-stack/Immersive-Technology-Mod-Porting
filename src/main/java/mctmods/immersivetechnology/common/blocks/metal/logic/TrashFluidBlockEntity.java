package mctmods.immersivetechnology.common.blocks.metal.logic;

import mctmods.immersivetechnology.common.blocks.metal.shape.TrashCanShape;
import mctmods.immersivetechnology.core.util.TranslationKey;
import mctmods.immersivetechnology.core.ITClientConfig;
import mctmods.immersivetechnology.core.registration.ITBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

public class TrashFluidBlockEntity extends OSDCommonBlockEntity implements IFluidHandler, TrashCanShape {
    public TrashFluidBlockEntity(BlockPos pos, BlockState state) { super(ITBlockEntities.TRASH_FLUID.get(), pos, state); }

    public IFluidHandler getFluidHandler(@Nullable Direction facing) { return this; }

    @Override public int getTanks() { return 1; }

    @Override @Nonnull public FluidStack getFluidInTank(int tank) { return FluidStack.EMPTY; }

    @Override public int getTankCapacity(int tank) { return Integer.MAX_VALUE; }

    @Override public boolean isFluidValid(int tank, @Nonnull FluidStack stack) { return true; }

    @Override public int fill(FluidStack resource, FluidAction action) {
        if (action.execute()) { acceptedAmount += resource.getAmount(); }
        return resource.getAmount();
    }

    @Override @Nonnull public FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }

    @Override @Nonnull public FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }

    @Override public TranslationKey text() { return ITClientConfig.perTickTrashCans ? TranslationKey.OVERLAY_OSD_TRASH_FLUID_NORMAL_ALTERNATIVE : TranslationKey.OVERLAY_OSD_TRASH_FLUID_NORMAL_FIRST_LINE; }
}
