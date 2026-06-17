package mctmods.immersivetechnology.core.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.fluids.FluidStack;

public final class ITFluidStacks {
    private ITFluidStacks() {}

    public static CompoundTag getTag(FluidStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    public static boolean contains(FluidStack stack, String key) {
        return getTag(stack).contains(key);
    }

    public static void putBoolean(FluidStack stack, String key, boolean value) {
        CompoundTag tag = getTag(stack);
        tag.putBoolean(key, value);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static void remove(FluidStack stack, String key) {
        CompoundTag tag = getTag(stack);
        tag.remove(key);
        if (tag.isEmpty()) stack.remove(DataComponents.CUSTOM_DATA);
        else stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
