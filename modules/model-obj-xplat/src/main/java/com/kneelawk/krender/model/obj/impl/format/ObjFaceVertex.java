package com.kneelawk.krender.model.obj.impl.format;

import java.io.IOException;

public record ObjFaceVertex(int position, int texture, int normal) {
    public static ObjFaceVertex parse(String str, int vertexIndex) throws IOException {
        String[] split = str.split("/");
        if (split.length < 1) throw new IllegalArgumentException("Cannot parse empty string");
        int position = ObjUtils.parseInt(split[0], "Bad vertex position index in face vertex " + vertexIndex);
        int texture = -1;
        int normal = -1;
        if (split.length >= 2 && !split[1].isBlank()) {
            texture = ObjUtils.parseInt(split[1], "Bad vertex texture index in face vertex " + vertexIndex);
        }
        if (split.length >= 3 && !split[2].isBlank()) {
            normal = ObjUtils.parseInt(split[2], "Bad vertex normal index in face vertex " + vertexIndex);
        }

        return new ObjFaceVertex(position, texture, normal);
    }

    public static ObjFaceVertex[] parseList(String[] strs, int off, int len) throws IOException {
        ObjFaceVertex[] vertices = new ObjFaceVertex[len];
        for (int i = 0; i < len; i++) {
            vertices[i] = parse(strs[i + off], i);
        }
        return vertices;
    }
}
