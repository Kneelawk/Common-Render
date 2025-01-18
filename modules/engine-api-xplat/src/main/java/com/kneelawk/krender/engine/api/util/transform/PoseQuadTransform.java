package com.kneelawk.krender.engine.api.util.transform;

import org.jetbrains.annotations.UnknownNullability;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.buffer.QuadTransform;
import com.kneelawk.krender.engine.api.buffer.QuadView;

/**
 * Simple quad transform that uses a {@link PoseStack.Pose} to transform each vertex in each quad.
 * <p>
 * This transforms each vertex's position and normal by the given matrix.
 */
public class PoseQuadTransform implements QuadTransform<PoseQuadTransform.Options> {
    private static final ThreadLocal<PoseQuadTransform> INSTANCES =
        ThreadLocal.withInitial(PoseQuadTransform::new);

    /**
     * {@return the singleton instance of this quad transform}
     */
    public static PoseQuadTransform getInstance() {
        return INSTANCES.get();
    }

    private final Vector3f vec = new Vector3f();

    private PoseQuadTransform() {}

    @Override
    public void transform(@UnknownNullability Options context, QuadView input, QuadEmitter output) {
        PoseStack.Pose pose = context.pose();
        Matrix4f matrix = pose.pose();
        Matrix3f normalMatix = pose.normal();
        float granularity = context.granularity();
        boolean granular = Math.abs(granularity) >= QuadEmitter.EPSILON;

        input.copyTo(output);

        for (int i = 0; i < 4; i++) {
            input.copyPos(i, vec);
            vec.mulPosition(matrix);
            if (granular) {
                vec.div(granularity).round().mul(granularity);
            }
            output.setPos(i, vec);

            if (input.hasNormal(i)) {
                input.copyNormal(i, vec);
                vec.mul(normalMatix);
                // normals are already quite granular, so we shouldn't need to modify them
                output.setNormal(i, vec);
            }
        }

        if (context.sort()) output.sortVertices();

        output.emit();
    }

    /**
     * Options for the {@link MatrixQuadTransform}.
     *
     * @param pose        the pose used on the quad vertex positions and normals.
     * @param granularity the smallest value that each transformed position must be a multiple of. This helps
     *                    prevent rounding errors that can cause quads to not overlay properly.
     * @param sort        whether to sort vertices after applying tranformations.
     */
    public record Options(PoseStack.Pose pose, float granularity, boolean sort) {
        /**
         * Create a new options with no sorting.
         *
         * @param pose        the pose used on the quad vertex positions and normals.
         * @param granularity the smallest value that each transformed position must be a multiple of.
         */
        public Options(PoseStack.Pose pose, float granularity) {
            this(pose, granularity, false);
        }

        /**
         * Create a new options with no granularity or sorting.
         *
         * @param pose the pose used on the quad vertex positions and normals.
         */
        public Options(PoseStack.Pose pose) {
            this(pose, 0f);
        }
    }
}
