package com.kneelawk.krender.model.gltf.impl.format;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum GltfAccessorComponentType {
    SIGNED_BYTE(5120, true, 1),
    UNSIGNED_BYTE(5121, false, 1),
    SIGNED_SHORT(5122, true, 2),
    UNSIGNED_SHORT(5123, false, 2),
    UNSIGNED_INT(5125, false, 4),
    FLOAT(5126, true, 4);

    public static final Codec<GltfAccessorComponentType> CODEC = Codec.INT.comapFlatMap(i -> {
        GltfAccessorComponentType byId = byId(i);
        if (byId == null) return DataResult.error(() -> "Invalid accessor component type: " + i);
        return DataResult.success(byId);
    }, GltfAccessorComponentType::getId);

    private final int id;
    private final boolean signed;
    private final int bytes;

    GltfAccessorComponentType(int id, boolean signed, int bytes) {
        this.id = id;
        this.signed = signed;
        this.bytes = bytes;
    }

    public int getId() {
        return id;
    }

    public boolean isSigned() {
        return signed;
    }

    public int getBytes() {
        return bytes;
    }

    public static @Nullable GltfAccessorComponentType byId(int id) {
        return switch (id) {
            case 5120 -> SIGNED_BYTE;
            case 5121 -> UNSIGNED_BYTE;
            case 5122 -> SIGNED_SHORT;
            case 5123 -> UNSIGNED_SHORT;
            case 5125 -> UNSIGNED_INT;
            case 5126 -> FLOAT;
            default -> null;
        };
    }
}
