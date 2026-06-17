package mctmods.immersivetechnology.common.blocks.metal.logic;

import blusunrize.immersiveengineering.api.fluid.IFluidPipe;
import blusunrize.immersiveengineering.common.blocks.metal.FluidPipeBlockEntity;
import blusunrize.immersiveengineering.common.util.Utils;
import java.text.DecimalFormat;
import mctmods.immersivetechnology.common.blocks.helper.ITBlockInterfaces;
import mctmods.immersivetechnology.core.network.ITOSDRequestMessage;
import mctmods.immersivetechnology.core.network.ITPacketHandler;
import mctmods.immersivetechnology.core.util.ITFluidStacks;
import mctmods.immersivetechnology.core.util.TranslationKey;
import mctmods.immersivetechnology.core.ITClientConfig;
import mctmods.immersivetechnology.core.ITServerConfig;
import mctmods.immersivetechnology.core.registration.ITBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

public class BarrelCreativeBlockEntity extends OSDCommonBlockEntity implements ITBlockInterfaces.IBlockEntityDrop, ITBlockInterfaces.IPlayerInteraction, ITBlockInterfaces.IBlockOverlayText {
    private FluidStack selectedFluid = FluidStack.EMPTY;

    private static final int CREATIVE_BARREL_OUTPUT_AMOUNT = ITServerConfig.creativeBarrelOutputAmount;

    private final IFluidHandler fluidHandler = new IFluidHandler() {
        @Override public int getTanks() { return 1; }

        @Override @NotNull public FluidStack getFluidInTank(int tank) {
            if (selectedFluid.isEmpty()) { return FluidStack.EMPTY; }
            return selectedFluid.copyWithAmount(Integer.MAX_VALUE);
        }

        @Override public int getTankCapacity(int tank) { return Integer.MAX_VALUE; }

        @Override public boolean isFluidValid(int tank, @NotNull FluidStack stack) { return false; }

        @Override public int fill(FluidStack resource, FluidAction action) { return 0; }

        @Override @NotNull public FluidStack drain(FluidStack resource, FluidAction action) {
            if (selectedFluid.isEmpty() || !FluidStack.isSameFluidSameComponents(selectedFluid, resource)) { return FluidStack.EMPTY; }
            return selectedFluid.copyWithAmount(resource.getAmount());
        }

        @Override public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            if (selectedFluid.isEmpty()) { return FluidStack.EMPTY; }
            return selectedFluid.copyWithAmount(maxDrain);
        }
    };

    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,##0.###");

    public BarrelCreativeBlockEntity(BlockPos pos, BlockState state) { super(ITBlockEntities.BARREL_CREATIVE.get(), pos, state); }

    @Override public void tickServer() {
        if (!selectedFluid.isEmpty()) {
            long thisTickOutput = 0;
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = worldPosition.relative(dir);
                assert level != null;
                BlockEntity neighbor = level.getBlockEntity(neighborPos);
                boolean isPipe = neighbor instanceof FluidPipeBlockEntity;
                FluidStack fs = selectedFluid.copy();
                fs.setAmount(CREATIVE_BARREL_OUTPUT_AMOUNT);
                boolean hadTag = ITFluidStacks.contains(fs, IFluidPipe.NBT_PRESSURIZED);
                if (isPipe && !hadTag) { ITFluidStacks.putBoolean(fs, IFluidPipe.NBT_PRESSURIZED, true); }
                var cap = FluidUtil.getFluidHandler(level, neighborPos, dir.getOpposite());
                if (cap.isEmpty()) { continue; }
                IFluidHandler handler = cap.orElseThrow(AssertionError::new);
                int accepted = handler.fill(fs, FluidAction.SIMULATE);
                if (!hadTag) { ITFluidStacks.remove(fs, IFluidPipe.NBT_PRESSURIZED); }
                if (accepted <= 0) { continue; }
                FluidStack toFill = Utils.copyFluidStackWithAmount(fs, accepted, false);
                if (isPipe) { ITFluidStacks.putBoolean(toFill, IFluidPipe.NBT_PRESSURIZED, true); }
                int filled = handler.fill(toFill, FluidAction.EXECUTE);
                thisTickOutput += filled;
            }
            acceptedAmount += thisTickOutput;
        }
        super.tickServer();
    }

    @Override public void readCustomNBT(@NotNull CompoundTag nbt, boolean descPacket) {
        if (level != null) {
            readCustomNBT(nbt, descPacket, level.registryAccess());
            return;
        }
        super.readCustomNBT(nbt, descPacket);
    }

    @Override public void readCustomNBT(@NotNull CompoundTag nbt, boolean descPacket, HolderLookup.Provider provider) {
        super.readCustomNBT(nbt, descPacket, provider);
        if (nbt.contains("SelectedFluid")) {
            selectedFluid = FluidStack.parseOptional(provider, nbt.getCompound("SelectedFluid"));
        }
    }

    @Override public void writeCustomNBT(@NotNull CompoundTag nbt, boolean descPacket) {
        if (level != null) {
            writeCustomNBT(nbt, descPacket, level.registryAccess());
            return;
        }
        super.writeCustomNBT(nbt, descPacket);
    }

    @Override public void writeCustomNBT(@NotNull CompoundTag nbt, boolean descPacket, HolderLookup.Provider provider) {
        super.writeCustomNBT(nbt, descPacket, provider);
        if (!selectedFluid.isEmpty()) { nbt.put("SelectedFluid", selectedFluid.saveOptional(provider)); }
    }

    public IFluidHandler getFluidHandler(@Nullable Direction side) { return fluidHandler; }

    @Override public boolean interact(@NotNull Direction side, @NotNull Player player, @NotNull InteractionHand hand, @NotNull ItemStack heldItem, float hitX, float hitY, float hitZ) {
        FluidStack contained = FluidUtil.getFluidContained(heldItem).orElse(FluidStack.EMPTY);
        if (!contained.isEmpty()) {
            setOutputFluid(contained);
            if (level != null && !level.isClientSide) {
                SoundEvent sound = contained.getFluid().getFluidType().getSound(player, level, worldPosition, SoundActions.BUCKET_EMPTY);
                if (sound == null) { sound = contained.getFluid().is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY; }
                level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        } else if (player.isShiftKeyDown()) {
            setOutputFluid(FluidStack.EMPTY);
            return true;
        }
        return FluidUtil.interactWithFluidHandler(player, hand, fluidHandler);
    }

    @Override public TranslationKey text() { return TranslationKey.OVERLAY_OSD_BARREL_NORMAL_FIRST_LINE; }

    @Override public Component[] getOverlayText(@NotNull Player player, @NotNull HitResult rtr, boolean hammer) {
        if (rtr.getType() == HitResult.Type.MISS) { return null; }
        assert level != null;
        if (level.isClientSide && requestCooldown == 0) {
            ITPacketHandler.sendToServer(new ITOSDRequestMessage(worldPosition));
            requestCooldown = 20;
        }
        if (selectedFluid.isEmpty()) { return new Component[]{Component.translatable(TranslationKey.GUI_EMPTY.text())}; }
        Component fluidName = selectedFluid.getHoverName();
        double rawValue = ITClientConfig.perTickTrashCans ? (double)lastAcceptedAmount / 20.0 : lastAcceptedAmount;
        String value = NUMBER_FORMAT.format(rawValue);
        return new Component[]{Component.translatable(text().text(), fluidName, value)};
    }

    @Override public void getBlockEntityDrop(@NotNull LootContext context, @NotNull Consumer<ItemStack> drop) {
        ItemStack stack = new ItemStack(getBlockState().getBlock(), 1);
        CompoundTag tag = new CompoundTag();
        writeCustomNBT(tag, false, context.getLevel().registryAccess());
        if (!tag.isEmpty()) { stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag)); }
        drop.accept(stack);
    }

    @Override public void onBEPlaced(BlockPlaceContext ctx) { onBEPlaced(ctx.getItemInHand()); }

    public void setOutputFluid(FluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            this.selectedFluid = FluidStack.EMPTY;
        } else {
            this.selectedFluid = fluidStack.copy();
            this.selectedFluid.setAmount(1);
        }
        setChanged();
    }

    public void onBEPlaced(ItemStack stack) {
        if (level != null) {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if (tag.contains("SelectedFluid")) { selectedFluid = FluidStack.parseOptional(level.registryAccess(), tag.getCompound("SelectedFluid")); }
        }
    }

}
