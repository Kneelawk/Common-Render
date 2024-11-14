package com.kneelawk.krender.engine.base.convert;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.buffer.QuadSink;
import com.kneelawk.krender.engine.api.convert.TypeConverter;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.mesh.Mesh;
import com.kneelawk.krender.engine.api.mesh.MeshBuilder;
import com.kneelawk.krender.engine.base.BaseKRendererApi;
import com.kneelawk.krender.engine.base.buffer.BaseQuadEmitter;

/**
 * Base type converter implementation.
 */
public class BaseTypeConverter implements TypeConverter {
    /**
     * The renderer that this converter is associated with.
     */
    protected final BaseKRendererApi renderer;

    /**
     * Creates a new base type converter.
     *
     * @param renderer the renderer that this converter and everything it converts to will be associated with.
     */
    public BaseTypeConverter(BaseKRendererApi renderer) {this.renderer = renderer;}

    @Override
    public RenderMaterial toAssociated(RenderMaterial material) {
        if (material.getRenderer() == renderer) return material;

        // The base material finder impl supports copying from incompatible material impls
        return renderer.materialManager().materialFinder().copyFrom(material).find();
    }

    @Override
    public QuadSink toAssociated(QuadSink quadSink) {
        if (quadSink.getRenderer() == renderer) return quadSink;

        return new ConvertingQuadEmitter(renderer, quadSink.asQuadEmitter());
    }

    @Override
    public Mesh toAssociated(Mesh mesh) {
        if (mesh.getRenderer() == renderer) return mesh;

        MeshBuilder builder = renderer.meshBuilder();
        BaseQuadEmitter emitter = (BaseQuadEmitter) builder.emitter();
        mesh.forEach(quad -> {
            emitter.copyFrom(quad);
            emitter.emit();
        });

        return builder.build();
    }

    @Override
    public QuadEmitter fromVertexConsumer(VertexConsumer consumer) {
        return new VertexConsumerQuadEmitter(renderer, consumer);
    }

    @Override
    public QuadEmitter fromVertexConsumerProvider(Function<RenderMaterial, VertexConsumer> provider) {
        return new VertexConsumerProviderQuadEmitter(renderer, provider);
    }
}
