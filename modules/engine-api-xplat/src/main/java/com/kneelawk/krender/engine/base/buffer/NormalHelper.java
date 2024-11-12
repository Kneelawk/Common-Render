package com.kneelawk.krender.engine.base.buffer;

// This class is largely based on the Fabric Render API's NormalHelper.

import org.jetbrains.annotations.NotNull;

import org.joml.Vector3f;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;

import com.kneelawk.krender.engine.api.buffer.QuadView;

/**
 * Static values and helpers for normal calculations.
 */
public final class NormalHelper {
    private NormalHelper() {}

    private static final float PACK = 127f;
    private static final float UNPACK = 1f / PACK;

    private static int pack(float f) {
        return (int) (f * PACK) & 0xFF;
    }

    private static float unpack(int i) {
        return ((byte) (i & 0xFF)) * UNPACK;
    }

    /**
     * Packs a normal vector within -1 to 1 range into a 32-bit int.
     *
     * @param x the normal vector x value.
     * @param y the normal vector y value.
     * @param z the normal vector z value.
     * @return the normal vector as an int.
     */
    public static int packNormal(float x, float y, float z) {
        x = Mth.clamp(x, -1, 1);
        y = Mth.clamp(y, -1, 1);
        z = Mth.clamp(z, -1, 1);

        return pack(x) | (pack(y) << 8) | (pack(z) << 16);
    }

    /**
     * Packs a normal vector within -1 to 1 range into a 32-bit int.
     *
     * @param normal the normal vector.
     * @return the normal vector as an int.
     */
    public static int packNormal(Vector3f normal) {
        return packNormal(normal.x, normal.y, normal.z);
    }

    /**
     * Packs a normal vector element within -1 to 1 range into the given packed 32-bit int.
     *
     * @param packed          the existing packed normal.
     * @param f               the normal vector element to pack.
     * @param coordinateIndex the index of the element within the normal vector.
     * @return the new packed normal vector.
     */
    public static int packNormal(int packed, float f, int coordinateIndex) {
        return (packed & (0xFFFFFF00 << (coordinateIndex * 8))) | (pack(f) << (coordinateIndex * 8));
    }

    /**
     * Unpacks the X component of an int normal vector.
     *
     * @param packed the packed int normal vector.
     * @return the unpacked X component of the normal vector.
     */
    public static float unpackNormalX(int packed) {
        return unpack(packed);
    }

    /**
     * Unpacks the Y component of an int normal vector.
     *
     * @param packed the packed int normal vector.
     * @return the unpacked Y component of the normal vector.
     */
    public static float unpackNormalY(int packed) {
        return unpack(packed >>> 8);
    }

    /**
     * Unpacks the Z component of an int normal vector.
     *
     * @param packed the packed int normal vector.
     * @return the unpacked Z component of the normal vector.
     */
    public static float unpackNormalZ(int packed) {
        return unpack(packed >>> 16);
    }

    /**
     * Unpacks the requested component of an int normal vector.
     *
     * @param packed          the packed int normal vector.
     * @param coordinateIndex the index of the component in the vector to unpack.
     * @return the unpacked requested component of the normal vector.
     */
    public static float unpackNormal(int packed, int coordinateIndex) {
        return unpack(packed >>> (coordinateIndex * 8));
    }

    /**
     * Unpacks the int normal vector into the given vector object.
     *
     * @param packed the packed int vector.
     * @param output the vector object to unpack the int vector into.
     */
    public static void unpackNormal(int packed, Vector3f output) {
        output.set(unpackNormalX(packed), unpackNormalY(packed), unpackNormalZ(packed));
    }

    /**
     * Determines the normal vector of a quad based on the quad's vertex positions.
     * <p>
     * This assumes the quad's vertices are in counter-clockwise order. If the quad's {@link QuadView#getNominalFace()}
     * is set, then this will check and use it as a shortcut if valid.
     *
     * @param output the vector to write the face normal to.
     * @param quad   the quad to compute the face normal of.
     */
    public static void computeFaceNormal(@NotNull Vector3f output, QuadView quad) {
        // check nominal face shortcut
        final Direction nominalFace = quad.getNominalFace();
        if (nominalFace != null && GeometryHelper.isParallel(nominalFace, quad)) {
            Vec3i normal = nominalFace.getNormal();
            output.set(normal.getX(), normal.getY(), normal.getZ());
            return;
        }

        // get face's diagonal tangents
        final float dx0 = quad.getX(2) - quad.getX(0);
        final float dy0 = quad.getY(2) - quad.getY(0);
        final float dz0 = quad.getZ(2) - quad.getZ(0);
        final float dx1 = quad.getX(3) - quad.getX(1);
        final float dy1 = quad.getY(3) - quad.getY(1);
        final float dz1 = quad.getZ(3) - quad.getZ(1);

        // cross diagonals
        float nx = dy0 * dz1 - dz0 * dy1;
        float ny = dz0 * dx1 - dx0 * dz1;
        float nz = dx0 * dy1 - dy0 * dx1;

        // normalize normal vec
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);

        if (len != 0) {
            nx /= len;
            ny /= len;
            nz /= len;
        }

        output.set(nx, ny, nz);
    }
}
