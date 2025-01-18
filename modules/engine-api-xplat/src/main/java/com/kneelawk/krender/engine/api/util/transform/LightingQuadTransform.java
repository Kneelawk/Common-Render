package com.kneelawk.krender.engine.api.util.transform;

import org.jetbrains.annotations.UnknownNullability;

import net.minecraft.client.renderer.LightTexture;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.buffer.QuadTransform;
import com.kneelawk.krender.engine.api.buffer.QuadView;

/**
 * Simple quad transform that ensures quads' light values are no less than a given value.
 */
public class LightingQuadTransform implements QuadTransform<LightingQuadTransform.Options> {
    private static final LightingQuadTransform INSTANCE = new LightingQuadTransform();

    /**
     * {@return an instance of this quad transform}
     */
    public static LightingQuadTransform getInstance() {
        return INSTANCE;
    }

    private LightingQuadTransform() {}

    @Override
    public void transform(@UnknownNullability Options context, QuadView input, QuadEmitter output) {
        int minPackedLight = context.minPackedLight();
        int minBlock = LightTexture.block(minPackedLight);
        int minSky = LightTexture.sky(minPackedLight);

        input.copyTo(output);

        for (int i = 0; i < 4; i++) {
            int light = output.getLightmap(i);
            int block = Math.max(LightTexture.block(light), minBlock);
            int sky = Math.max(LightTexture.sky(light), minSky);
            output.setLightmap(i, LightTexture.pack(block, sky));
        }

        output.emit();
    }

    /**
     * Options for the {@link LightingQuadTransform}.
     *
     * @param minPackedLight the minimum packed light value for each vertex.
     */
    public record Options(int minPackedLight) {}
}
