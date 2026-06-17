package mctmods.immersivetechnology.core.util;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class ITFluidIngredients {
    public static SizedFluidIngredient deserialize(JsonObject json) {
        return SizedFluidIngredient.FLAT_CODEC.parse(JsonOps.INSTANCE, json)
                .getOrThrow(message -> new IllegalArgumentException("Invalid fluid ingredient: " + message));
    }

    public static SizedFluidIngredient read(RegistryFriendlyByteBuf buffer) {
        return SizedFluidIngredient.STREAM_CODEC.decode(buffer);
    }

    public static void write(RegistryFriendlyByteBuf buffer, SizedFluidIngredient ingredient) {
        SizedFluidIngredient.STREAM_CODEC.encode(buffer, ingredient);
    }

    private ITFluidIngredients() {}
}
