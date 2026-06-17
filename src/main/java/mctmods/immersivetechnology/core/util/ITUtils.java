package mctmods.immersivetechnology.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.fluids.FluidStack;

import blusunrize.immersiveengineering.api.fluid.IFluidPipe;

public class ITUtils {
    public static void dropStackAtPos(Level world, BlockPos pos, ItemStack stack) { Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack); }

    public static FluidStack copyFluidStackWithAmount(FluidStack stack, int amount, boolean stripPressure) {
        FluidStack copy = stack.copyWithAmount(amount);
        if (stripPressure) {
            ITFluidStacks.remove(copy, IFluidPipe.NBT_PRESSURIZED);
        }
        return copy;
    }
}
