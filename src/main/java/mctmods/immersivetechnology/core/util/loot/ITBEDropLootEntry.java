package mctmods.immersivetechnology.core.util.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import java.util.List;

import mctmods.immersivetechnology.common.blocks.helper.ITBlockInterfaces;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ITBEDropLootEntry extends LootPoolSingletonContainer {
    public static final MapCodec<ITBEDropLootEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> singletonFields(instance).apply(instance, ITBEDropLootEntry::new));

    protected ITBEDropLootEntry(int weightIn, int qualityIn, List<LootItemCondition> conditionsIn, List<LootItemFunction> functionsIn) { super(weightIn, qualityIn, conditionsIn, functionsIn); }

    protected void createItemStack(@Nonnull Consumer<ItemStack> output, LootContext context) {
        if (context.hasParam(LootContextParams.BLOCK_ENTITY)) {
            BlockEntity te = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
            if (te instanceof ITBlockInterfaces.IBlockEntityDrop) { ((ITBlockInterfaces.IBlockEntityDrop)te).getBlockEntityDrop(context, output); }
        }
    }

    public static LootPoolSingletonContainer.Builder<?> builder() { return simpleBuilder(ITBEDropLootEntry::new); }

    @Nonnull public LootPoolEntryType getType() { return ITLootFunctions.TILE_DROP.get(); }
}
