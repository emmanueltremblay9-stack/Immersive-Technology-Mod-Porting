package mctmods.immersivetechnology.mixin.common.accessor;

import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "blusunrize.immersiveengineering.common.util.IEBlockCapabilityCaches$IEBlockCapCacheImpl", remap = false)
public interface IEBlockCapCacheAccess {
    @Accessor("cache")
    void immersivetechnology$setCache(BlockCapabilityCache<?, ?> cache);
}
