package com.kneelawk.krender.model.gltf.impl.format;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum GltfBufferViewTarget {
    ARRAY_BUFFER(34962),
    ELEMENT_ARRAY_BUFFER(34963);
    
    public static final Codec<GltfBufferViewTarget> CODEC = Codec.INT.comapFlatMap(i -> {
        GltfBufferViewTarget target = byId(i);
        if (target == null) return DataResult.error(() -> "No buffer view target with id " + i);
        return DataResult.success(target);
    }, GltfBufferViewTarget::getId);

    private final int id;

    GltfBufferViewTarget(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static @Nullable GltfBufferViewTarget byId(int id) {
        return switch (id) {
            case 34962 -> ARRAY_BUFFER;
            case 34963 -> ELEMENT_ARRAY_BUFFER;
            default -> null;
        };
    }
}
