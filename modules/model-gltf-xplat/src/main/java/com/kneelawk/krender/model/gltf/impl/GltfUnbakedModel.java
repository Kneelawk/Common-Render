package com.kneelawk.krender.model.gltf.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.buffer.PooledQuadEmitter;
import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.material.MaterialManager;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.mesh.MeshBuilder;
import com.kneelawk.krender.engine.api.model.SimpleModelCore;
import com.kneelawk.krender.engine.api.util.ColorUtils;
import com.kneelawk.krender.engine.api.util.transform.MatrixQuadTransform;
import com.kneelawk.krender.model.gltf.impl.format.GltfAccessor;
import com.kneelawk.krender.model.gltf.impl.format.GltfAccessorComponentType;
import com.kneelawk.krender.model.gltf.impl.format.GltfAccessorType;
import com.kneelawk.krender.model.gltf.impl.format.GltfMaterial;
import com.kneelawk.krender.model.gltf.impl.format.GltfMesh;
import com.kneelawk.krender.model.gltf.impl.format.GltfNode;
import com.kneelawk.krender.model.gltf.impl.format.GltfPbrMetallicRoughness;
import com.kneelawk.krender.model.gltf.impl.format.GltfPrimitive;
import com.kneelawk.krender.model.gltf.impl.format.GltfScene;
import com.kneelawk.krender.model.gltf.impl.format.GltfTexture;
import com.kneelawk.krender.model.gltf.impl.format.GltfTextureRef;
import com.kneelawk.krender.model.gltf.impl.format.metadata.GltfMetadata;
import com.kneelawk.krender.model.gltf.impl.format.metadata.MaterialOverride;

public class GltfUnbakedModel implements UnbakedModel {
    private final GltfFile file;
    private final GltfMetadata metadata;
    private final ResourceLocation name;

    public GltfUnbakedModel(GltfFile file, GltfMetadata metadata, ResourceLocation name) {
        this.file = file;
        this.metadata = metadata;
        this.name = name;
    }

    @Override
    public void resolveDependencies(Resolver resolver) {
    }

    @Override
    public TextureSlots.Data getTextureSlots() {
        TextureSlots.Data.Builder builder = new TextureSlots.Data.Builder();
        int imageCount = file.root().images().size();
        for (int i = 0; i < imageCount; i++) {
            try {
                builder.addTexture(String.valueOf(i), getMaterial(i));
            } catch (IOException e) {
                KGltfLog.LOG.error("Error getting image {} for model gltf {}", i, name);
            }
        }

        if (metadata.particle().isPresent()) {
            if (metadata.particle().get().left().isPresent()) {
                int particleIndex = metadata.particle().get().left().get();
                if (particleIndex < 0 || particleIndex > file.root().images().size()) {
                    KGltfLog.LOG.warn(
                        "Model {} has metadata that requests a particle texture image index of {} out of {} images",
                        name, particleIndex, file.root().images().size());
                } else {
                    try {
                        builder.addTexture("particle", getMaterial(particleIndex));
                    } catch (IOException e) {
                        KGltfLog.LOG.error("Error getting particle image {} for model gltf {}", particleIndex, name, e);
                    }
                }
            } else if (metadata.particle().get().right().isPresent()) {
                builder.addTexture("particle",
                    new Material(TextureAtlas.LOCATION_BLOCKS, metadata.particle().get().right().get()));
            }
        } else if (!file.root().images().isEmpty()) {
            try {
                builder.addTexture("particle", getMaterial(0));
            } catch (IOException e) {
                KGltfLog.LOG.error("Error getting default particle image 0 for model gltf {}", name, e);
            }
        }

        return builder.build();
    }

    @Override
    public BakedModel bake(TextureSlots textureSlots, ModelBaker baker, ModelState modelState,
                           boolean hasAmbientOcclusion, boolean useBlockLight, ItemTransforms transforms) {
        SpriteGetter spriteGetter = baker.sprites();
        Function<String, TextureAtlasSprite> spriteLookup = spriteName -> {
            Material mat = textureSlots.getMaterial(spriteName);
            if (mat == null) return spriteGetter.reportMissingReference(spriteName);
            return spriteGetter.get(mat);
        };
        try {
            MeshBuilder meshBuilder = KRenderer.getDefault().meshBuilder();
            TextureAtlasSprite particleTexture = spriteLookup.apply("particle");

            // build transform matrix
            Matrix4f baseTransform = new Matrix4f().identity();
            baseTransform.mul(modelState.getRotation().getMatrix());
            metadata.transformMatrix(baseTransform);

            QuadEmitter emitter = meshBuilder.emitter();
            try (PooledQuadEmitter pooled = emitter.withTransformQuad(
                MatrixQuadTransform.getInstance(), new MatrixQuadTransform.Options(baseTransform, metadata.transformGranularity())
            )) {
                if (file.root().scene().isPresent()) {
                    loadScene(KRenderer.getDefault().materialManager(), pooled, metadata, spriteLookup,
                        file.root().scenes().get(file.root().scene().getAsInt()));
                } else {
                    for (GltfScene scene : file.root().scenes()) {
                        loadScene(KRenderer.getDefault().materialManager(), pooled, metadata, spriteLookup, scene);
                    }
                }
            }

            return KRenderer.getDefault().bakedModelFactory().wrap(
                new SimpleModelCore(meshBuilder.build(), particleTexture, metadata.useAmbientOcclusion(),
                    metadata.gui3d()));
        } catch (Exception e) {
            KGltfLog.LOG.error("Error loading model '{}'", name, e);
            return baker.bake(ResourceLocation.withDefaultNamespace("error/missing"), modelState);
        }
    }

    private void loadScene(MaterialManager manager, QuadEmitter emitter, GltfMetadata metadata,
                           Function<String, TextureAtlasSprite> spriteLookup,
                           GltfScene scene) throws IOException {
        for (int nodeIndex : scene.nodes()) {
            loadNode(manager, emitter, metadata, spriteLookup, MaterialOverride.DEFAULT,
                file.root().nodes().get(nodeIndex), nodeIndex);
        }
    }

    private void loadNode(MaterialManager manager, QuadEmitter emitter, GltfMetadata metadata,
                          Function<String, TextureAtlasSprite> spriteLookup,
                          MaterialOverride material, GltfNode node, int nodeIndex) throws IOException {
        Matrix4f nodeTransforms = new Matrix4f();

        if (node.matrix().length == 16) {
            nodeTransforms.set(node.matrix());
        } else {
            if (node.translation().length == 3) {
                nodeTransforms.translate(node.translation()[0], node.translation()[1], node.translation()[2]);
            }
            if (node.rotation().length == 4) {
                nodeTransforms.rotate(
                    new Quaternionf(node.rotation()[0], node.rotation()[1], node.rotation()[2], node.rotation()[3]));
            }
            if (node.scale().length == 3) {
                nodeTransforms.scale(node.scale()[0], node.scale()[1], node.scale()[2]);
            }
        }

        material = metadata.getNodeMaterial(String.valueOf(nodeIndex), material);
        if (node.name().isPresent()) {
            material = metadata.getNodeMaterial(node.name().get(), material);
        }

        try (PooledQuadEmitter pooled = emitter.withTransformQuad(
            MatrixQuadTransform.getInstance(), new MatrixQuadTransform.Options(nodeTransforms, metadata.transformGranularity())
        )) {
            if (node.mesh().isPresent()) {
                int meshIndex = node.mesh().getAsInt();
                loadMesh(manager, pooled, metadata, spriteLookup, material, file.root().meshes().get(meshIndex),
                    meshIndex);
            }

            for (int child : node.children()) {
                loadNode(manager, pooled, metadata, spriteLookup, material, file.root().nodes().get(child), child);
            }
        }
    }

    private void loadMesh(MaterialManager manager, QuadEmitter emitter, GltfMetadata metadata,
                          Function<String, TextureAtlasSprite> spriteLookup, MaterialOverride material,
                          GltfMesh mesh, int meshIndex) throws IOException {
        material = metadata.getMeshMaterial(String.valueOf(meshIndex), material);
        if (mesh.name().isPresent()) {
            material = metadata.getMeshMaterial(mesh.name().get(), material);
        }

        List<GltfPrimitive> primitives = mesh.primitives();
        for (int i = 0; i < primitives.size(); i++) {
            loadPrimitive(manager, emitter, metadata, spriteLookup, material, primitives.get(i), i, meshIndex);
        }
    }

    private void loadPrimitive(MaterialManager manager, QuadEmitter emitter, GltfMetadata metadata,
                               Function<String, TextureAtlasSprite> spriteLookup,
                               @NotNull MaterialOverride override, GltfPrimitive primitive, int primitiveIndex,
                               int meshIndex) throws IOException {
        if (primitive.mode().isPresent() && primitive.mode().getAsInt() != 4) {
            KGltfLog.LOG.warn(
                "Model {}, mesh {}, primitive {} contains non-triangle primitives. This primitive will be ignored.",
                name, meshIndex, primitiveIndex);
            return;
        }

        RenderMaterial material = manager.defaultMaterial();
        TextureAtlasSprite sprite = null;
        int texCoordIndex = 0;
        int colorFactor = -1;
        if (primitive.material().isPresent()) {
            int materialIndex = primitive.material().getAsInt();
            GltfMaterial gltfMaterial = file.root().materials().get(materialIndex);

            if (gltfMaterial.pbrMetallicRoughness().isPresent()) {
                GltfPbrMetallicRoughness mr = gltfMaterial.pbrMetallicRoughness().get();
                if (mr.baseColorTexture().isPresent()) {
                    GltfTextureRef ref = mr.baseColorTexture().get();
                    sprite = getTexture(spriteLookup, ref);
                    texCoordIndex = ref.texCoord();
                }
                float[] baseColorFactorF = mr.baseColorFactor();
                colorFactor = ColorUtils.toArgb(baseColorFactorF[0], baseColorFactorF[1], baseColorFactorF[2],
                    baseColorFactorF[3]);
            }

            if (gltfMaterial.emissiveFactor().length == 3) {
                for (int i = 0; i < 3; i++) {
                    if (gltfMaterial.emissiveFactor()[i] > 0.0001) {
                        material = manager.materialFinder().copyFrom(material).setEmissive(true).find();
                    }
                }
            }

            if (gltfMaterial.alphaMode().isPresent()) {
                material = manager.materialFinder().copyFrom(material)
                    .setBlendMode(gltfMaterial.alphaMode().get().getBlendMode()).find();
            }

            // apply the per-material material overrides under mesh and node specific overrides
            if (gltfMaterial.name().isPresent()) {
                override = override.overlay(metadata.getMaterial(gltfMaterial.name().get(), MaterialOverride.DEFAULT));
            }
            override = override.overlay(metadata.getMaterial(String.valueOf(materialIndex), MaterialOverride.DEFAULT));
        }

        if (sprite == null) {
            sprite = spriteLookup.apply("primitive missing texture");
            KGltfLog.LOG.warn("Model {}, mesh {}, primitive {} specifies no texture. Using a missing-texture.", name,
                meshIndex, primitiveIndex);
        }

        // finally apply the base material override under everything else
        override = override.overlay(metadata.materialOverride());

        material = override.toRenderMaterial(manager, material);
        if (override.colorFactor().isPresent()) {
            colorFactor = override.colorFactor().getAsInt();
        }

        Map<String, Integer> attributes = primitive.attributes();
        if (!attributes.containsKey("POSITION")) {
            KGltfLog.LOG.warn(
                "Model {}, mesh {}, primitive {} has no POSITION attribute. This primitive will be ignored.", name,
                meshIndex, primitiveIndex);
            return;
        }
        GltfFile.Accessor positionAccessorHolder = file.getAccessor(attributes.get("POSITION"));
        BufferAccess positionBuffer = positionAccessorHolder.buffer();
        GltfAccessor positionAccessor = positionAccessorHolder.accessor();
        int positionSize = positionAccessor.componentType().getBytes() * positionAccessor.type().getComponentCount();
        if (positionAccessor.componentType() != GltfAccessorComponentType.FLOAT ||
            positionAccessor.type() != GltfAccessorType.VEC3) {
            throw new IOException("Model " + name + ", mesh " + meshIndex + ", primitive " + primitiveIndex +
                " has invalid position type " + positionAccessor.componentType() + " x " + positionAccessor.type());
        }

        BufferAccess texCoordBuffer = null;
        int texCoordSize = -1;
        if (attributes.containsKey("TEXCOORD_" + texCoordIndex)) {
            GltfFile.Accessor texCoordAccessorHolder = file.getAccessor(attributes.get("TEXCOORD_" + texCoordIndex));
            texCoordBuffer = texCoordAccessorHolder.buffer();
            GltfAccessor texCoordAccessor = texCoordAccessorHolder.accessor();
            texCoordSize = texCoordAccessor.componentType().getBytes() * texCoordAccessor.type().getComponentCount();

            if (texCoordAccessor.componentType() != GltfAccessorComponentType.FLOAT ||
                texCoordAccessor.type() != GltfAccessorType.VEC2) {
                throw new IOException("Model " + name + ", mesh " + meshIndex + ", primitive " + primitiveIndex +
                    " has invalid texture-coordinate type " + texCoordAccessor.componentType() + " x " +
                    texCoordAccessor.type());
            }
        }

        BufferAccess normalBuffer = null;
        int normalSize = -1;
        if (attributes.containsKey("NORMAL")) {
            GltfFile.Accessor normalAccessorHolder = file.getAccessor(attributes.get("NORMAL"));
            normalBuffer = normalAccessorHolder.buffer();
            GltfAccessor normalAccessor = normalAccessorHolder.accessor();
            normalSize = normalAccessor.componentType().getBytes() * normalAccessor.type().getComponentCount();

            if (normalAccessor.componentType() != GltfAccessorComponentType.FLOAT ||
                normalAccessor.type() != GltfAccessorType.VEC3) {
                throw new IOException("Model " + name + ", mesh " + meshIndex + ", primitive " + primitiveIndex +
                    " has invalid normal type " + normalAccessor.componentType() + " x " + normalAccessor.type());
            }
        }

        Vector3f vec3 = new Vector3f();
        Vector2f vec2 = new Vector2f();

        if (primitive.indices().isPresent()) {
            GltfFile.Accessor indexAccessorHolder = file.getAccessor(primitive.indices().getAsInt());
            BufferAccess indexBuffer = indexAccessorHolder.buffer();
            GltfAccessor indexAccessor = indexAccessorHolder.accessor();

            // must be scalar
            GltfAccessorComponentType indexComponentType = indexAccessor.componentType();
            int indexSize = indexComponentType.getBytes();
            int indexCount = (int) indexAccessor.count();
            for (int i = 0; i < indexCount; i++) {
                int index = indexBuffer.getInt(i * indexSize, indexComponentType);
                putQuad(emitter, material, sprite, colorFactor, positionBuffer, positionSize, texCoordBuffer,
                    texCoordSize,
                    normalBuffer, normalSize, vec3, vec2, i, index);
            }
        } else {
            int vertexCount = (int) positionAccessor.count();
            for (int i = 0; i < vertexCount; i++) {
                putQuad(emitter, material, sprite, colorFactor, positionBuffer, positionSize, texCoordBuffer,
                    texCoordSize, normalBuffer, normalSize, vec3, vec2, i, i);
            }
        }
    }

    private void putQuad(QuadEmitter emitter, RenderMaterial material, TextureAtlasSprite sprite, int colorFactor,
                         BufferAccess positionBuffer, int positionSize, BufferAccess texCoordBuffer, int texCoordSize,
                         BufferAccess normalBuffer, int normalSize, Vector3f vec3, Vector2f vec2, int i, int index) {
        int vertexIndex = i % 3;

        putVertex(emitter, index, vertexIndex, positionBuffer, positionSize, vec3, texCoordBuffer, texCoordSize,
            vec2, normalBuffer, normalSize);

        if (vertexIndex == 2) {
            // make degenerate quads for now
            putVertex(emitter, index, 3, positionBuffer, positionSize, vec3, texCoordBuffer, texCoordSize,
                vec2, normalBuffer, normalSize);
        }

        if (vertexIndex == 2) {
            emitter.setQuadColor(colorFactor, colorFactor, colorFactor, colorFactor);
            emitter.setMaterial(material);
            emitter.spriteBake(sprite, QuadEmitter.BAKE_ROTATE_NONE);
            emitter.emit();
        }
    }

    private static void putVertex(QuadEmitter emitter, int index, int vertexIndex, BufferAccess positionBuffer,
                                  int positionSize, Vector3f vec3, BufferAccess texCoordBuffer, int texCoordSize,
                                  Vector2f vec2, BufferAccess normalBuffer, int normalSize) {
        positionBuffer.getVec3f(index * positionSize, vec3);
        emitter.setPos(vertexIndex, vec3);

        if (texCoordBuffer != null) {
            texCoordBuffer.getVec2f(index * texCoordSize, vec2);
            emitter.setUv(vertexIndex, vec2);
        }

        if (normalBuffer != null) {
            normalBuffer.getVec3f(index * normalSize, vec3);
            emitter.setNormal(vertexIndex, vec3);
        }
    }

    private TextureAtlasSprite getTexture(Function<String, TextureAtlasSprite> imageLookup,
                                          GltfTextureRef ref) {
        GltfTexture texture = file.root().textures().get(ref.index());
        return imageLookup.apply(String.valueOf(texture.source()));
    }

    private Material getMaterial(int imageIndex) throws IOException {
        return new Material(TextureAtlas.LOCATION_BLOCKS, getImage(imageIndex));
    }

    private ResourceLocation getImage(int index) throws IOException {
        ResourceLocation external = file.getImageLocation(index);
        if (external != null) return external;
        return KGltfConstants.getImageName(name, index);
    }
}
