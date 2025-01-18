package com.kneelawk.krender.engine.backend.frapi.impl.model;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;

import org.joml.Vector2f;
import org.joml.Vector3f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import com.kneelawk.krender.engine.backend.frapi.api.ConversionUtils;

public class FRAPIEmitter implements QuadEmitter {
    private final com.kneelawk.krender.engine.api.buffer.QuadEmitter emitter;
    private final Vector3f scratch3 = new Vector3f();
    private final Vector2f scratch2 = new Vector2f();

    private QuadTransform[] transforms = new QuadTransform[0];

    public FRAPIEmitter(com.kneelawk.krender.engine.api.buffer.QuadEmitter emitter) {this.emitter = emitter;}

    @Override
    public void pushTransform(QuadTransform transform) {
        QuadTransform[] newTransforms = Arrays.copyOf(transforms, transforms.length + 1);
        newTransforms[transforms.length] = transform;
        transforms = newTransforms;
    }

    @Override
    public void popTransform() {
        if (transforms.length > 0) {
            transforms = Arrays.copyOf(transforms, transforms.length - 1);
        }
    }

    @Override
    public QuadEmitter pos(int vertexIndex, float x, float y, float z) {
        emitter.setPos(vertexIndex, x, y, z);
        return this;
    }

    @Override
    public QuadEmitter color(int vertexIndex, int color) {
        emitter.setColor(vertexIndex, color);
        return this;
    }

    @Override
    public QuadEmitter uv(int vertexIndex, float u, float v) {
        emitter.setUv(vertexIndex, u, v);
        return this;
    }

    @Override
    public QuadEmitter spriteBake(TextureAtlasSprite sprite, int bakeFlags) {
        // technically an implementation detail, but the two bake flag formats are very similar
        int flags = bakeFlags ^ com.kneelawk.krender.engine.api.buffer.QuadEmitter.BAKE_DENORMALIZED;

        emitter.spriteBake(sprite, flags);
        return this;
    }

    @Override
    public QuadEmitter lightmap(int vertexIndex, int lightmap) {
        emitter.setLightmap(vertexIndex, lightmap);
        return this;
    }

    @Override
    public QuadEmitter normal(int vertexIndex, float x, float y, float z) {
        emitter.setNormal(vertexIndex, x, y, z);
        return this;
    }

    @Override
    public QuadEmitter cullFace(@Nullable Direction face) {
        emitter.setCullFace(face);
        return this;
    }

    @Override
    public QuadEmitter nominalFace(@Nullable Direction face) {
        emitter.setNominalFace(face);
        return this;
    }

    @Override
    public QuadEmitter material(RenderMaterial material) {
        emitter.setMaterial(ConversionUtils.toKRender(emitter.getRendererOrDefault(), material));
        return this;
    }

    @Override
    public QuadEmitter tintIndex(int colorIndex) {
        emitter.setTintIndex(colorIndex);
        return this;
    }

    @Override
    public QuadEmitter tag(int tag) {
        emitter.setTag(tag);
        return this;
    }

    @Override
    public QuadEmitter copyFrom(QuadView quad) {
        com.kneelawk.krender.engine.api.buffer.QuadEmitter emitter = this.emitter;
        Vector3f vec3 = scratch3;
        Vector2f vec2 = scratch2;

        emitter.setCullFace(quad.cullFace());
        emitter.setNominalFace(quad.nominalFace());
        emitter.setMaterial(ConversionUtils.toKRender(emitter.getRendererOrDefault(), quad.material()));
        emitter.setTintIndex(quad.tintIndex());
        emitter.setTag(quad.tag());

        for (int i = 0; i < 4; i++) {
            quad.copyPos(i, vec3);
            emitter.setPos(i, vec3);

            emitter.setColor(i, quad.color(i));

            quad.copyUv(i, vec2);
            emitter.setUv(i, vec2);

            emitter.setLightmap(i, quad.lightmap(i));

            if (quad.hasNormal(i)) {
                quad.copyNormal(i, vec3);
                emitter.setNormal(i, vec3);
            } else {
                emitter.removeNormal(i);
            }
        }

        return this;
    }

    @Override
    public QuadEmitter fromVanilla(int[] quadData, int startIndex) {
        emitter.fromVanilla(quadData, startIndex);
        return this;
    }

    @Override
    public QuadEmitter fromVanilla(BakedQuad quad, RenderMaterial material, @Nullable Direction cullFace) {
        emitter.fromVanilla(quad, ConversionUtils.toKRender(emitter.getRendererOrDefault(), material), cullFace);
        return this;
    }

    @Override
    public QuadEmitter emit() {
        for (QuadTransform transform : transforms) {
            if (!transform.transform(this)) return this;
        }

        emitter.emit();
        return this;
    }

    @Override
    public float x(int vertexIndex) {
        return emitter.getX(vertexIndex);
    }

    @Override
    public float y(int vertexIndex) {
        return emitter.getY(vertexIndex);
    }

    @Override
    public float z(int vertexIndex) {
        return emitter.getZ(vertexIndex);
    }

    @Override
    public float posByIndex(int vertexIndex, int coordinateIndex) {
        return emitter.getPosByIndex(vertexIndex, coordinateIndex);
    }

    @Override
    public Vector3f copyPos(int vertexIndex, @Nullable Vector3f target) {
        return emitter.copyPos(vertexIndex, target);
    }

    @Override
    public int color(int vertexIndex) {
        return emitter.getColor(vertexIndex);
    }

    @Override
    public float u(int vertexIndex) {
        return emitter.getU(vertexIndex);
    }

    @Override
    public float v(int vertexIndex) {
        return emitter.getV(vertexIndex);
    }

    @Override
    public Vector2f copyUv(int vertexIndex, @Nullable Vector2f target) {
        return emitter.copyUv(vertexIndex, target);
    }

    @Override
    public int lightmap(int vertexIndex) {
        return emitter.getLightmap(vertexIndex);
    }

    @Override
    public boolean hasNormal(int vertexIndex) {
        return emitter.hasNormal(vertexIndex);
    }

    @Override
    public float normalX(int vertexIndex) {
        return emitter.getNormalY(vertexIndex);
    }

    @Override
    public float normalY(int vertexIndex) {
        return emitter.getNormalY(vertexIndex);
    }

    @Override
    public float normalZ(int vertexIndex) {
        return emitter.getNormalZ(vertexIndex);
    }

    @Override
    public @Nullable Vector3f copyNormal(int vertexIndex, @Nullable Vector3f target) {
        return emitter.copyNormal(vertexIndex, target);
    }

    @Override
    public @Nullable Direction cullFace() {
        return emitter.getCullFace();
    }

    @Override
    public @NotNull Direction lightFace() {
        return emitter.getLightFace();
    }

    @Override
    public @Nullable Direction nominalFace() {
        return emitter.getNominalFace();
    }

    @Override
    public Vector3f faceNormal() {
        return emitter.getFaceNormal();
    }

    @Override
    public RenderMaterial material() {
        return ConversionUtils.toFabric(emitter.getMaterial());
    }

    @Override
    public int tintIndex() {
        return emitter.getTintIndex();
    }

    @Override
    public int tag() {
        return emitter.getTag();
    }

    @Override
    public void toVanilla(int[] target, int targetIndex) {
        emitter.toVanilla(target, targetIndex);
    }

    @Override
    public BakedQuad toBakedQuad(TextureAtlasSprite sprite) {
        return emitter.toBakedQuad(sprite);
    }
}
