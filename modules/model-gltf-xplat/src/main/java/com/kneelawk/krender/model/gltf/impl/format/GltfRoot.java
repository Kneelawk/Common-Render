package com.kneelawk.krender.model.gltf.impl.format;

import java.util.List;
import java.util.OptionalInt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GltfRoot(OptionalInt scene, List<GltfScene> scenes, List<GltfNode> nodes, List<GltfBuffer> buffers,
                       List<GltfBufferView> bufferViews, List<GltfMesh> meshes,
                       List<GltfTexture> textures, List<GltfImage> images, List<GltfMaterial> materials) {
    public static final Codec<GltfRoot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.optionalInt(Codec.INT.optionalFieldOf("scene")).forGetter(GltfRoot::scene),
        GltfScene.CODEC.listOf().fieldOf("scenes").forGetter(GltfRoot::scenes),
        GltfNode.CODEC.listOf().fieldOf("nodes").forGetter(GltfRoot::nodes),
        GltfBuffer.CODEC.listOf().fieldOf("buffers").forGetter(GltfRoot::buffers),
        GltfBufferView.CODEC.listOf().fieldOf("bufferViews").forGetter(GltfRoot::bufferViews),
        GltfMesh.CODEC.listOf().fieldOf("meshes").forGetter(GltfRoot::meshes),
        GltfTexture.CODEC.listOf().fieldOf("textures").forGetter(GltfRoot::textures),
        GltfImage.CODEC.listOf().fieldOf("images").forGetter(GltfRoot::images),
        GltfMaterial.CODEC.listOf().fieldOf("materials").forGetter(GltfRoot::materials)
    ).apply(instance, GltfRoot::new));
}
