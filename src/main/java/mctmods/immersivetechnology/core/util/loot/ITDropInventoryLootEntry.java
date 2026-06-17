package mctmods.immersivetechnology.core.util.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import java.util.List;

import mctmods.immersivetechnology.common.blocks.helper.ITBlockInterfaces;
import mctmods.immersivetechnology.core.util.inventory.IITDropInventory;
import mctmods.immersivetechnology.core.util.inventory.ITInventoryHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class ITDropInventoryLootEntry extends LootPoolSingletonContainer {
    public static final MapCodec<ITDropInventoryLootEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> singletonFields(instance).apply(instance, ITDropInventoryLootEntry::new));

    protected ITDropInventoryLootEntry(int weightIn, int qualityIn, List<LootItemCondition> conditionsIn, List<LootItemFunction> functionsIn) { super(weightIn, qualityIn, conditionsIn, functionsIn); }

    protected void createItemStack(@Nonnull Consumer<ItemStack> output, LootContext context) {
        if (context.hasParam(LootContextParams.BLOCK_ENTITY)) {
            BlockEntity te = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
            if (te instanceof ITBlockInterfaces.IGeneralMultiblock dummyBE) {
                te = (BlockEntity)dummyBE.master();
            }
            if (te instanceof IITDropInventory itInvBE) {
                if (itInvBE.getDroppedItems() != null) { itInvBE.getDroppedItems().forEach(output); return; }
            }
            if (te != null) {
                IItemHandler itemHandler = te.getLevel() != null
                        ? te.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, te.getBlockPos(), null)
                        : null;
                if (itemHandler instanceof ITInventoryHandler itHandler) {
                    for (int i = 0; i < itHandler.getSlots(); ++i) {
                        if (!itHandler.getStackInSlot(i).isEmpty()) {
                            output.accept(itHandler.getStackInSlot(i));
                            itHandler.setStackInSlot(i, ItemStack.EMPTY);
                        }
                    }
                }
            }
        }
    }

    public static LootPoolSingletonContainer.Builder<?> builder() { return simpleBuilder(ITDropInventoryLootEntry::new); }

    @Nonnull public LootPoolEntryType getType() { return ITLootFunctions.DROP_INVENTORY.get(); }
}
