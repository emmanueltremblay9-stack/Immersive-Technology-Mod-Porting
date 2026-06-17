package mctmods.immersivetechnology.core.network;

import mctmods.immersivetechnology.common.gui.helper.ITContainerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ITMessageContainerUpdate implements ITMessage {
    private final int windowId;
    private final CompoundTag nbt;

    public ITMessageContainerUpdate(FriendlyByteBuf buf) {
        this.windowId = buf.readByte();
        this.nbt = buf.readNbt();
    }

    @Override public void toBytes(FriendlyByteBuf buf) {
        buf.writeByte(this.windowId);
        buf.writeNbt(this.nbt);
    }

    @Override public void process(IPayloadContext context) {
        ServerPlayer player = context.player() instanceof ServerPlayer serverPlayer ? serverPlayer : null;
        if (player != null) {
            context.enqueueWork(() -> {
                player.resetLastActionTime();
                if (player.containerMenu.containerId == this.windowId) {
                    AbstractContainerMenu menu = player.containerMenu;
                    if (menu instanceof ITContainerMenu itMenu) { itMenu.receiveMessageFromScreen(this.nbt); }
                }
            });
        }
    }

    @Override public CustomPacketPayload.Type<ITMessageContainerUpdate> type() { return ITPacketHandler.CONTAINER_UPDATE; }
}
