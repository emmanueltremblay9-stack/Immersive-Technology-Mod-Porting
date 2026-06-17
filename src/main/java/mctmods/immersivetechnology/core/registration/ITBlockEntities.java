package mctmods.immersivetechnology.core.registration;

import mctmods.immersivetechnology.api.convergence.HeatCapabilities;
import mctmods.immersivetechnology.api.convergence.MechanicalCapabilities;
import mctmods.immersivetechnology.common.blocks.metal.logic.*;
import mctmods.immersivetechnology.common.blocks.wooden.logic.CrateCreativeBlockEntity;
import mctmods.immersivetechnology.core.lib.ITLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

@SuppressWarnings("ConstantConditions")
public class ITBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ITLib.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BarrelCreativeBlockEntity>> BARREL_CREATIVE = REGISTER.register(
            "barrel_creative",
            () -> BlockEntityType.Builder.of(BarrelCreativeBlockEntity::new, ITBlocks.Metal.BARREL_CREATIVE.get()).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BarrelSteelBlockEntity>> BARREL_STEEL = REGISTER.register(
            "barrel_steel",
            () -> BlockEntityType.Builder.of(BarrelSteelBlockEntity::new, ITBlocks.Metal.BARREL_STEEL.get()).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BarrelOpenBlockEntity>> BARREL_OPEN = REGISTER.register(
            "barrel_open",
            () -> BlockEntityType.Builder.of(BarrelOpenBlockEntity::new, ITBlocks.Metal.BARREL_OPEN.get()).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrateCreativeBlockEntity>> CRATE_CREATIVE = REGISTER.register(
            "crate_creative",
            () -> BlockEntityType.Builder.of(CrateCreativeBlockEntity::new, ITBlocks.Wooden.CRATE_CREATIVE.get()).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HeatCreativeBlockEntity>> HEAT_CREATIVE = REGISTER.register(
            "heat_creative",
            () -> BlockEntityType.Builder.of(HeatCreativeBlockEntity::new, ITBlocks.Metal.HEAT_CREATIVE.get()).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RotorCreativeBlockEntity>> ROTOR_CREATIVE = REGISTER.register(
            "rotor_creative",
            () -> BlockEntityType.Builder.of(RotorCreativeBlockEntity::new, ITBlocks.Metal.ROTOR_CREATIVE.get()).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrashEnergyBlockEntity>> TRASH_ENERGY = REGISTER.register(
            "trash_energy",
            () -> BlockEntityType.Builder.of(TrashEnergyBlockEntity::new, ITBlocks.Metal.TRASH_ENERGY.get()).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrashFluidBlockEntity>> TRASH_FLUID = REGISTER.register(
            "trash_fluid",
            () -> BlockEntityType.Builder.of(TrashFluidBlockEntity::new, ITBlocks.Metal.TRASH_FLUID.get()).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrashItemBlockEntity>> TRASH_ITEM = REGISTER.register(
            "trash_item",
            () -> BlockEntityType.Builder.of(TrashItemBlockEntity::new, ITBlocks.Metal.TRASH_ITEM.get()).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ValveFluidBlockEntity>> VALVE_FLUID = REGISTER.register(
            "valve_fluid",
            () -> BlockEntityType.Builder.of(ValveFluidBlockEntity::new, ITBlocks.Metal.VALVE_FLUID.get()).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ValveLoadBlockEntity>> VALVE_LOAD = REGISTER.register(
            "valve_load",
            () -> BlockEntityType.Builder.of(ValveLoadBlockEntity::new, ITBlocks.Metal.VALVE_LOAD.get()).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ValveLimiterBlockEntity>> VALVE_LIMITER = REGISTER.register(
            "valve_limiter",
            () -> BlockEntityType.Builder.of(ValveLimiterBlockEntity::new, ITBlocks.Metal.VALVE_LIMITER.get()).build(null)
    );

    public static void init(IEventBus event) { REGISTER.register(event); }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BARREL_CREATIVE.get(), BarrelCreativeBlockEntity::getFluidHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BARREL_STEEL.get(), BarrelCommonBlockEntity::getFluidHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BARREL_OPEN.get(), BarrelCommonBlockEntity::getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CRATE_CREATIVE.get(), CrateCreativeBlockEntity::getItemHandler);
        event.registerBlockEntity(HeatCapabilities.HEAT_PROVIDER_CAPABILITY, HEAT_CREATIVE.get(), HeatCreativeBlockEntity::getHeatProvider);
        event.registerBlockEntity(MechanicalCapabilities.MECHANICAL_PROVIDER_CAPABILITY, ROTOR_CREATIVE.get(), RotorCreativeBlockEntity::getMechanicalProvider);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, TRASH_ENERGY.get(), TrashEnergyBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, TRASH_FLUID.get(), TrashFluidBlockEntity::getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TRASH_ITEM.get(), TrashItemBlockEntity::getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, VALVE_FLUID.get(), ValveFluidBlockEntity::getFluidHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, VALVE_LIMITER.get(), ValveLimiterBlockEntity::getItemHandler);
    }
}
