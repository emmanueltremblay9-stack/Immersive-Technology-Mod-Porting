package mctmods.immersivetechnology.common.fluids.helper;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.function.Consumer;

public class ITMarkableFluidTank extends FluidTank {
    private static final HolderLookup.Provider BUILTIN_PROVIDER = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

    private final Consumer<Void> markDirty;

    public ITMarkableFluidTank(int capacity, Consumer<Void> markDirty) {
        super(capacity);
        this.markDirty = markDirty;
    }

    @Override protected void onContentsChanged() {
        markDirty.accept(null);
    }

    public static ITMarkableFluidTank makeClient(int capacity, Consumer<Void> markDirty) {
        return new ITMarkableFluidTank(capacity, markDirty);
    }

    public ITMarkableFluidTank readFromNBT(CompoundTag nbt) {
        readFromNBT(BUILTIN_PROVIDER, nbt);
        return this;
    }

    public CompoundTag writeToNBT(CompoundTag nbt) { return writeToNBT(BUILTIN_PROVIDER, nbt); }
}
