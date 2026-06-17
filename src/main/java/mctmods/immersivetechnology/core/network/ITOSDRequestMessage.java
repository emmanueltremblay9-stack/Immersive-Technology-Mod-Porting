package mctmods.immersivetechnology.core.network;

import mctmods.immersivetechnology.common.blocks.metal.logic.OSDCommonBlockEntity;
import mctmods.immersivetechnology.common.blocks.metal.logic.ValveCommonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ITOSDRequestMessage(BlockPos pos) implements ITMessage {
    public ITOSDRequestMessage(FriendlyByteBuf buf) { this(buf.readBlockPos()); }

    @Override public void toBytes(FriendlyByteBuf buf) { buf.writeBlockPos(pos); }

    @Override public void process(IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.player() instanceof ServerPlayer serverPlayer ? serverPlayer : null;
            if (player != null) {
                Level level = player.level();
                BlockEntity te = level.getBlockEntity(pos);
                if (te instanceof OSDCommonBlockEntity trash) {
                    ITPacketHandler.sendToPlayer(player, new ITOSDSyncMessage(pos, trash.lastAcceptedAmount, 0, 0));
                }
                if (te instanceof ValveCommonBlockEntity valve) {
                    ITPacketHandler.sendToPlayer(player, new ITOSDSyncMessage(pos, valve.lastAcceptedAmount, valve.average, valve.packetAverage));
                }
            }
        });
    }

    @Override public CustomPacketPayload.Type<ITOSDRequestMessage> type() { return ITPacketHandler.OSD_REQUEST; }
}
