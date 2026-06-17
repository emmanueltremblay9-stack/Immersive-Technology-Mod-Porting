package mctmods.immersivetechnology.common.multiblocks.metal.recipe.serializer;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import mctmods.immersivetechnology.common.multiblocks.metal.logic.BoilerTankLogic;
import mctmods.immersivetechnology.common.multiblocks.metal.recipe.BoilerTankRecipe;
import mctmods.immersivetechnology.core.registration.ITMultiblockProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class BoilerTankRecipeSerializer extends IERecipeSerializer<BoilerTankRecipe> {
    public static final DualMapCodec<RegistryFriendlyByteBuf, BoilerTankRecipe> CODECS = DualCompositeMapCodecs.composite(
            IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("input"), recipe -> recipe.input,
            IEDualCodecs.FLUID_STACK.fieldOf("result"), recipe -> recipe.output,
            DualCodecs.INT.optionalFieldOf("time", 1), BoilerTankRecipe::getTotalProcessTime,
            DualCodecs.DOUBLE.optionalFieldOf("requiredHeat", BoilerTankLogic.DEFAULT_WORKING_HEAT_LEVEL), recipe -> recipe.requiredHeat,
            BoilerTankRecipe::new
    );

    @Override public ItemStack getIcon() { return ITMultiblockProvider.BOILER_TANK.iconStack(); }

    @Override protected DualMapCodec<RegistryFriendlyByteBuf, BoilerTankRecipe> codecs() { return CODECS; }
}
