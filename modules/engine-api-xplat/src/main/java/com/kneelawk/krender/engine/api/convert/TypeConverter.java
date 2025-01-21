package com.kneelawk.krender.engine.api.convert;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.buffer.QuadSink;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.mesh.Mesh;

/**
 * Converts between different backends' types and between this backend's types and vanilla types.
 */
public interface TypeConverter {
    /**
     * Converts a render material that is potentially from another backend into one from this backend.
     *
     * @param material the render material to convert.
     * @return the potentially new, roughly equivalent render material that is associated with this backend.
     */
    RenderMaterial toAssociated(RenderMaterial material);

    /**
     * Wraps a quad-sink that is potentially from another backend in one compatible with this backend.
     *
     * @param quadSink the quad-sink to wrap.
     * @return a new wrapper quad-sink that is compatible with this backend.
     */
    QuadSink toAssociated(QuadSink quadSink);

    /**
     * Converts a mesh that is potentially from another backend into one from this backed.
     *
     * @param mesh the mesh to convert.
     * @return the potentially new, roughly equivalent mesh that is associated with this backend.
     */
    Mesh toAssociated(Mesh mesh);

    /**
     * Creates a quad emitter that wraps the given {@link VertexConsumer}.
     * <p>
     * Note: most platforms likely will not support some features that require different render materials.
     *
     * @param consumer the vertex consumer to wrap.
     * @return the quad emitter for the given vertex consumer.
     */
    QuadEmitter fromVertexConsumer(VertexConsumer consumer);

    /**
     * Creates a quad emitter that wraps the given {@link MultiBufferSource} for emitting terrain quads.
     *
     * @param source the multi-buffer source to wrap.
     * @return the quad emitter for the given multi-buffer source.
     */
    default QuadEmitter fromBlockMultiBufferSource(MultiBufferSource source) {
        return fromVertexConsumerProvider(mat -> {
            RenderType type = mat.toVanillaBlock();
            if (type == null) return source.getBuffer(RenderType.cutout());
            return source.getBuffer(type);
        });
    }

    /**
     * Creates a quad emitter that wraps the given {@link MultiBufferSource} for emitting item quads.
     *
     * @param source the multi-buffer source to wrap.
     * @return the quad emitter for the given multi-buffer source.
     */
    default QuadEmitter fromItemMultiBufferSource(MultiBufferSource source) {
        return fromVertexConsumerProvider(mat -> {
            RenderType type = mat.toVanillaItem();
            if (type == null) return source.getBuffer(Sheets.cutoutBlockSheet());
            return source.getBuffer(type);
        });
    }

    /**
     * Creates a quad emitter that wraps the given {@link MultiBufferSource} for emitting entity quads.
     *
     * @param source the multi-buffer source to wrap.
     * @return the quad emitter for the given multi-buffer source.
     */
    default QuadEmitter fromEntityMultiBufferSource(MultiBufferSource source) {
        return fromVertexConsumerProvider(mat -> {
            RenderType type = mat.toVanillaEntity();
            if (type == null) return source.getBuffer(RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS));
            return source.getBuffer(type);
        });
    }

    /**
     * Creates a quad emitter that wraps the given provider of vertex consumers for render materials.
     *
     * @param provider the vertex consumer provider to wrap.
     * @return the quad emitter for the given vertex consumer provider.
     */
    QuadEmitter fromVertexConsumerProvider(Function<RenderMaterial, VertexConsumer> provider);
}
