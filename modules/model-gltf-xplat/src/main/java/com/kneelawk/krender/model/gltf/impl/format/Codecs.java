package com.kneelawk.krender.model.gltf.impl.format;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public class Codecs {
    static final Codec<float[]> FLOAT_ARRAY = Codec.FLOAT.listOf().xmap(l -> {
        int len = l.size();
        float[] a = new float[len];
        for (int i = 0; i < len; i++) {
            a[i] = l.get(i);
        }
        return a;
    }, a -> {
        int len = a.length;
        List<Float> l = new ObjectArrayList<>(len);
        for (float v : a) {
            l.add(v);
        }
        return l;
    });

    public static MapCodec<OptionalInt> optionalInt(MapCodec<Optional<Integer>> codec) {
        return codec.xmap(o -> o.map(OptionalInt::of).orElse(OptionalInt.empty()), o -> {
            if (o.isPresent()) {
                return Optional.of(o.getAsInt());
            } else {
                return Optional.empty();
            }
        });
    }

    public static MapCodec<OptionalLong> optionalLong(MapCodec<Optional<Long>> codec) {
        return codec.xmap(o -> o.map(OptionalLong::of).orElse(OptionalLong.empty()), o -> {
            if (o.isPresent()) {
                return Optional.of(o.getAsLong());
            } else {
                return Optional.empty();
            }
        });
    }
}
