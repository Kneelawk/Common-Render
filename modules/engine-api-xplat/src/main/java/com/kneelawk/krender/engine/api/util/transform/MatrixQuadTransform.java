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
public class MatrixQuadTransform implements QuadTransform<Matrix4f> {
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
    public void transform(@UnknownNullability Matrix4f context, QuadView input, QuadEmitter output) {
        input.copyTo(output);

        for (int i = 0; i < 4; i++) {
            input.copyPos(i, vec);
            vec.mulPosition(context);
            output.setPos(i, vec);

            if (input.hasNormal(i)) {
                input.copyNormal(i, vec);
                vec.mulDirection(context);
                output.setNormal(i, vec);
            }
        }

        output.emit();
    }
}
