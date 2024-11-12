package com.kneelawk.krender.model.gltf.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import org.joml.Vector2f;
import org.joml.Vector3f;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.mesh.MeshBuilder;
import com.kneelawk.krender.engine.api.model.SimpleModelCore;
import com.kneelawk.krender.engine.api.util.ColorUtil;
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
    public Collection<ResourceLocation> getDependencies() {
        return List.of();
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> resolver) {}

    @Override
    public @Nullable BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter,
                                     ModelState state) {
        System.out.println("========");
        System.out.println("Loading " + name);
        Function<ResourceLocation, TextureAtlasSprite> blockSprite =
            resourceLocation -> spriteGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, resourceLocation));
        try {
            MeshBuilder meshBuilder = KRenderer.getDefault().meshBuilder();
            TextureAtlasSprite particleTexture = blockSprite.apply(MissingTextureAtlasSprite.getLocation());
            // TODO: push transforms
            QuadEmitter emitter = meshBuilder.emitter();

            if (file.root().scene().isPresent()) {
                loadScene(emitter, blockSprite, file.root().scenes().get(file.root().scene().getAsInt()));
            } else {
                for (GltfScene scene : file.root().scenes()) {
                    loadScene(emitter, blockSprite, scene);
                }
            }

            return KRenderer.getDefault().bakedModelFactory()
                .wrap(new SimpleModelCore(meshBuilder.build(), particleTexture, true, true));
        } catch (Exception e) {
            KGltfLog.LOG.error("Error loading model '{}'", name, e);
            return baker.bake(ModelBakery.MISSING_MODEL_LOCATION, state);
        } finally {
            System.out.println("Finished " + name);
            System.out.println("========");
        }
    }

    private void loadScene(QuadEmitter emitter, Function<ResourceLocation, TextureAtlasSprite> blockSprite,
                           GltfScene scene) throws IOException {
        for (int nodeIndex : scene.nodes()) {
            loadNode(emitter, blockSprite, file.root().nodes().get(nodeIndex));
        }
    }

    private void loadNode(QuadEmitter emitter, Function<ResourceLocation, TextureAtlasSprite> blockSprite,
                          GltfNode node) throws IOException {
        // TODO: push transforms

        if (node.mesh().isPresent()) {
            int meshIndex = node.mesh().getAsInt();
            loadMesh(emitter, blockSprite, file.root().meshes().get(meshIndex), meshIndex);
        }

        for (int child : node.children()) {
            loadNode(emitter, blockSprite, file.root().nodes().get(child));
        }
    }

    private void loadMesh(QuadEmitter emitter, Function<ResourceLocation, TextureAtlasSprite> blockSprite,
                          GltfMesh mesh, int meshIndex) throws IOException {
        List<GltfPrimitive> primitives = mesh.primitives();
        for (int i = 0; i < primitives.size(); i++) {
            loadPrimitive(emitter, blockSprite, primitives.get(i), i, meshIndex);
        }
    }

    private void loadPrimitive(QuadEmitter emitter, Function<ResourceLocation, TextureAtlasSprite> blockSprite,
                               GltfPrimitive primitive, int primitiveIndex, int meshIndex) throws IOException {
        if (primitive.mode().isPresent() && primitive.mode().getAsInt() != 4) {
            KGltfLog.LOG.warn(
                "Model {}, mesh {}, primitive {} contains non-triangle primitives. This primitive will be ignored.",
                name, meshIndex, primitiveIndex);
            return;
        }

        RenderMaterial material = KRenderer.getDefault().materialManager().defaultMaterial();
        TextureAtlasSprite sprite = blockSprite.apply(MissingTextureAtlasSprite.getLocation());
        int texCoordIndex = 0;
        int colorFactor = -1;
        if (primitive.material().isPresent()) {
            GltfMaterial gltfMaterial = file.root().materials().get(primitive.material().getAsInt());
            if (gltfMaterial.pbrMetallicRoughness().isPresent()) {
                GltfPbrMetallicRoughness mr = gltfMaterial.pbrMetallicRoughness().get();
                if (mr.baseColorTexture().isPresent()) {
                    GltfTextureRef ref = mr.baseColorTexture().get();
                    sprite = getTexture(blockSprite, ref);
                    texCoordIndex = ref.texCoord();
                }
                float[] baseColorFactorF = mr.baseColorFactor();
                colorFactor = ColorUtil.toArgb(baseColorFactorF[0], baseColorFactorF[1], baseColorFactorF[2],
                    baseColorFactorF[3]);
            }
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

    private TextureAtlasSprite getTexture(Function<ResourceLocation, TextureAtlasSprite> blockSprite,
                                          GltfTextureRef ref)
        throws IOException {
        GltfTexture texture = file.root().textures().get(ref.index());
        return blockSprite.apply(getImage(texture.source()));
    }

    private ResourceLocation getImage(int index) throws IOException {
        ResourceLocation external = file.getImageLocation(index);
        if (external != null) return external;
        return KGltfConstants.getImageName(name, index);
    }
}
