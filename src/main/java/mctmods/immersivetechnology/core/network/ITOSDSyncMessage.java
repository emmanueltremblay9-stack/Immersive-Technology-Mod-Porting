package mctmods.immersivetechnology.core.network;

import mctmods.immersivetechnology.common.blocks.metal.logic.OSDCommonBlockEntity;
import mctmods.immersivetechnology.common.blocks.metal.logic.ValveCommonBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ITOSDSyncMessage(BlockPos pos, long lastAccepted, long average, int packetAverage) implements ITMessage {
    public ITOSDSyncMessage(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readLong(), buf.readLong(), buf.readInt());
    }

    @Override public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeLong(lastAccepted);
        buf.writeLong(average);
        buf.writeInt(packetAverage);
    }

    @Override public void process(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player == null) { return; }
            BlockEntity te = player.level().getBlockEntity(pos);
            if (te instanceof OSDCommonBlockEntity osd) {
                osd.lastAcceptedAmount = lastAccepted;
            }
            if (te instanceof ValveCommonBlockEntity valve) {
                valve.lastAcceptedAmount = lastAccepted;
                valve.average = average;
                valve.packetAverage = packetAverage;
            }
        });
    }

    @Override public CustomPacketPayload.Type<ITOSDSyncMessage> type() { return ITPacketHandler.OSD_SYNC; }
}
