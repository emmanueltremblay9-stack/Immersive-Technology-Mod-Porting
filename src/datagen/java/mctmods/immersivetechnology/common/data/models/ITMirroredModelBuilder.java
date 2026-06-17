package mctmods.immersivetechnology.common.data.models;

import com.google.gson.JsonObject;
import mctmods.immersivetechnology.client.models.mirror.ITMirroredModelLoader;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ITMirroredModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
    public static <T extends ModelBuilder<T>> ITMirroredModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new ITMirroredModelBuilder<>(parent, existingFileHelper);
    }

    private ITNongeneratedModels.ITNongeneratedModel inner;

    protected ITMirroredModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(ITMirroredModelLoader.ID, parent, existingFileHelper, false);
    }

    public ITMirroredModelBuilder<T> inner(ITNongeneratedModels.ITNongeneratedModel inner) {
        this.inner = inner;
        return this;
    }

    @Override public JsonObject toJson(JsonObject json) {
        JsonObject result = super.toJson(json);
        result.add(ITMirroredModelLoader.INNER_MODEL, inner.toJson());
        return result;
    }
}
