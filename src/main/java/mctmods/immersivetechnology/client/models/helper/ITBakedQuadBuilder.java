package mctmods.immersivetechnology.client.models.helper;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class ITBakedQuadBuilder {
    public static final VertexFormat FORMAT = DefaultVertexFormat.BLOCK;
    private static final int VERTEX_SIZE = FORMAT.getVertexSize() / Integer.BYTES;

    private int nextVertex = 0;
    private final int[] data = new int[VERTEX_SIZE * 4];

    public void putVertexData(Vec3 pos, Vec3 faceNormal, double u, double v, TextureAtlasSprite sprite, float[] colour, float alpha) {
        int next = nextVertex * VERTEX_SIZE;

        data[next++] = Float.floatToIntBits((float)pos.x);
        data[next++] = Float.floatToIntBits((float)pos.y);
        data[next++] = Float.floatToIntBits((float)pos.z);

        data[next++] = (int)(colour[0] * 255) |
                ((int)(colour[1] * 255) << 8) |
                ((int)(colour[2] * 255) << 16) |
                ((int)(colour[3] * alpha * 255) << 24);

        data[next++] = Float.floatToIntBits(sprite.getU((float)u));
        data[next++] = Float.floatToIntBits(sprite.getV((float)v));

        data[next++] = 0xF00000;

        data[next] |= (int)(faceNormal.x * 127) & 255;
        data[next] |= ((int)(faceNormal.y * 127) & 255) << 8;
        data[next] |= ((int)(faceNormal.z * 127) & 255) << 16;
        ++next;

        ++nextVertex;
        Preconditions.checkState(next == nextVertex * VERTEX_SIZE);
    }

    public BakedQuad bake(int tint, Direction side, TextureAtlasSprite texture, boolean shade) {
        return new BakedQuad(data, tint, side, texture, shade);
    }
}
