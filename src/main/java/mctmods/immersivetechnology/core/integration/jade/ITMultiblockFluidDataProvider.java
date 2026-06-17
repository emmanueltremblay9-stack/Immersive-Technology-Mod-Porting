package mctmods.immersivetechnology.core.integration.jade;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import mctmods.immersivetechnology.common.multiblocks.helper.ITDisplayContext;
import mctmods.immersivetechnology.core.lib.ITLib;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.*;
import snownee.jade.api.fluid.JadeFluidObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ITMultiblockFluidDataProvider implements IServerExtensionProvider<CompoundTag>, IClientExtensionProvider<CompoundTag, FluidView> {

    @Override
    @Nullable
    public List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {
        Object target = accessor.getTarget();
        if (!(target instanceof IMultiblockBE<?> multiblockBE)) {
            return null;
        }
        final IMultiblockBEHelper<?> helper = multiblockBE.getHelper();
        if (helper.getState() instanceof ITDisplayContext dc) {
            IFluidTank[] tanks = dc.getInternalTanks();
            if (tanks != null && tanks.length > 0) {
                List<CompoundTag> list = new ArrayList<>();
                for (IFluidTank tank : tanks) {
                    FluidStack fs = tank.getFluid();
                    JadeFluidObject fluidObject = JadeFluidObject.of(fs.getFluid(), fs.getAmount(), fs.getComponentsPatch());
                    CompoundTag tag = FluidView.writeDefault(fluidObject, tank.getCapacity());
                    list.add(tag);
                }
                return List.of(new ViewGroup<>(list));
            }
        }
        return null;
    }

    @Override
    public List<ClientViewGroup<FluidView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> list) {
        return ClientViewGroup.map(list, FluidView::readDefault, null);
    }

    @Override
    public ResourceLocation getUid() {
        return ITLib.rl("multiblock_fluid");
    }
}
