package mctmods.immersivetechnology.common.data.generators;

import mctmods.immersivetechnology.core.util.loot.ITBEDropLootEntry;
import mctmods.immersivetechnology.core.registration.ITBlocks;
import mctmods.immersivetechnology.core.registration.ITFluids;
import mctmods.immersivetechnology.core.registration.ITMultiblockProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public class ITBlockLootProvider extends BlockLootSubProvider {
    public ITBlockLootProvider(HolderLookup.Provider registries) { super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries); }

    @Override protected void generate() {
        registerEntity(ITBlocks.Metal.BARREL_CREATIVE.getRegObject());
        registerEntity(ITBlocks.Metal.BARREL_OPEN.getRegObject());
        registerEntity(ITBlocks.Metal.BARREL_STEEL.getRegObject());

        dropSelf(ITBlocks.Wooden.CRATE_CREATIVE.get());
        dropSelf(ITBlocks.Metal.HEAT_CREATIVE.get());
        dropSelf(ITBlocks.Metal.ROTOR_CREATIVE.get());
        dropSelf(ITBlocks.Metal.TRASH_ENERGY.get());
        dropSelf(ITBlocks.Metal.TRASH_FLUID.get());
        dropSelf(ITBlocks.Metal.TRASH_ITEM.get());
        dropSelf(ITBlocks.Metal.VALVE_FLUID.get());
        dropSelf(ITBlocks.Metal.VALVE_LIMITER.get());
        dropSelf(ITBlocks.Metal.VALVE_LOAD.get());
        dropSelf(ITBlocks.Metal.TECHNOLOGY_ENGINEERING.get());
        dropSelf(ITBlocks.Stone.REINFORCED_COKE_BRICK.get());
        dropSelf(ITBlocks.Stone.SLAB_REINFORCED_COKE_BRICK.get());

        registerMultiblocksNoDrop();

        ITFluids.ALL_ENTRIES.forEach(entry -> addNoDrop(entry.getBlock()));
    }

    private void registerEntity(DeferredHolder<Block, ? extends Block> block) {
        LootPool.Builder pool = createPoolBuilder().add(ITBEDropLootEntry.builder());
        add(block.get(), LootTable.lootTable().withPool(pool));
    }

    private void registerMultiblocksNoDrop() {
        addNoDrop(ITMultiblockProvider.ALTERNATOR.block().get());
        addNoDrop(ITMultiblockProvider.BOILER_LIQUID.block().get());
        addNoDrop(ITMultiblockProvider.BOILER_SOLID.block().get());
        addNoDrop(ITMultiblockProvider.BOILER_TANK.block().get());
        addNoDrop(ITMultiblockProvider.COOLING_TOWER.block().get());
        addNoDrop(ITMultiblockProvider.DISTILLER.block().get());
        addNoDrop(ITMultiblockProvider.GAS_TURBINE.block().get());
        addNoDrop(ITMultiblockProvider.HEAT_EXCHANGER.block().get());
        addNoDrop(ITMultiblockProvider.SOLAR_MELTER.block().get());
        addNoDrop(ITMultiblockProvider.SOLAR_REFLECTOR.block().get());
        addNoDrop(ITMultiblockProvider.SOLAR_TOWER.block().get());
        addNoDrop(ITMultiblockProvider.STEAM_TURBINE.block().get());
        addNoDrop(ITMultiblockProvider.STEEL_SHEETMETAL_TANK.block().get());
    }

    private void addNoDrop(Block block) {
        if (block.getLootTable() != BuiltInLootTables.EMPTY) {
            add(block, noDrop());
        }
    }

    private LootPool.Builder createPoolBuilder() { return LootPool.lootPool().when(ExplosionCondition.survivesExplosion()); }

    @Override @NotNull protected Set<Block> getKnownBlocks() { return ITBlocks.REGISTER.getEntries().stream().map(DeferredHolder::get).collect(Collectors.toSet()); }
}
