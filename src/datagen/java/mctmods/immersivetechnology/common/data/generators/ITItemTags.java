package mctmods.immersivetechnology.common.data.generators;

import mctmods.immersivetechnology.core.lib.ITLib;
import mctmods.immersivetechnology.core.registration.ITItems;
import mctmods.immersivetechnology.core.registration.ITTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ITItemTags extends ItemTagsProvider {
    public ITItemTags(PackOutput output, CompletableFuture<Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blocks, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blocks, ITLib.MODID, existingFileHelper);
    }

    @Override protected void addTags(@NotNull Provider provider) {
        ResourceLocation ieHammers = ResourceLocation.fromNamespaceAndPath("immersiveengineering", "tools/hammers");
        ResourceLocation ieScrewdrivers = ResourceLocation.fromNamespaceAndPath("immersiveengineering", "tools/screwdrivers");
        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "salts"))).add(ITItems.SALT.get());
        tag(ITTags.saltDustCommon).add(ITItems.SALT.get());
        tag(ITTags.igniters).add(Items.TORCH, Items.FLINT_AND_STEEL);
        tag(ITTags.igniters_consume).add(Items.TORCH);
        tag(ITTags.formationTools).add(ITItems.FORMATION_TOOL.get()).addOptional(ResourceLocation.fromNamespaceAndPath("immersiveengineering", "hammer")).addOptionalTag(ieHammers);
        tag(ITTags.screwdrivers).addOptional(ResourceLocation.fromNamespaceAndPath("immersiveengineering", "screwdriver")).addOptionalTag(ieScrewdrivers);
        tag(ItemTags.create(ieHammers)).add(ITItems.FORMATION_TOOL.get());
    }
}
