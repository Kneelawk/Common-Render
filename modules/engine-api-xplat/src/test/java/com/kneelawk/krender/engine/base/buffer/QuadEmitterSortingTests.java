package com.kneelawk.krender.engine.base.buffer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.joml.Vector3f;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.mesh.MeshBuilder;
import com.kneelawk.krender.engine.backend.shim.ShimRenderer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuadEmitterSortingTests {
    @BeforeAll
    static void bootstrap() {
        // minecraft static initializers go brrrrrr
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void sorting1() {
        MeshBuilder builder = ShimRenderer.INSTANCE.meshBuilder();
        QuadEmitter emitter = builder.emitter();
        emitter.asVertexEmitter().addVertex(1f, 0f, 0f).addVertex(1f, 1f, 0f).addVertex(0f, 1f, 0f)
            .addVertex(0f, 0f, 1f);

        emitter.sortVertices();

        assertEquals(new Vector3f(0f, 1f, 0f), emitter.copyPos(0, null));
        assertEquals(new Vector3f(0f, 0f, 1f), emitter.copyPos(1, null));
        assertEquals(new Vector3f(1f, 0f, 0f), emitter.copyPos(2, null));
        assertEquals(new Vector3f(1f, 1f, 0f), emitter.copyPos(3, null));
    }
}
