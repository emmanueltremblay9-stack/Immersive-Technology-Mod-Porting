package mctmods.immersivetechnology.common.multiblocks.metal.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import mctmods.immersivetechnology.common.multiblocks.metal.logic.BoilerLiquidLogic;
import mctmods.immersivetechnology.common.multiblocks.metal.recipe.BoilerLiquidRecipe;
import mctmods.immersivetechnology.core.registration.ITMultiblockProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class BoilerLiquidRecipeSerializer extends IERecipeSerializer<BoilerLiquidRecipe> {
    public static final DualMapCodec<RegistryFriendlyByteBuf, BoilerLiquidRecipe> CODECS = DualCompositeMapCodecs.composite(
            IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("input"), recipe -> recipe.input,
            DualCodecs.INT.fieldOf("time"), BoilerLiquidRecipe::getTotalProcessTime,
            DualCodecs.DOUBLE.fieldOf("heatPerTick"), BoilerLiquidRecipe::getHeatPerTick,
            DualCodecs.DOUBLE.optionalFieldOf("targetHeat", BoilerLiquidLogic.DEFAULT_WORKING_HEAT_LEVEL), BoilerLiquidRecipe::getTargetHeat,
            BoilerLiquidRecipe::new
    );

    @Override public ItemStack getIcon() { return ITMultiblockProvider.BOILER_LIQUID.iconStack(); }

    @Override protected DualMapCodec<RegistryFriendlyByteBuf, BoilerLiquidRecipe> codecs() { return CODECS; }
}
