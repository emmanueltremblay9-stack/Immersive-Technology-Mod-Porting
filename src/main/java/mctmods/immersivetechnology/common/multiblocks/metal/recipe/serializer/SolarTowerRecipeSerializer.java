package mctmods.immersivetechnology.common.multiblocks.metal.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import mctmods.immersivetechnology.common.multiblocks.metal.recipe.SolarTowerRecipe;
import mctmods.immersivetechnology.core.registration.ITMultiblockProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class SolarTowerRecipeSerializer extends IERecipeSerializer<SolarTowerRecipe> {
    public static final DualMapCodec<RegistryFriendlyByteBuf, SolarTowerRecipe> CODECS = DualCompositeMapCodecs.composite(
            IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("input"), recipe -> recipe.input,
            IEDualCodecs.FLUID_STACK.fieldOf("output"), recipe -> recipe.fluidOutput,
            DualCodecs.INT.fieldOf("time"), SolarTowerRecipe::getTotalProcessTime,
            DualCodecs.DOUBLE.fieldOf("requiredTemp"), recipe -> recipe.requiredTemp,
            SolarTowerRecipe::new
    );

    @Override public ItemStack getIcon() { return ITMultiblockProvider.SOLAR_TOWER.iconStack(); }

    @Override protected DualMapCodec<RegistryFriendlyByteBuf, SolarTowerRecipe> codecs() { return CODECS; }
}
