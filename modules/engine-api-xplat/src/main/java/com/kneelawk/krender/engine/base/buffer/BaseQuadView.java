package com.kneelawk.krender.engine.base.buffer;

import org.jetbrains.annotations.Nullable;

import org.joml.Vector2f;
import org.joml.Vector3f;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.buffer.QuadView;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.util.ColorUtil;
import com.kneelawk.krender.engine.base.BaseKRendererApi;

import static com.kneelawk.krender.engine.base.buffer.BaseQuadFormat.HEADER_BITS;
import static com.kneelawk.krender.engine.base.buffer.BaseQuadFormat.HEADER_COLOR_INDEX;
import static com.kneelawk.krender.engine.base.buffer.BaseQuadFormat.HEADER_FACE_NORMAL;
import static com.kneelawk.krender.engine.base.buffer.BaseQuadFormat.HEADER_STRIDE;
import static com.kneelawk.krender.engine.base.buffer.BaseQuadFormat.HEADER_TAG;
import static com.kneelawk.krender.engine.base.buffer.BaseQuadFormat.VERTEX_COLOR;
import static com.kneelawk.krender.engine.base.buffer.BaseQuadFormat.VERTEX_LIGHTMAP;
import static com.kneelawk.krender.engine.base.buffer.BaseQuadFormat.VERTEX_NORMAL;
import static com.kneelawk.krender.engine.base.buffer.BaseQuadFormat.VERTEX_STRIDE;
import static com.kneelawk.krender.engine.base.buffer.BaseQuadFormat.VERTEX_U;
import static com.kneelawk.krender.engine.base.buffer.BaseQuadFormat.VERTEX_V;
import static com.kneelawk.krender.engine.base.buffer.BaseQuadFormat.VERTEX_X;
import static com.kneelawk.krender.engine.base.buffer.BaseQuadFormat.VERTEX_Y;
import static com.kneelawk.krender.engine.base.buffer.BaseQuadFormat.VERTEX_Z;
import static com.kneelawk.krender.engine.base.buffer.BaseQuadFormat.isNormalPresent;

// This class is largely based on the Fabric Render API's QuadViewImpl.

/**
 * Base {@link QuadView} implementation for use in backends.
 */
public class BaseQuadView implements QuadView {
    /**
     * The renderer associated with this quad view.
     */
    protected final BaseKRendererApi renderer;

    /**
     * Header and quad data.
     */
    protected int[] data;

    /**
     * The index in the data array where this view's data actually starts.
     */
    protected int baseIndex = 0;

    /**
     * The current nominal face of this quad.
     */
    protected @Nullable Direction nominalFace;
    /**
     * Whether the current quad's geometry bits are incorrect.
     */
    protected boolean geometryInvalid = true;
    /**
     * The calculated, cached face normal.
     */
    protected final Vector3f faceNormal = new Vector3f();

    /**
     * Vec3 scratch vector for copying.
     */
    protected final Vector3f scratch3 = new Vector3f();
    /**
     * Vec2 scratch vector for copying.
     */
    protected final Vector2f scratch2 = new Vector2f();

    /**
     * Creates a new base quad view using the given KRenderer for things like material lookup.
     *
     * @param renderer the KRenderer to associate this quad view with.
     */
    public BaseQuadView(BaseKRendererApi renderer) {
        this.renderer = renderer;
    }

    /**
     * Loads the given data buffer into this quad view.
     * <p>
     * This assumes that the data buffer contains valid geometry flags and that they do not need to be recalculated.
     *
     * @param data      the new data buffer.
     * @param baseIndex the index of the first int of the quad in the buffer.
     */
    public void load(int[] data, int baseIndex) {
        this.data = data;
        this.baseIndex = baseIndex;
        load();
    }

    /**
     * Loads the cache from the data buffer.
     */
    public void load() {
        geometryInvalid = false;
        nominalFace = getLightFace();
        NormalHelper.unpackNormal(getPackedFaceNormal(), faceNormal);
    }

    /**
     * Calculates the cache based on existing data and writes it to the data buffer.
     */
    protected void computeGeometry() {
        if (geometryInvalid) {
            geometryInvalid = false;

            NormalHelper.computeFaceNormal(faceNormal, this);
            data[baseIndex + HEADER_FACE_NORMAL] = NormalHelper.packNormal(faceNormal);

            data[baseIndex + HEADER_BITS] =
                BaseQuadFormat.setLightFace(data[baseIndex + HEADER_BITS], GeometryHelper.computeLightFace(this));

            data[baseIndex + HEADER_BITS] =
                BaseQuadFormat.setGeometryFlags(data[baseIndex + HEADER_BITS],
                    GeometryHelper.computeGeometryFlags(this));
        }
    }

    @Override
    public void copyTo(QuadEmitter target) {
        computeGeometry();

        if (target instanceof BaseQuadEmitter quad) {
            System.arraycopy(data, baseIndex, quad.data, quad.baseIndex, BaseQuadFormat.TOTAL_STRIDE);
            quad.faceNormal.set(faceNormal);
            quad.nominalFace = nominalFace;
            quad.geometryInvalid = false;
        } else {
            Vector3f vec3 = scratch3;
            Vector2f vec2 = scratch2;

            target.setCullFace(getCullFace());
            target.setNominalFace(getNominalFace());
            target.setMaterial(target.getRendererOrDefault().converter().toAssociated(getMaterial()));
            target.setColorIndex(getColorIndex());
            target.setTag(getTag());

            for (int i = 0; i < 4; i++) {
                copyPos(i, vec3);
                target.setPos(i, vec3);

                target.setColor(i, getColor(i));

                copyUv(i, vec2);
                target.setUv(i, vec2);

                target.setLightmap(i, getLightmap(i));

                if (hasNormal(i)) {
                    copyNormal(i, vec3);
                    target.setNormal(i, vec3);
                } else {
                    target.removeNormal(i);
                }
            }
        }
    }

    @Override
    public float getX(int vertexIndex) {
        return Float.intBitsToFloat(data[baseIndex + HEADER_STRIDE + vertexIndex * VERTEX_STRIDE + VERTEX_X]);
    }

    @Override
    public float getY(int vertexIndex) {
        return Float.intBitsToFloat(data[baseIndex + HEADER_STRIDE + vertexIndex * VERTEX_STRIDE + VERTEX_Y]);
    }

    @Override
    public float getZ(int vertexIndex) {
        return Float.intBitsToFloat(data[baseIndex + HEADER_STRIDE + vertexIndex * VERTEX_STRIDE + VERTEX_Z]);
    }

    @Override
    public float getPosByIndex(int vertexIndex, int coordinateIndex) {
        return Float.intBitsToFloat(
            data[baseIndex + HEADER_STRIDE + vertexIndex * VERTEX_STRIDE + VERTEX_X + coordinateIndex]);
    }

    @Override
    public Vector3f copyPos(int vertexIndex, @Nullable Vector3f target) {
        if (target == null) {
            target = new Vector3f();
        }

        final int index = baseIndex + HEADER_STRIDE + vertexIndex * VERTEX_STRIDE + VERTEX_X;
        target.set(Float.intBitsToFloat(data[index]), Float.intBitsToFloat(data[index + 1]),
            Float.intBitsToFloat(data[index + 2]));
        return target;
    }

    @Override
    public int getColor(int vertexIndex) {
        return data[baseIndex + HEADER_STRIDE + vertexIndex * VERTEX_STRIDE + VERTEX_COLOR];
    }

    @Override
    public float getU(int vertexIndex) {
        return Float.intBitsToFloat(data[baseIndex + HEADER_STRIDE + vertexIndex * VERTEX_STRIDE + VERTEX_U]);
    }

    @Override
    public float getV(int vertexIndex) {
        return Float.intBitsToFloat(data[baseIndex + HEADER_STRIDE + vertexIndex * VERTEX_STRIDE + VERTEX_V]);
    }

    @Override
    public float getUvByIndex(int vertexIndex, int coordinateIndex) {
        return Float.intBitsToFloat(
            data[baseIndex + HEADER_STRIDE + vertexIndex * VERTEX_STRIDE + VERTEX_U + coordinateIndex]);
    }

    @Override
    public Vector2f copyUv(int vertexIndex, @Nullable Vector2f target) {
        if (target == null) {
            target = new Vector2f();
        }

        final int index = baseIndex + HEADER_STRIDE + vertexIndex * VERTEX_STRIDE + VERTEX_U;
        target.set(Float.intBitsToFloat(data[index]), Float.intBitsToFloat(data[index + 1]));
        return target;
    }

    @Override
    public int getLightmap(int vertexIndex) {
        return data[baseIndex + HEADER_STRIDE + vertexIndex * VERTEX_STRIDE + VERTEX_LIGHTMAP];
    }

    @Override
    public boolean hasNormal(int vertexIndex) {
        return isNormalPresent(data[baseIndex + HEADER_BITS], vertexIndex);
    }

    @Override
    public float getNormalX(int vertexIndex) {
        return hasNormal(vertexIndex) ?
            NormalHelper.unpackNormalX(data[baseIndex + HEADER_STRIDE + vertexIndex * VERTEX_STRIDE + VERTEX_NORMAL]) :
            Float.NaN;
    }

    @Override
    public float getNormalY(int vertexIndex) {
        return hasNormal(vertexIndex) ?
            NormalHelper.unpackNormalY(data[baseIndex + HEADER_STRIDE + vertexIndex * VERTEX_STRIDE + VERTEX_NORMAL]) :
            Float.NaN;
    }

    @Override
    public float getNormalZ(int vertexIndex) {
        return hasNormal(vertexIndex) ?
            NormalHelper.unpackNormalZ(data[baseIndex + HEADER_STRIDE + vertexIndex * VERTEX_STRIDE + VERTEX_NORMAL]) :
            Float.NaN;
    }

    @Override
    public float getNormalByIndex(int vertexIndex, int coordinateIndex) {
        return hasNormal(vertexIndex) ?
            NormalHelper.unpackNormal(data[baseIndex + HEADER_STRIDE + vertexIndex * VERTEX_STRIDE + VERTEX_NORMAL],
                coordinateIndex) : Float.NaN;
    }

    @Override
    public @Nullable Vector3f copyNormal(int vertexIndex, @Nullable Vector3f target) {
        if (!hasNormal(vertexIndex)) return null;

        if (target == null) {
            target = new Vector3f();
        }

        NormalHelper.unpackNormal(data[baseIndex + HEADER_STRIDE + vertexIndex * VERTEX_STRIDE + VERTEX_NORMAL],
            target);

        return target;
    }

    @Override
    public @Nullable Direction getCullFace() {
        return BaseQuadFormat.getCullFace(data[baseIndex + HEADER_BITS]);
    }

    @Override
    public Direction getLightFace() {
        return BaseQuadFormat.getLightFace(data[baseIndex + HEADER_BITS]);
    }

    @Override
    public @Nullable Direction getNominalFace() {
        return nominalFace;
    }

    @Override
    public Vector3f getFaceNormal() {
        computeGeometry();
        return faceNormal;
    }

    /**
     * {@return the packed face normal of this quad}
     */
    public int getPackedFaceNormal() {
        computeGeometry();
        return data[baseIndex + HEADER_FACE_NORMAL];
    }

    @Override
    public RenderMaterial getMaterial() {
        return BaseQuadFormat.getMaterial(data[baseIndex + HEADER_BITS], renderer.materialManager());
    }

    @Override
    public int getColorIndex() {
        return data[baseIndex + HEADER_COLOR_INDEX];
    }

    @Override
    public int getTag() {
        return data[baseIndex + HEADER_TAG];
    }

    @Override
    public void toVanilla(int[] target, int targetIndex) {
        toVanilla(target, targetIndex, getMaterial());
    }

    @Override
    public BakedQuad toBakedQuad(TextureAtlasSprite sprite) {
        int[] quad = new int[VANILLA_QUAD_STRIDE];
        final RenderMaterial material = getMaterial();

        toVanilla(quad, 0, material);
        int tintIndex = material.isColorIndexDisabled() ? -1 : getColorIndex();
        boolean shade = !material.isDiffuseDisabled();
        return new BakedQuad(quad, tintIndex, getLightFace(), sprite, shade);
    }

    /**
     * Converts this quad to vanilla quad data, but using the given render material.
     *
     * @param target      the array to copy vanilla vertex data to.
     * @param targetIndex the index within the array to start copying to.
     * @param material    the material to use for emissive-ness calculations.
     */
    protected void toVanilla(int[] target, int targetIndex, RenderMaterial material) {
        // we use roughly the same vertex format vanilla uses
        System.arraycopy(data, baseIndex + HEADER_STRIDE, target, targetIndex, VANILLA_QUAD_STRIDE);

        for (int i = 0; i < 4; i++) {
            // convert colors 
            target[i * VERTEX_STRIDE + VERTEX_COLOR] = ColorUtil.toNative(target[i * VERTEX_STRIDE + VERTEX_COLOR]);

            // handle emissives
            if (material.isEmissive()) {
                target[i * VERTEX_STRIDE + VERTEX_LIGHTMAP] = LightTexture.FULL_BRIGHT;
            }
        }
    }

    @Override
    public @Nullable KRenderer getRenderer() {
        return renderer;
    }
}
