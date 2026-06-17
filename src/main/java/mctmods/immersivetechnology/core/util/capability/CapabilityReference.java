package mctmods.immersivetechnology.core.util.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class CapabilityReference<T> implements Supplier<T> {
    private final Supplier<T> supplier;

    private CapabilityReference(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> CapabilityReference<T> of(Supplier<T> supplier) {
        return new CapabilityReference<>(supplier);
    }

    public static <T> CapabilityReference<T> forNeighbor(BlockEntity owner, BlockCapability<T, Direction> capability, Direction direction) {
        return new CapabilityReference<>(() -> {
            Level level = owner.getLevel();
            if (level == null) return null;
            BlockPos neighbor = owner.getBlockPos().relative(direction);
            return level.getCapability(capability, neighbor, direction.getOpposite());
        });
    }

    public boolean isPresent() {
        return get() != null;
    }

    @Nullable
    public T getNullable() { return get(); }

    @Override
    @Nullable
    public T get() {
        return supplier.get();
    }
}
