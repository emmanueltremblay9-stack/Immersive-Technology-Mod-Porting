package mctmods.immersivetechnology.mixin.common;

import blusunrize.immersiveengineering.common.blocks.metal.FluidPipeBlockEntity;
import mctmods.immersivetechnology.mixin.common.accessor.IEBlockCapCacheAccess;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(FluidPipeBlockEntity.class)
public abstract class FluidPipeBlockEntityMixin {
    @Final @Shadow(remap = false) private Map<Direction, ?> neighbors;

    @Inject(method = "updateConnectionByte(Lnet/minecraft/core/Direction;)Z", at = @At("HEAD"), remap = false)
    private void invalidateCache(Direction dir, CallbackInfoReturnable<Boolean> cir) {
        Object ref = neighbors.get(dir);
        if (ref instanceof IEBlockCapCacheAccess access) {
            access.immersivetechnology$setCache(null);
        }
    }
}
