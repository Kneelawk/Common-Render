package com.kneelawk.krender.model.obj.impl.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public record MtlFile(Map<String, MtlMaterial> materials) {
    public static MtlFile load(Resource resource, ResourceLocation name) throws IOException {
        MtlFile file = new MtlFile(new Object2ObjectLinkedOpenHashMap<>());

        try (BufferedReader reader = resource.openAsReader()) {
            int i = 1;
            String line;

            String currentName = "none";
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) continue;

                String[] strs = line.split(" ");
                if (strs.length < 1) continue;

                try {
                    if (line.startsWith("newmtl ")) {
                        currentName = line.substring("newmtl ".length());
                        file.materials.put(currentName, new MtlMaterial(currentName));
                    } else if (strs[0].equals("Kd")) {
                        checkArgs(strs, currentName, "Kd", 3);
                        float[] diffuseColor = ObjUtils.parseFloats(strs, 1, 3);
                        file.materials.computeIfPresent(currentName, (n, mat) -> mat.withDiffuseColor(diffuseColor));
                    } else if (strs[0].equals("map_Kd")) {
                        checkArgs(strs, currentName, "map_Kd", 1);
                        ResourceLocation diffuseTexture = ResourceLocation.parse(strs[1]);
                        file.materials.computeIfPresent(currentName,
                            (n, mat) -> mat.withDiffuseTexture(diffuseTexture));
                    } else if (strs[0].equals("Ke")) {
                        file.materials.computeIfPresent(currentName, (n, mat) -> mat.withEmissive(true));
                    } else if (strs[0].equals("d")) {
                        checkArgs(strs, currentName, "d", 1);
                        float dissolve = ObjUtils.parseFloat(strs[1], "Bad dissolve value: " + strs[1]);
                        file.materials.computeIfPresent(currentName, (n, mat) -> mat.withDissolve(dissolve));
                    } else if (strs[0].equals("Tr")) {
                        checkArgs(strs, currentName, "Tr", 1);
                        float dissolve = 1f - ObjUtils.parseFloat(strs[1], "Bad transparency value: " + strs[1]);
                        file.materials.computeIfPresent(currentName, (n, mat) -> mat.withDissolve(dissolve));
                    }
                } catch (Exception e) {
                    throw new IOException("Error while parsing '" + name + "' on line " + i + ": " + line, e);
                }

                i++;
            }
        }

        return file;
    }

    private static void checkArgs(String[] strs, String currentName, String commandName, int requiredArgs)
        throws IOException {
        if (strs.length < requiredArgs + 1) {
            throw new IOException(
                "Material '" + currentName + "', command '" + commandName + "' requires " + requiredArgs +
                    " arguments but only has " + (strs.length - 1));
        }
    }
}
