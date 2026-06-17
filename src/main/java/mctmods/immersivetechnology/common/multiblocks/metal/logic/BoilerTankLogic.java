package mctmods.immersivetechnology.common.multiblocks.metal.logic;

import blusunrize.immersiveengineering.api.fluid.FluidUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent.CapabilityRegistrar;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.*;
import mctmods.immersivetechnology.core.util.capability.CapabilityReference;
import com.google.common.collect.ImmutableList;
import mctmods.immersivetechnology.api.convergence.HeatCapabilities;
import mctmods.immersivetechnology.api.convergence.capability.IHeatConsumer;
import mctmods.immersivetechnology.api.convergence.capability.IHeatProvider;
import mctmods.immersivetechnology.common.multiblocks.helper.ITDisplayContext;
import mctmods.immersivetechnology.common.multiblocks.helper.ITMultiBlockInventoryUtils;
import mctmods.immersivetechnology.common.multiblocks.helper.ITPressurizedFluidOutput;
import mctmods.immersivetechnology.common.multiblocks.helper.ITSlotwiseItemHandler;
import mctmods.immersivetechnology.common.multiblocks.metal.recipe.BoilerTankRecipe;
import mctmods.immersivetechnology.common.multiblocks.metal.shapes.BoilerTankShape;
import mctmods.immersivetechnology.common.fluids.helper.ITArrayFluidHandler;
import mctmods.immersivetechnology.common.fluids.helper.ITMarkableFluidTank;
import mctmods.immersivetechnology.core.ITCommonConfig;
import mctmods.immersivetechnology.core.util.multiblock.PoIJSONSchema;
import mctmods.immersivetechnology.core.ITServerConfig;
import mctmods.immersivetechnology.core.util.capability.StoredCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class BoilerTankLogic implements IMultiblockLogic<BoilerTankLogic.State>, IServerTickableComponent<BoilerTankLogic.State>, ITPressurizedFluidOutput<BoilerTankLogic.State> {
    public static final int INPUT_SLOT_FILLED = 0;
    public static final int INPUT_SLOT_EMPTY = 1;
    public static final int OUTPUT_SLOT_EMPTY = 2;
    public static final int OUTPUT_SLOT_FILLED = 3;

    public static final int TANK_CAPACITY = ITServerConfig.boilerTankCapacity;
    public static final int PROGRESS_LOSS_PER_TICK = ITServerConfig.boilerTankProgressLossPerTick;
    public static final double DEFAULT_WORKING_HEAT_LEVEL = ITCommonConfig.boilerDefaultWorkingHeat;

    private static final List<PoIJSONSchema> RAW_POIS = ImmutableList.copyOf(BoilerTankShape.DATA.pointsOfInterest);

    public static final List<BlockPos> FLUID_INPUT_POI = getPosList("fluid_input");
    private static final List<BlockPos> FLUID_OUTPUT_POI = getPosList("fluid_output");
    private static final List<BlockPos> HEAT_INPUT_POI = getPosList("heat_input");
    private static final RelativeBlockFace FLUID_INPUT_FACING = getFacing("fluid_input");
    private static final RelativeBlockFace FLUID_OUTPUT_FACING = getFacing("fluid_output");
    private static final RelativeBlockFace HEAT_INPUT_FACING = getFacing("heat_input");

    private static List<BlockPos> getPosList(String name) { return RAW_POIS.stream().filter(poi -> poi.name.equals(name)).map(poi -> new BlockPos(poi.pos[0], poi.pos[1], poi.pos[2])).collect(ImmutableList.toImmutableList()); }

    private static RelativeBlockFace getFacing(String name) {
        List<RelativeBlockFace> facings = RAW_POIS.stream().filter(poi -> poi.name.equals(name)).flatMap(poi -> poi.relativeFaces.stream()).distinct().toList();
        if (facings.size() != 1) { throw new RuntimeException("Inconsistent facings for POI: " + name); }
        return facings.get(0);
    }

    @Override public List<BlockPos> getOutputPositions() { return FLUID_OUTPUT_POI; }

    @Override public Direction getOutputDirection(IMultiblockContext<State> ctx) { return ctx.getLevel().toAbsolute(FLUID_OUTPUT_FACING); }

    @Override public List<ITMarkableFluidTank> getOutputTanks(State state) { return ImmutableList.of(state.tanks.output); }

    @Override public List<CapabilityReference<IFluidHandler>> getFluidOutputs(State state) { return ImmutableList.of(state.fluidOutput); }

    @Override public void tickServer(IMultiblockContext<State> ctx) {
        final State state = ctx.getState();
        final Level level = ctx.getLevel().getRawLevel();
        boolean update = false;
        double heatLevel = 0;
        if (state.heatSource.isPresent()) { heatLevel = state.heatSource.get().getHeatLevel(); }
        double previousHeatLevel = state.heatLevel;
        state.heatLevel = heatLevel;
        boolean isActive = heatLevel >= state.getWorkingHeatLevel() && state.recipeTimeRemaining > 0;
        if (state.active != isActive) { state.active = isActive; update = true; }
        if (previousHeatLevel != state.heatLevel) { update = true; }
        double required = state.getWorkingHeatLevel();
        if (heatLevel >= required) {
            if (state.recipeTimeRemaining > 0) {
                if (state.lastRecipe == null) { state.recipeTimeRemaining = 0; update = true; }
                else {
                    state.recipeTimeRemaining--;
                    if (state.recipeTimeRemaining == 0) {
                        state.tanks.output.fill(state.lastRecipe.output.copy(), FluidAction.EXECUTE);
                        state.totalProcessTime = 0;
                        update = true;
                    }
                }
            } else if (state.tanks.input.getFluidAmount() > 0) {
                state.lastRecipe = BoilerTankRecipe.findRecipe(level, state.tanks.input.getFluid());
                if (state.lastRecipe != null && state.lastRecipe.input.amount() <= state.tanks.input.getFluidAmount() && state.lastRecipe.output.getAmount() <= state.tanks.output.getCapacity() - state.tanks.output.getFluidAmount()) {
                    if (heatLevel >= state.lastRecipe.requiredHeat) {
                        int reqAmount = state.lastRecipe.input.amount();
                        FluidStack drained = state.tanks.input.drain(reqAmount, FluidAction.EXECUTE);
                        if (drained.getAmount() == reqAmount && state.lastRecipe.input.ingredient().test(drained)) {
                            state.recipeTimeRemaining = state.lastRecipe.getTotalProcessTime();
                            state.totalProcessTime = state.lastRecipe.getTotalProcessTime();
                            state.recipeTimeRemaining--;
                            if (state.recipeTimeRemaining == 0) {
                                state.tanks.output.fill(state.lastRecipe.output.copy(), FluidAction.EXECUTE);
                                state.totalProcessTime = 0;
                            }
                            update = true;
                        }
                    }
                }
            }
        } else if (state.recipeTimeRemaining > 0) {
            int previousProgress = state.recipeTimeRemaining;
            if (state.lastRecipe == null) { state.recipeTimeRemaining = 0; update = true; }
            else {
                state.recipeTimeRemaining = Math.min(state.recipeTimeRemaining + PROGRESS_LOSS_PER_TICK, state.lastRecipe.getTotalProcessTime());
                if (previousProgress != state.recipeTimeRemaining) { update = true; }
            }
        }
        if (state.tanks.output.getFluidAmount() > 0) {
            if (FluidUtils.fillFluidContainer(state.tanks.output, OUTPUT_SLOT_EMPTY, OUTPUT_SLOT_FILLED, state.inventory)) { update = true; }
        }
        pumpOutputs(ctx);
        if (tryEmptyContainer(state.tanks.input, state.inventory)) { update = true; }
        if (update) { ctx.markMasterDirty(); ctx.requestMasterBESync(); }
    }

    private boolean tryEmptyContainer(IFluidHandler tank, IItemHandlerModifiable inv) {
        ItemStack filledContainer = inv.getStackInSlot(BoilerTankLogic.INPUT_SLOT_FILLED);
        if (filledContainer.isEmpty()) { return false; }
        FluidActionResult simResult = FluidUtils.tryEmptyContainer(filledContainer, tank, FluidType.BUCKET_VOLUME, FluidAction.SIMULATE);
        if (!simResult.isSuccess()) { return false; }
        ItemStack emptyContainer = simResult.getResult();
        ItemStack outputStack = inv.getStackInSlot(BoilerTankLogic.INPUT_SLOT_EMPTY);
        if (!outputStack.isEmpty() && !ItemStack.isSameItemSameComponents(outputStack, emptyContainer)) { return false; }
        if (outputStack.getCount() + emptyContainer.getCount() > emptyContainer.getMaxStackSize()) { return false; }
        FluidActionResult execResult = FluidUtils.tryEmptyContainer(filledContainer, tank, FluidType.BUCKET_VOLUME, FluidAction.EXECUTE);
        filledContainer.shrink(1);
        inv.setStackInSlot(BoilerTankLogic.INPUT_SLOT_FILLED, filledContainer);
        if (outputStack.isEmpty()) { inv.setStackInSlot(BoilerTankLogic.INPUT_SLOT_EMPTY, execResult.getResult()); }
        else { outputStack.grow(execResult.getResult().getCount()); }
        return true;
    }

    @Override public void registerCapabilities(CapabilityRegistrar<State> register) {
        register.register(Capabilities.FluidHandler.BLOCK, this::getFluidCapability);
        register.register(HeatCapabilities.HEAT_CONSUMER_CAPABILITY, this::getHeatConsumerCapability);
    }

    private IFluidHandler getFluidCapability(State state, CapabilityPosition position) {
        BlockPos localPos = position.posInMultiblock();
        RelativeBlockFace side = position.side();
        if (FLUID_INPUT_POI.contains(localPos) && (side == null || side == FLUID_INPUT_FACING)) { return state.inputCap.get(); }
        if (FLUID_OUTPUT_POI.contains(localPos) && (side == null || side == FLUID_OUTPUT_FACING)) { return state.outputCap.get(); }
        return null;
    }

    private IHeatConsumer getHeatConsumerCapability(State state, CapabilityPosition position) {
        BlockPos localPos = position.posInMultiblock();
        RelativeBlockFace side = position.side();
        if (HEAT_INPUT_POI.contains(localPos) && (side == null || side == HEAT_INPUT_FACING)) { return state.boilerInputCap.get(); }
        return null;
    }

    @Override public void dropExtraItems(State state, Consumer<ItemStack> drop) { ITMultiBlockInventoryUtils.dropItems(state.inventory, drop); }

    @Override public State createInitialState(IInitialMultiblockContext<State> ctx) { return new State(ctx); }

    @Override public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) { return BoilerTankShape.GETTER; }

    public static class State implements IMultiblockState, ITDisplayContext {
        public final BoilerTanks tanks;
        public StoredCapability<IFluidHandler> inputCap;
        public StoredCapability<IFluidHandler> outputCap;
        public StoredCapability<IHeatConsumer> boilerInputCap;
        public CapabilityReference<IFluidHandler> fluidOutput;
        public CapabilityReference<IHeatProvider> heatSource;
        public ITSlotwiseItemHandler inventory;
        public int recipeTimeRemaining = 0;
        public int totalProcessTime = 0;
        public BoilerTankRecipe lastRecipe;
        public double heatLevel = 0;
        public double workingHeatLevel = DEFAULT_WORKING_HEAT_LEVEL;
        public boolean active = false;

        public State(IInitialMultiblockContext<State> ctx) {
            final Runnable markDirty = ctx.getMarkDirtyRunnable();
            final Runnable sync = ctx.getSyncRunnable();
            final Runnable onChanged = () -> { markDirty.run(); sync.run(); };
            tanks = new BoilerTanks(v -> onChanged.run());
            inventory = new ITSlotwiseItemHandler(
                    List.of(
                            ITSlotwiseItemHandler.IOConstraint.FLUID_INPUT,
                            ITSlotwiseItemHandler.IOConstraint.OUTPUT,
                            ITSlotwiseItemHandler.IOConstraint.FLUID_INPUT,
                            ITSlotwiseItemHandler.IOConstraint.OUTPUT
                    ),
                    onChanged
            );
            inputCap = new StoredCapability<>(new ITArrayFluidHandler(tanks.input, false, true, onChanged));
            outputCap = new StoredCapability<>(new ITArrayFluidHandler(tanks.output, true, false, onChanged));
            boilerInputCap = new StoredCapability<>(new BoilerInputImpl(tanks.input));
            MultiblockFace outputMBFace = new MultiblockFace(FLUID_OUTPUT_FACING, FLUID_OUTPUT_POI.get(0));
            CapabilityPosition opposingCP = CapabilityPosition.opposing(outputMBFace);
            MultiblockFace opposingMBFace = new MultiblockFace(opposingCP.side(), opposingCP.posInMultiblock());
            fluidOutput = CapabilityReference.of(ctx.getCapabilityAt(Capabilities.FluidHandler.BLOCK, opposingMBFace));
            MultiblockFace heatMBFace = new MultiblockFace(HEAT_INPUT_FACING, HEAT_INPUT_POI.get(0));
            CapabilityPosition heatOpposingCP = CapabilityPosition.opposing(heatMBFace);
            MultiblockFace heatOpposingMBFace = new MultiblockFace(heatOpposingCP.side(), heatOpposingCP.posInMultiblock());
            heatSource = CapabilityReference.of(ctx.getCapabilityAt(HeatCapabilities.HEAT_PROVIDER_CAPABILITY, heatOpposingMBFace));
        }

        public double getWorkingHeatLevel() { return workingHeatLevel; }

        @Override public void writeSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            nbt.put("tanks", tanks.toNBT());
            nbt.putInt("recipeTimeRemaining", recipeTimeRemaining);
            nbt.putInt("totalProcessTime", totalProcessTime);
            nbt.put("inventory", inventory.serializeNBT());
        }

        @Override public void readSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            tanks.readNBT(nbt.getCompound("tanks"));
            recipeTimeRemaining = nbt.getInt("recipeTimeRemaining");
            totalProcessTime = nbt.getInt("totalProcessTime");
            inventory.deserializeNBT(nbt.getCompound("inventory"));
        }

        @Override public void writeSyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            CompoundTag display = new CompoundTag();
            writeDisplaySyncNBT(display, provider);
            nbt.put("display", display);
        }

        @Override public void readSyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            if (nbt.contains("display", Tag.TAG_COMPOUND)) { readDisplaySyncNBT(nbt.getCompound("display"), provider); }
        }

        @Override public boolean isActive() { return active; }

        @Override public IItemHandlerModifiable getInventory() { return inventory; }

        @Override public IFluidTank[] getInternalTanks() { return new IFluidTank[]{tanks.input, tanks.output}; }

        @Override public void writeDisplaySyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            nbt.putBoolean("active", active);
            nbt.putDouble("heatLevel", heatLevel);
            nbt.put("tanks", tanks.toNBT());
            nbt.putDouble("workingHeatLevel", getWorkingHeatLevel());
            nbt.putInt("recipeTimeRemaining", recipeTimeRemaining);
            nbt.putInt("totalProcessTime", totalProcessTime);
        }

        @Override public void readDisplaySyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
            active = nbt.getBoolean("active");
            heatLevel = nbt.getDouble("heatLevel");
            tanks.readNBT(nbt.getCompound("tanks"));
            recipeTimeRemaining = nbt.getInt("recipeTimeRemaining");
            totalProcessTime = nbt.getInt("totalProcessTime");
        }
    }

    @SuppressWarnings("unused")
    private record BoilerInputImpl(ITMarkableFluidTank tank) implements IHeatConsumer {
        @Override public int getFluidAmount() { return tank.getFluidAmount(); }
    }

    public record BoilerTanks(ITMarkableFluidTank input, ITMarkableFluidTank output) {
        public BoilerTanks(Consumer<Void> markDirty) {
            this(new ITMarkableFluidTank(TANK_CAPACITY, markDirty), new ITMarkableFluidTank(TANK_CAPACITY, markDirty));
        }

        public static BoilerTanks makeClient() { return new BoilerTanks(v -> {}); }

        public CompoundTag toNBT() {
            CompoundTag tag = new CompoundTag();
            tag.put("input", this.input.writeToNBT(new CompoundTag()));
            tag.put("output", this.output.writeToNBT(new CompoundTag()));
            return tag;
        }

        public void readNBT(CompoundTag tag) {
            this.input.readFromNBT(tag.getCompound("input"));
            this.output.readFromNBT(tag.getCompound("output"));
        }

        @SuppressWarnings("unused")
        public int getCapacity() { return TANK_CAPACITY; }
    }
}
