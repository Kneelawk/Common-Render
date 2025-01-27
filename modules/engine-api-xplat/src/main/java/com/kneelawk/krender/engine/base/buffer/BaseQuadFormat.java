package com.kneelawk.krender.engine.base.buffer;

import com.google.common.base.Preconditions;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;

import com.kneelawk.krender.engine.api.buffer.QuadView;
import com.kneelawk.krender.engine.api.material.MaterialManager;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.util.DirectionIds;

import static com.kneelawk.krender.engine.api.util.DirectionIds.DIRECTION_BIT_COUNT;
import static com.kneelawk.krender.engine.api.util.DirectionIds.DIRECTION_MASK;

// This class is largely based on the Fabric Render Indigo EncodingFormat.

/**
 * Static values useful for encoding and decoding quads when using the base implementations.
 */
public final class BaseQuadFormat {
    private BaseQuadFormat() {}

    /**
     * The int index of the bits in the header.
     */
    public static final int HEADER_BITS = 0;
    /**
     * The int index of the face normal in the header.
     */
    public static final int HEADER_FACE_NORMAL = 1;
    /**
     * The int index of the color index in the header.
     */
    public static final int HEADER_TINT_INDEX = 2;
    /**
     * The int index of the tag in the header.
     */
    public static final int HEADER_TAG = 3;
    /**
     * The number of ints in the header.
     */
    public static final int HEADER_STRIDE = 4; // 4 ints

    /**
     * The int index of the x position in the vertex.
     */
    public static final int VERTEX_X = 0;
    /**
     * The int index of the y position in the vertex.
     */
    public static final int VERTEX_Y = 1;
    /**
     * The int index of the z position in the vertex.
     */
    public static final int VERTEX_Z = 2;
    /**
     * The int index of the color in the vertex.
     */
    public static final int VERTEX_COLOR = 3;
    /**
     * The int index of the u tex-coord in the vertex.
     */
    public static final int VERTEX_U = 4;
    /**
     * The int index of the v tex-coord in the vertex.
     */
    public static final int VERTEX_V = 5;
    /**
     * The int index of the lightmap value in the vertex.
     */
    public static final int VERTEX_LIGHTMAP = 6;
    /**
     * The int index of the compressed normal in the vertex.
     */
    public static final int VERTEX_NORMAL = 7;
    /**
     * The number of ints in a vertex.
     */
    public static final int VERTEX_STRIDE = 8;

    /**
     * The number of ints in a quad.
     */
    public static final int QUAD_STRIDE = VERTEX_STRIDE * 4;
    /**
     * The number of bytes in a quad.
     */
    public static final int QUAD_STRIDE_BYTES = QUAD_STRIDE * 4;
    /**
     * The number of ints in a quad, including the quad's header.
     */
    public static final int TOTAL_STRIDE = HEADER_STRIDE + QUAD_STRIDE;

    /**
     * Empty vertex data.
     */
    public static final int[] EMPTY = new int[TOTAL_STRIDE];

    /**
     * The bit offset of the cull face in the header bits.
     */
    public static final int CULL_SHIFT = 0;
    /**
     * The inverse mask of the cull face.
     */
    public static final int CULL_INVERSE_MASK = ~(DIRECTION_MASK << CULL_SHIFT);
    /**
     * The bit offset of the light face in the header bits.
     */
    public static final int LIGHT_SHIFT = CULL_SHIFT + DIRECTION_BIT_COUNT;
    /**
     * The inverse mask of the light face.
     */
    public static final int LIGHT_INVERSE_MASK = ~(DIRECTION_MASK << LIGHT_SHIFT);
    /**
     * The bit offset of the normal presence flags in the header bits.
     */
    public static final int NORMALS_SHIFT = LIGHT_SHIFT + DIRECTION_BIT_COUNT;
    /**
     * The number of bits in the normal presence flags.
     */
    public static final int NORMALS_COUNT = 4;
    /**
     * The bit mask of the normal presence flags, not shifted.
     */
    public static final int NORMALS_MASK = (1 << NORMALS_COUNT) - 1;
    /**
     * The shifted inverse mask of the normal presence flags.
     */
    public static final int NORMALS_INVERSE_MASK = ~(NORMALS_MASK << NORMALS_SHIFT);
    /**
     * The bit offset of the geometry flags in the header bits.
     */
    public static final int GEOMETRY_SHIFT = NORMALS_SHIFT + NORMALS_COUNT;
    /**
     * The bit mask of the geometry flags, not shifted.
     */
    public static final int GEOMETRY_MASK = (1 << GeometryHelper.FLAG_BIT_COUNT) - 1;
    /**
     * The shifted inverse mask of the geometry flags.
     */
    public static final int GEOMETRY_INVERSE_MASK = ~(GEOMETRY_MASK << GEOMETRY_SHIFT);
    /**
     * The bit offset of the material in the header bits.
     */
    public static final int MATERIAL_SHIFT = GEOMETRY_SHIFT + GeometryHelper.FLAG_BIT_COUNT;
    /**
     * The inverse mask of the material.
     */
    public static final int MATERIAL_INVERSE_MASK = ~(BaseMaterialViewApi.FULL_BIT_MASK << MATERIAL_SHIFT);

    static {
        // We check that our vertices are the same size and format as vanilla's because that makes translation a lot easier
        Preconditions.checkState(VERTEX_STRIDE == QuadView.VANILLA_VERTEX_STRIDE,
            "KRender Engine base vertex format (%s ints) is incompatible with vanilla vertex format (%s ints)",
            VERTEX_STRIDE, QuadView.VANILLA_VERTEX_STRIDE);

        // Check that there are enough bits in the header to hold everything
        Preconditions.checkState(MATERIAL_SHIFT + BaseMaterialViewApi.TOTAL_BIT_LENGTH <= 32,
            "KRender Engine base quad format header bit count (%s) has exceeded 32 bits",
            MATERIAL_SHIFT + BaseMaterialViewApi.TOTAL_BIT_LENGTH);
    }

    /**
     * Gets the direction cull face when given header bits.
     *
     * @param bits the header bits.
     * @return the direction cull face.
     */
    public static @Nullable Direction getCullFace(int bits) {
        return DirectionIds.idToDirection((bits >>> CULL_SHIFT) & DIRECTION_MASK);
    }

    /**
     * Sets the cull face when given existing header bits and the new cull face direction.
     *
     * @param bits the existing header bits.
     * @param face the new cull face direction.
     * @return the header bits with the new cull face.
     */
    public static int setCullFace(int bits, @Nullable Direction face) {
        return (bits & CULL_INVERSE_MASK) | (DirectionIds.directionToId(face) << CULL_SHIFT);
    }

    /**
     * Gets the light face when given header bits.
     *
     * @param bits the header bits.
     * @return the light face.
     */
    public static Direction getLightFace(int bits) {
        final Direction direction = DirectionIds.idToDirection((bits >> LIGHT_SHIFT) & DIRECTION_MASK);
        assert direction != null;
        return direction;
    }

    /**
     * Sets the light face when given existing header bits and the new light face direction.
     *
     * @param bits the existing header bits.
     * @param face the new light face direction.
     * @return the header bits with the new light face.
     */
    public static int setLightFace(int bits, Direction face) {
        return (bits & LIGHT_INVERSE_MASK) | (DirectionIds.directionToId(face) << LIGHT_SHIFT);
    }

    /**
     * Gets the entire set of normals flags when given header bits.
     *
     * @param bits the header bits.
     * @return the normal flags.
     */
    public static int getNormalFlags(int bits) {
        return (bits >> NORMALS_SHIFT) & NORMALS_MASK;
    }

    /**
     * Gets whether a specific normal is present when given header bits.
     *
     * @param bits        the header bits.
     * @param vertexIndex the index of the normal to check.
     * @return whether the normal exists.
     */
    public static boolean isNormalPresent(int bits, int vertexIndex) {
        return (getNormalFlags(bits) & (1 << vertexIndex)) != 0;
    }

    /**
     * Sets whether a specific normal is present when given existing header bits, the index of the normal, and whether
     * it should be present.
     *
     * @param bits        the existing header bits.
     * @param vertexIndex the index of the vertex the normal is for.
     * @param present     whether the normal should be marked as present.
     * @return the new header bits.
     */
    public static int setNormalPresent(int bits, int vertexIndex, boolean present) {
        final int offset = NORMALS_SHIFT + vertexIndex;
        return (bits & ~(1 << offset)) | ((present ? 1 : 0) << offset);
    }

    /**
     * Gets the geometry flags from the header bits.
     *
     * @param bits the header bits.
     * @return the geometry flags.
     */
    public static int getGeometryFlags(int bits) {
        return (bits >> GEOMETRY_SHIFT) & GEOMETRY_MASK;
    }

    /**
     * Sets the geometry flags when given existing header bits and the new geometry flags.
     *
     * @param bits          the existing header bits.
     * @param geometryFlags the new geometry flags.
     * @return the new header bits.
     */
    public static int setGeometryFlags(int bits, int geometryFlags) {
        return (bits & GEOMETRY_INVERSE_MASK) | ((geometryFlags & GEOMETRY_MASK) << GEOMETRY_SHIFT);
    }

    /**
     * Gets the render material from the given manager when given header bits.
     *
     * @param bits    the header bits.
     * @param manager the material manager.
     * @return the render material.
     */
    public static RenderMaterial getMaterial(int bits, MaterialManager manager) {
        return manager.materialByIntId((bits >>> MATERIAL_SHIFT) & BaseMaterialViewApi.FULL_BIT_MASK);
    }

    /**
     * Sets the render material when given existing header bits and the new render material.
     *
     * @param bits     the existing header bits.
     * @param material the new render material.
     * @return the new header bits.
     */
    public static int setMaterial(int bits, RenderMaterial material) {
        return (bits & MATERIAL_INVERSE_MASK) | (material.intId() << MATERIAL_SHIFT);
    }
}
