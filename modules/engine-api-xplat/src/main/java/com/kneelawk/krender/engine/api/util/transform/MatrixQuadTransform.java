package com.kneelawk.krender.engine.api.util.transform;

import org.jetbrains.annotations.UnknownNullability;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.buffer.QuadTransform;
import com.kneelawk.krender.engine.api.buffer.QuadView;

/**
 * Simple quad transform that uses a matrix to transform each vertex in each quad.
 * <p>
 * This transforms each vertex's position and normal by the given matrix.
 */
public class MatrixQuadTransform implements QuadTransform<MatrixQuadTransform.Options> {
    private static final ThreadLocal<MatrixQuadTransform> INSTANCES = ThreadLocal.withInitial(MatrixQuadTransform::new);

    /**
     * {@return the singleton instance of this quad transform}
     */
    public static MatrixQuadTransform getInstance() {
        return INSTANCES.get();
    }

    private final Vector3f vec = new Vector3f();

    private MatrixQuadTransform() {}

    @Override
    public void transform(@UnknownNullability Options context, QuadView input, QuadEmitter output) {
        Matrix4f matrix = context.matrix();
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
                vec.mulDirection(matrix);
                // normals are already quite granular, so we shouldn't need to modify them
                output.setNormal(i, vec);
            }
        }

        output.emit();
    }

    /**
     * Options for the {@link MatrixQuadTransform}.
     *
     * @param matrix      the matrix used the quad vertex positions and normals.
     * @param granularity the smallest value that each transformed position must be a multiple of. This helps
     *                    prevent rounding errors that can cause quads to not overlay properly.
     */
    public record Options(Matrix4f matrix, float granularity) {}
}
