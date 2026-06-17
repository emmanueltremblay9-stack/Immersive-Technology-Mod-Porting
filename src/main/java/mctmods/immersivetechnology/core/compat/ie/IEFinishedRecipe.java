package mctmods.immersivetechnology.core.compat.ie;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class IEFinishedRecipe<T extends IEFinishedRecipe<T>> {
    protected final RecipeSerializer<?> serializer;
    protected final List<Consumer<JsonObject>> writers = new ArrayList<>();
    protected int maxInputCount = 0;
    private int inputCount = 0;

    public IEFinishedRecipe(RecipeSerializer<?> serializer) {
        this.serializer = serializer;
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T)this;
    }

    public T addWriter(Consumer<JsonObject> writer) {
        writers.add(writer);
        return self();
    }

    public T addInput(IngredientWithSize input) {
        String key = maxInputCount <= 1 ? "input" : "input" + inputCount++;
        return addWriter(json -> json.add(key, input.serialize()));
    }

    public T addFluidTag(String key, TagKey<Fluid> fluidTag, int amount) {
        return addWriter(json -> {
            JsonObject obj = new JsonObject();
            obj.addProperty("tag", fluidTag.location().toString());
            obj.addProperty("amount", amount);
            json.add(key, obj);
        });
    }

    public T addFluidTag(String key, SizedFluidIngredient ingredient) {
        return addWriter(json -> json.add(key, encode(SizedFluidIngredient.FLAT_CODEC, ingredient)));
    }

    public T addFluid(String key, FluidStack stack) {
        return addWriter(json -> json.add(key, encode(FluidStack.CODEC, stack)));
    }

    public T setTime(int time) {
        return addWriter(json -> json.addProperty("time", time));
    }

    public T setEnergy(int energy) {
        return addWriter(json -> json.addProperty("energy", energy));
    }

    protected JsonObject serializeRecipeData() {
        JsonObject json = new JsonObject();
        writers.forEach(writer -> writer.accept(json));
        return json;
    }

    public void build(RecipeOutput out, ResourceLocation id) {
        JsonObject json = serializeRecipeData();
        ResourceLocation serializerName = BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer);
        if (serializerName == null) {
            throw new IllegalStateException("Unregistered recipe serializer: " + serializer);
        }
        json.addProperty("type", serializerName.toString());
        Recipe<?> recipe = Recipe.CODEC.parse(JsonOps.INSTANCE, json)
                .getOrThrow(message -> new IllegalStateException("Failed to encode recipe " + id + ": " + message));
        out.accept(id, recipe, null);
    }

    protected static <A> JsonElement encode(Codec<A> codec, A value) {
        return codec.encodeStart(JsonOps.INSTANCE, value).result().orElseThrow();
    }
}
