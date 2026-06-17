package mctmods.immersivetechnology.common.blocks.metal.logic;

import mctmods.immersivetechnology.core.util.capability.CapabilityReference;
import blusunrize.immersiveengineering.common.util.Utils;
import com.google.common.collect.ImmutableMap;
import mctmods.immersivetechnology.common.blocks.helper.ITBaseBlockEntity;
import mctmods.immersivetechnology.common.blocks.helper.ITBlockInterfaces;
import mctmods.immersivetechnology.common.blocks.helper.ITEnums.IOSideConfig;
import mctmods.immersivetechnology.common.blocks.helper.ITClientTickableBE;
import mctmods.immersivetechnology.common.blocks.helper.ITServerTickableBE;
import mctmods.immersivetechnology.common.fluids.helper.ITMarkableFluidTank;
import mctmods.immersivetechnology.core.util.TranslationKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class BarrelCommonBlockEntity extends ITBaseBlockEntity implements ITServerTickableBE, ITClientTickableBE, ITBlockInterfaces.IBlockOverlayText, ITBlockInterfaces.IPlayerInteraction, ITBlockInterfaces.IBlockEntityDrop, ITBlockInterfaces.IComparatorOverride, ITBlockInterfaces.IPlacementInteraction, ITBlockInterfaces.IConfigurableSides {
    public final ITMarkableFluidTank tank;
    public EnumMap<Direction, IOSideConfig> sideConfig = new EnumMap<>(ImmutableMap.of(Direction.DOWN, IOSideConfig.OUTPUT, Direction.UP, IOSideConfig.INPUT));
    protected static final int transferSpeed = FluidType.BUCKET_VOLUME;
    protected final Map<Direction, CapabilityReference<IFluidHandler>> neighbors = ImmutableMap.of(Direction.DOWN, CapabilityReference.forNeighbor(this, Capabilities.FluidHandler.BLOCK, Direction.DOWN), Direction.UP, CapabilityReference.forNeighbor(this, Capabilities.FluidHandler.BLOCK, Direction.UP));
    private final IFluidHandler nonsidedHandler = new SidedFluidHandler(this, null);
    private final IFluidHandler upHandler = new SidedFluidHandler(this, Direction.UP);
    private final IFluidHandler downHandler = new SidedFluidHandler(this, Direction.DOWN);

    public BarrelCommonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int tankSize) {
        super(type, pos, state);
        this.tank = new ITMarkableFluidTank(tankSize, v -> setChanged());
    }

    @Override public void tickClient() { }

    @Override public abstract void tickServer();

    private void readSideConfig(CompoundTag nbt) {
        sideConfig.clear();
        int[] sideCfgArray = nbt.getIntArray("sideConfig");
        if (sideCfgArray.length >= 2) {
            sideConfig.put(Direction.DOWN, IOSideConfig.VALUES[sideCfgArray[0]]);
            sideConfig.put(Direction.UP, IOSideConfig.VALUES[sideCfgArray[1]]);
        } else {
            sideConfig.put(Direction.DOWN, IOSideConfig.OUTPUT);
            sideConfig.put(Direction.UP, IOSideConfig.INPUT);
        }
    }

    private void writeSideConfig(CompoundTag nbt) {
        int[] sideCfgArray = new int[2];
        sideCfgArray[0] = sideConfig.getOrDefault(Direction.DOWN, IOSideConfig.OUTPUT).ordinal();
        sideCfgArray[1] = sideConfig.getOrDefault(Direction.UP, IOSideConfig.INPUT).ordinal();
        nbt.putIntArray("sideConfig", sideCfgArray);
    }

    @Override public void readCustomNBT(@NotNull CompoundTag nbt, boolean descPacket) {
        if (level != null) {
            readCustomNBT(nbt, descPacket, level.registryAccess());
            return;
        }
        readSideConfig(nbt);
        postRead(descPacket);
    }

    @Override public void readCustomNBT(@NotNull CompoundTag nbt, boolean descPacket, HolderLookup.Provider provider) {
        readSideConfig(nbt);
        tank.readFromNBT(provider, nbt.getCompound("tank"));
        postRead(descPacket);
    }

    protected void postRead(boolean descPacket) { if (!descPacket) updateState(); }

    @Override public void writeCustomNBT(@NotNull CompoundTag nbt, boolean descPacket) {
        if (level != null) {
            writeCustomNBT(nbt, descPacket, level.registryAccess());
            return;
        }
        writeSideConfig(nbt);
    }

    @Override public void writeCustomNBT(@NotNull CompoundTag nbt, boolean descPacket, HolderLookup.Provider provider) {
        writeSideConfig(nbt);
        nbt.put("tank", tank.writeToNBT(provider, new CompoundTag()));
    }

    public IFluidHandler getFluidHandler(@Nullable Direction facing) {
        if (facing == null) return nonsidedHandler;
        if (facing.getAxis() != Direction.Axis.Y) return null;
        return facing == Direction.UP ? upHandler : downHandler;
    }

    @Override public boolean interact(@NotNull Direction side, @NotNull Player player, @NotNull InteractionHand hand, @NotNull ItemStack heldItem, float hitX, float hitY, float hitZ) {
        FluidStack contained = FluidUtil.getFluidContained(heldItem).orElse(FluidStack.EMPTY);
        if (!contained.isEmpty() && !isFluidValid(contained)) {
            if (level != null && !level.isClientSide) player.displayClientMessage(Component.translatable(TranslationKey.NO_GAS_ALLOWED.text()), false);
            return true;
        }
        if (FluidUtil.interactWithFluidHandler(player, hand, tank)) { setChanged(); markContainingBlockForUpdate(null); return true; }
        return false;
    }

    @Override public Component[] getOverlayText(@NotNull Player player, @NotNull HitResult rtr, boolean hammer) {
        if (rtr.getType() == HitResult.Type.MISS) return null;
        if (Utils.isFluidRelatedItemStack(player.getItemInHand(InteractionHand.MAIN_HAND))) {
            FluidStack fs = tank.getFluid();
            if (fs.isEmpty()) return new Component[]{Component.translatable(TranslationKey.GUI_EMPTY.text())};
            return new Component[]{Component.literal(TranslationKey.OVERLAY_OSD_BARREL_NORMAL_FIRST_LINE.format(fs.getHoverName().getString(), fs.getAmount()))};
        }
        return new Component[0];
    }

    @Override public int getComparatorInputOverride() { return (15 * tank.getFluidAmount()) / tank.getCapacity(); }

    @Override public void getBlockEntityDrop(@NotNull LootContext context, @NotNull Consumer<ItemStack> drop) {
        ItemStack stack = new ItemStack(getBlockState().getBlock(), 1);
        CompoundTag tag = new CompoundTag();
        writeTank(context.getLevel().registryAccess(), tag, true);
        if (!tag.isEmpty()) stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        drop.accept(stack);
    }

    @Override public void onBEPlaced(BlockPlaceContext ctx) {
        CompoundTag tag = ctx.getItemInHand().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.isEmpty()) readTank(ctx.getLevel().registryAccess(), tag);
    }

    public void writeTank(CompoundTag nbt, boolean toItem) {
        if (level == null) return;
        writeTank(level.registryAccess(), nbt, toItem);
    }

    public void writeTank(HolderLookup.Provider provider, CompoundTag nbt, boolean toItem) {
        boolean write = tank.getFluidAmount() > 0;
        CompoundTag tankTag = tank.writeToNBT(provider, new CompoundTag());
        if (!toItem || write) nbt.put("tank", tankTag);
    }

    public void readTank(CompoundTag nbt) {
        if (level != null) readTank(level.registryAccess(), nbt);
    }

    public void readTank(HolderLookup.Provider provider, CompoundTag nbt) { tank.readFromNBT(provider, nbt.getCompound("tank")); }

    protected abstract boolean isFluidValid(@NotNull FluidStack fluid);

    @Override @NotNull public IOSideConfig getSideConfig(@NotNull Direction side) { return sideConfig.getOrDefault(side, IOSideConfig.NONE); }

    @Override public boolean toggleSide(Direction side, @NotNull Player p) {
        if (side.getAxis() != Direction.Axis.Y) return false;
        if (!canConfigureSide(side)) return false;
        IOSideConfig next = IOSideConfig.next(sideConfig.getOrDefault(side, IOSideConfig.NONE));
        sideConfig.put(side, next);
        setChanged();
        updateState();
        markContainingBlockForUpdate(null);
        return true;
    }

    protected abstract boolean canConfigureSide(Direction side);

    protected abstract void updateState();

    public static class SidedFluidHandler implements IFluidHandler {
        BarrelCommonBlockEntity barrel;
        @Nullable Direction facing;

        public SidedFluidHandler(BarrelCommonBlockEntity barrel, @Nullable Direction facing) { this.barrel = barrel; this.facing = facing; }

        @Override public int getTanks() { return barrel.tank.getTanks(); }

        @Override @NotNull public FluidStack getFluidInTank(int tank) { return barrel.tank.getFluidInTank(tank); }

        @Override public int getTankCapacity(int tank) { return barrel.tank.getTankCapacity(tank); }

        @Override public boolean isFluidValid(int tank, @NotNull FluidStack stack) { return barrel.isFluidValid(stack); }

        @Override public int fill(FluidStack resource, FluidAction action) { if (resource.isEmpty() || (facing != null && barrel.sideConfig.get(facing) != IOSideConfig.INPUT)) return 0; return barrel.tank.fill(resource, action); }

        @Override @NotNull public FluidStack drain(FluidStack resource, FluidAction action) { if (resource.isEmpty() || (facing != null && barrel.sideConfig.get(facing) != IOSideConfig.OUTPUT)) return FluidStack.EMPTY; return barrel.tank.drain(resource, action); }

        @Override @NotNull public FluidStack drain(int maxDrain, FluidAction action) { if (facing != null && barrel.sideConfig.get(facing) != IOSideConfig.OUTPUT) return FluidStack.EMPTY; return barrel.tank.drain(maxDrain, action); }
    }
}
