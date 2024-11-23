package com.kneelawk.krender.model.obj.impl.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import com.kneelawk.krender.model.guard.api.ModelGuards;
import com.kneelawk.krender.model.obj.impl.KObjConstants;
import com.kneelawk.krender.model.obj.impl.KObjLog;

public record ObjFile(List<ObjVertexPosition> positions, List<ObjVertexTexture> textures, List<ObjVertexNormal> normals,
                      List<ObjFace> faces, Map<String, MtlMaterial> materials) {
    public static ObjFile load(Resource resource, ResourceLocation name, ResourceManager manager, ModelGuards guards)
        throws IOException {
        Map<String, MtlMaterial> materials = new Object2ObjectLinkedOpenHashMap<>();
        List<ObjVertexPosition> positions = new ObjectArrayList<>();
        List<ObjVertexTexture> textures = new ObjectArrayList<>();
        List<ObjVertexNormal> normals = new ObjectArrayList<>();
        List<ObjFace> faces = new ObjectArrayList<>();

        try (BufferedReader reader = resource.openAsReader()) {
            int i = 1;
            String line;

            String currentMaterial = "none";
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) continue;

                String[] strs = line.split(" ");
                if (strs.length < 1) continue;

                try {
                    if (strs[0].equals("mtllib")) {
                        ResourceLocation mtlLocation = ResourceLocation.parse(strs[1]);
                        Optional<Resource> mtlResource =
                            guards.getResource(manager, KObjConstants.LOADER_ID, mtlLocation);
                        if (mtlResource.isPresent()) {
                            materials.putAll(MtlFile.load(mtlResource.get(), mtlLocation).materials());
                        } else {
                            KObjLog.LOG.error(
                                "Obj file '{}' referenced missing mtl file '{}'. This will likely lead to missing textures.",
                                name, mtlLocation);
                        }
                    } else if (strs[0].equals("usemtl")) {
                        checkArgs(strs, "usemtl", 1);
                        currentMaterial = strs[1];
                    } else if (strs[0].equals("v")) {
                        checkArgs(strs, "v", 3);
                        float[] position = ObjUtils.parseFloats(strs, 1, 3);
                        positions.add(new ObjVertexPosition(position[0], position[1], position[2]));
                    } else if (strs[0].equals("vt")) {
                        checkArgs(strs, "vt", 2);
                        float[] texture = ObjUtils.parseFloats(strs, 1, 2);
                        textures.add(new ObjVertexTexture(texture[0], texture[1]));
                    } else if (strs[0].equals("vn")) {
                        checkArgs(strs, "vn", 3);
                        float[] normal = ObjUtils.parseFloats(strs, 1, 3);
                        normals.add(new ObjVertexNormal(normal[0], normal[1], normal[2]));
                    } else if (strs[0].equals("f")) {
                        ObjFaceVertex[] vertices = ObjFaceVertex.parseList(strs, 1, strs.length - 1);
                        if (vertices.length < 3) throw new IOException("Obj face has fewer than 3 vertices");
                        if (vertices.length > 4) {
                            KObjLog.LOG.error(
                                "Obj file '{}' face on line {} has {} vertices, but minecraft supports a maximum of 4 vertices. Some vertices will not be rendered.",
                                name, i, vertices.length);
                        }

                        faces.add(new ObjFace(vertices, currentMaterial));
                    }
                } catch (Exception e) {
                    throw new IOException("Error while parsing '" + name + "' on line " + i + ": " + line, e);
                }

                i++;
            }
        }

        return new ObjFile(positions, textures, normals, faces, materials);
    }

    private static void checkArgs(String[] strs, String commandName, int requiredArgs)
        throws IOException {
        if (strs.length < requiredArgs + 1) {
            throw new IOException(
                "Obj command '" + commandName + "' requires " + requiredArgs +
                    " arguments but only has " + (strs.length - 1));
        }
    }
}
