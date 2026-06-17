package blusunrize.immersiveengineering.common.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class ItemNBTHelper {
    private ItemNBTHelper() {}

    public static boolean hasKey(ItemStack stack, String key) {
        return getTag(stack).contains(key);
    }

    public static boolean hasKey(ItemStack stack, String key, int type) {
        return getTag(stack).contains(key, type);
    }

    public static int getInt(ItemStack stack, String key) {
        return getTag(stack).getInt(key);
    }

    public static CompoundTag getTagCompound(ItemStack stack, String key) {
        return getTag(stack).getCompound(key);
    }

    public static Tag getTag(ItemStack stack, String key) {
        return getTag(stack).get(key);
    }

    public static CompoundTag getTag(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.copyTag();
    }
}
