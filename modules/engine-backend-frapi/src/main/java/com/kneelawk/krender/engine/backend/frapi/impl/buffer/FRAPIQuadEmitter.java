package com.kneelawk.krender.engine.backend.frapi.impl.buffer;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.kneelawk.krender.engine.backend.frapi.api.ConversionUtils;
import com.kneelawk.krender.engine.backend.frapi.impl.FRAPIRenderer;
import com.kneelawk.krender.engine.base.buffer.BaseQuadFormat;
import com.kneelawk.krender.engine.base.buffer.RootQuadEmitter;

public class FRAPIQuadEmitter extends RootQuadEmitter {
    private final QuadEmitter frapiEmitter;
    private final Vector3f vec3 = new Vector3f();
    private final Vector2f vec2 = new Vector2f();

    public FRAPIQuadEmitter(QuadEmitter frapiEmitter) {
        super(FRAPIRenderer.INSTNACE);
        this.frapiEmitter = frapiEmitter;
        begin(new int[BaseQuadFormat.TOTAL_STRIDE], 0);
    }

    @Override
    public void emitDirectly() {
        QuadEmitter target = frapiEmitter;

        Vector3f vec3 = this.vec3;
        Vector2f vec2 = this.vec2;

        target.cullFace(getCullFace());
        target.nominalFace(getNominalFace());
        target.material(ConversionUtils.toFabric(getMaterial()));
        target.tintIndex(getTintIndex());
        target.tag(getTag());

        for (int i = 0; i < 4; i++) {
            copyPos(i, vec3);
            target.pos(i, vec3);

            target.color(i, getColor(i));

            copyUv(i, vec2);
            target.uv(i, vec2);

            target.lightmap(i, getLightmap(i));

            if (hasNormal(i)) {
                copyNormal(i, vec3);
                target.normal(i, vec3);
            }
        }

        target.emit();
    }
}
