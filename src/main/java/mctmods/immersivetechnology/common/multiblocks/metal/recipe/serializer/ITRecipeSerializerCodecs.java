package mctmods.immersivetechnology.common.multiblocks.metal.recipe.serializer;

import net.neoforged.neoforge.fluids.FluidStack;

public final class ITRecipeSerializerCodecs {
    private ITRecipeSerializerCodecs() {}

    public static FluidStack emptyToNull(FluidStack stack) {
        return stack == null || stack.isEmpty() ? null : stack;
    }

    public static FluidStack nullToEmpty(FluidStack stack) {
        return stack == null ? FluidStack.EMPTY : stack;
    }
}
