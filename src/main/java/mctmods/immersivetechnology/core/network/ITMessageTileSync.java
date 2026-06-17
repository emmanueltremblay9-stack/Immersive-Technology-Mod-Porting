package mctmods.immersivetechnology.core.network;

import mctmods.immersivetechnology.common.blocks.helper.ITBaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ITMessageTileSync implements ITMessage {
    private final BlockPos pos;
    private final CompoundTag nbt;

    public ITMessageTileSync(BlockPos pos, CompoundTag nbt) {
        this.pos = pos;
        this.nbt = nbt;
    }

    public ITMessageTileSync(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.nbt = buf.readNbt();
    }

    @Override public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeNbt(this.nbt);
    }

    @Override public void process(IPayloadContext context) {
        ServerPlayer player = context.player() instanceof ServerPlayer serverPlayer ? serverPlayer : null;
        if (player != null) {
            context.enqueueWork(() -> {
                Level level = player.level();
                BlockEntity tile = level.getBlockEntity(this.pos);
                if (tile instanceof ITBaseBlockEntity itbe) {
                    itbe.receiveMessageFromClient(this.nbt);
                }
            });
        }
    }

    @Override public CustomPacketPayload.Type<ITMessageTileSync> type() { return ITPacketHandler.TILE_SYNC; }
}
