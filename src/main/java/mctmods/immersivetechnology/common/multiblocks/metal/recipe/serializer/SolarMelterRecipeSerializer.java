package mctmods.immersivetechnology.common.multiblocks.metal.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import mctmods.immersivetechnology.common.multiblocks.metal.recipe.SolarMelterRecipe;
import mctmods.immersivetechnology.core.registration.ITMultiblockProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class SolarMelterRecipeSerializer extends IERecipeSerializer<SolarMelterRecipe> {
    public static final DualMapCodec<RegistryFriendlyByteBuf, SolarMelterRecipe> CODECS = DualCompositeMapCodecs.composite(
            IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("input"), recipe -> recipe.input,
            IEDualCodecs.FLUID_STACK.fieldOf("output"), recipe -> recipe.fluidOutput,
            DualCodecs.INT.fieldOf("time"), SolarMelterRecipe::getTotalProcessTime,
            DualCodecs.DOUBLE.fieldOf("requiredTemp"), recipe -> recipe.requiredTemp,
            SolarMelterRecipe::new
    );

    @Override public ItemStack getIcon() { return ITMultiblockProvider.SOLAR_MELTER.iconStack(); }

    @Override protected DualMapCodec<RegistryFriendlyByteBuf, SolarMelterRecipe> codecs() { return CODECS; }
}
