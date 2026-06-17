package mctmods.immersivetechnology.common.items.helper;

import blusunrize.immersiveengineering.api.client.TextUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import mctmods.immersivetechnology.core.compat.ie.ItemNBTHelper;
import mctmods.immersivetechnology.common.blocks.helper.ITBlock;
import mctmods.immersivetechnology.common.blocks.helper.ITBaseBlock;
import mctmods.immersivetechnology.common.blocks.helper.ITProperties;
import mctmods.immersivetechnology.core.lib.ITLib;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ITBlockItem extends BlockItem {
    private static final HolderLookup.Provider BUILTIN_PROVIDER = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

    public ITBlockItem(Block b, Item.Properties props) { super(b, props); }

    public ITBlockItem(Block b) { this(b, new Item.Properties()); }

    @Override @NotNull public String getDescriptionId(@NotNull ItemStack stack) { return getBlock().getDescriptionId(); }

    @Override public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
        if (getBlock() instanceof ITBlock ieBlock && ieBlock.hasFlavour()) {
            String flavourKey = ITLib.DESC_FLAVOUR + ieBlock.getNameForFlavour();
            tooltip.add(TextUtils.applyFormat(Component.translatable(flavourKey), ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltip, advanced);
        if (ItemNBTHelper.hasKey(stack, EnergyHelper.ENERGY_KEY)) tooltip.add(TextUtils.applyFormat(Component.translatable(ITLib.DESC_INFO + "energyStored", ItemNBTHelper.getInt(stack, EnergyHelper.ENERGY_KEY)), ChatFormatting.GRAY));
        if (ItemNBTHelper.hasKey(stack, "tank")) {
            HolderLookup.Provider registries = context.registries() != null ? context.registries() : BUILTIN_PROVIDER;
            FluidStack fs = FluidStack.parseOptional(registries, ItemNBTHelper.getTagCompound(stack, "tank"));
            if (!fs.isEmpty()) tooltip.add(TextUtils.applyFormat(Component.translatable(ITLib.DESC_INFO + "fluidStored", fs.getHoverName(), fs.getAmount()), ChatFormatting.GRAY));
        }
    }

    @Override protected boolean placeBlock(@NotNull BlockPlaceContext context, @NotNull BlockState newState) {
        if (getBlock() instanceof MultiblockPartBlock) { return false; }
        Block b = newState.getBlock();
        if (b instanceof ITBaseBlock ieBlock) {
            if (!ieBlock.canIEBlockBePlaced(newState, context)) return false;
            boolean ret = super.placeBlock(context, newState);
            if (ret) ieBlock.onIEBlockPlacedBy(context, newState);
            return ret;
        } else return super.placeBlock(context, newState);
    }

    @Override protected boolean updateCustomBlockEntityTag(@NotNull BlockPos pos, @NotNull Level worldIn, @Nullable Player player, @NotNull ItemStack stack, BlockState state) {
        if (!state.hasProperty(ITProperties.MULTIBLOCKSLAVE)) return super.updateCustomBlockEntityTag(pos, worldIn, player, stack, state);
        else return false;
    }

    @Override @Nonnull public Optional<TooltipComponent> getTooltipImage(@Nonnull ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        if (tag.contains("Items")) {
            ListTag list = tag.getList("Items", 10);
            NonNullList<ItemStack> items = NonNullList.create();
            list.forEach(e -> {
                CompoundTag itemTag = (CompoundTag)e;
                ResourceLocation id = ResourceLocation.tryParse(itemTag.getString("id"));
                ItemStack s = id != null ? new ItemStack(BuiltInRegistries.ITEM.get(id), itemTag.getByte("Count")) : ItemStack.EMPTY;
                if (!s.isEmpty()) items.add(s);
            });
            return Optional.of(new BundleTooltip(new BundleContents(items)));
        }
        return super.getTooltipImage(stack);
    }

    @Override public boolean canFitInsideContainerItems() { return !(getBlock() instanceof ITBaseBlock ieBlock) || ieBlock.fitsIntoContainer(); }
}
