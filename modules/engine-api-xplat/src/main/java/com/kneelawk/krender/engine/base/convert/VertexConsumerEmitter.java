package com.kneelawk.krender.engine.base.convert;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;

import com.kneelawk.krender.engine.api.buffer.QuadView;
import com.kneelawk.krender.engine.api.material.RenderMaterial;

import static com.kneelawk.krender.engine.api.util.ColorUtils.alpha;
import static com.kneelawk.krender.engine.api.util.ColorUtils.blue;
import static com.kneelawk.krender.engine.api.util.ColorUtils.green;
import static com.kneelawk.krender.engine.api.util.ColorUtils.red;

/**
 * Emits quads to vertex consumers.
 */
public class VertexConsumerEmitter {
    private final Vector3f vec3 = new Vector3f();
    private final Vector2f vec2 = new Vector2f();

    /**
     * Creates a new {@link VertexConsumerEmitter}.
     */
    public VertexConsumerEmitter() {}

    /**
     * Emits a quad to a vertex consumer.
     *
     * @param quad     the quad to emit.
     * @param consumer the vertex consumer to emit the quad to.
     */
    public void emitQuad(QuadView quad, VertexConsumer consumer) {
        RenderMaterial mat = quad.getMaterial();

        for (int i = 0; i < 4; i++) {
            // vertex consumers don't seem to need a specific order of attributes anymore,
            // so we must make sure to specify all of them

            quad.copyPos(i, vec3);
            consumer.addVertex(vec3);

            int color = quad.getColor(i);
            consumer.setColor(red(color), green(color), blue(color), alpha(color));

            quad.copyUv(i, vec2);
            consumer.setUv(vec2.x, vec2.y);

            // QuadEmitters do not currently support supplying an overlay position
            consumer.setOverlay(OverlayTexture.NO_OVERLAY);

            if (mat.isEmissive()) {
                consumer.setLight(LightTexture.FULL_BRIGHT);
            } else {
                consumer.setLight(quad.getLightmap(i));
            }

            vec3.set(quad.getFaceNormal());
            // copyNormal does nothing if the normal is not present
            quad.copyNormal(i, vec3);
            consumer.setNormal(vec3.x, vec3.y, vec3.z);
        }
    }
}
