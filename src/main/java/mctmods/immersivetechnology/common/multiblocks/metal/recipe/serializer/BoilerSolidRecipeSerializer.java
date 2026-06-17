package mctmods.immersivetechnology.common.multiblocks.metal.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import mctmods.immersivetechnology.common.multiblocks.metal.logic.BoilerSolidLogic;
import mctmods.immersivetechnology.common.multiblocks.metal.recipe.BoilerSolidRecipe;
import mctmods.immersivetechnology.core.registration.ITMultiblockProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class BoilerSolidRecipeSerializer extends IERecipeSerializer<BoilerSolidRecipe> {
    public static final DualMapCodec<RegistryFriendlyByteBuf, BoilerSolidRecipe> CODECS = DualCompositeMapCodecs.composite(
            IngredientWithSize.CODECS.fieldOf("input"), recipe -> recipe.input,
            DualCodecs.DOUBLE.fieldOf("heatPerTick"), BoilerSolidRecipe::getHeatPerTick,
            DualCodecs.DOUBLE.optionalFieldOf("targetHeat", BoilerSolidLogic.DEFAULT_WORKING_HEAT_LEVEL), BoilerSolidRecipe::getTargetHeat,
            BoilerSolidRecipe::new
    );

    @Override public ItemStack getIcon() { return ITMultiblockProvider.BOILER_SOLID.iconStack(); }

    @Override protected DualMapCodec<RegistryFriendlyByteBuf, BoilerSolidRecipe> codecs() { return CODECS; }
}
