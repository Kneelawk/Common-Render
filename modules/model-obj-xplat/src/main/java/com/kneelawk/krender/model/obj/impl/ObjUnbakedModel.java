package com.kneelawk.krender.model.obj.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import org.joml.Matrix4f;

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
import com.kneelawk.krender.engine.api.buffer.PooledQuadEmitter;
import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.material.MaterialFinder;
import com.kneelawk.krender.engine.api.material.MaterialManager;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.mesh.MeshBuilder;
import com.kneelawk.krender.engine.api.model.SimpleModelCore;
import com.kneelawk.krender.engine.api.util.ColorUtil;
import com.kneelawk.krender.engine.api.util.transform.MatrixQuadTransform;
import com.kneelawk.krender.model.obj.impl.format.MtlMaterial;
import com.kneelawk.krender.model.obj.impl.format.ObjFace;
import com.kneelawk.krender.model.obj.impl.format.ObjFaceVertex;
import com.kneelawk.krender.model.obj.impl.format.ObjFile;
import com.kneelawk.krender.model.obj.impl.format.ObjVertexNormal;
import com.kneelawk.krender.model.obj.impl.format.ObjVertexPosition;
import com.kneelawk.krender.model.obj.impl.format.ObjVertexTexture;
import com.kneelawk.krender.model.obj.impl.format.metadata.MaterialOverride;
import com.kneelawk.krender.model.obj.impl.format.metadata.ObjMetadata;

public class ObjUnbakedModel implements UnbakedModel {
    private final ObjFile file;
    private final ObjMetadata metadata;
    private final ResourceLocation name;

    public ObjUnbakedModel(ObjFile file, ObjMetadata metadata, ResourceLocation name) {
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
        Function<ResourceLocation, TextureAtlasSprite> blockSprite =
            resourceLocation -> spriteGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, resourceLocation));

        try {
            MaterialManager manager = KRenderer.getDefault().materialManager();
            MaterialFinder finder = manager.materialFinder();
            MeshBuilder builder = KRenderer.getDefault().meshBuilder();
            TextureAtlasSprite particle = blockSprite.apply(MissingTextureAtlasSprite.getLocation());

            if (metadata.particle().isPresent()) {
                particle = blockSprite.apply(metadata.particle().get());
            } else {
                for (MtlMaterial material : file.materials().values()) {
                    if (material.diffuseTexture() != null) {
                        particle = blockSprite.apply(material.diffuseTexture());
                        break;
                    }
                }
            }

            ObjMaterial defau =
                new ObjMaterial(manager.missingMaterial(), blockSprite.apply(MissingTextureAtlasSprite.getLocation()),
                    -1);
            Map<String, ObjMaterial> materials = new Object2ObjectLinkedOpenHashMap<>();
            for (MtlMaterial material : file.materials().values()) {
                finder.clear();
                finder.setEmissive(material.emissive());
                MaterialOverride override = metadata.getMaterial(material.name(), MaterialOverride.DEFAULT);
                RenderMaterial renderMaterial = override
                    .toRenderMaterial(manager, finder.find());
                ResourceLocation diffuseTexture = material.diffuseTexture() != null ? material.diffuseTexture() :
                    MissingTextureAtlasSprite.getLocation();

                int color;
                if (override.colorFactor().isPresent()) {
                    color = override.colorFactor().getAsInt();
                } else if (material.diffuseColor().length == 3) {
                    color = ColorUtil.toArgb(material.diffuseColor()[0], material.diffuseColor()[1],
                        material.diffuseColor()[2], material.dissolve());
                } else {
                    color = -1;
                }

                materials.put(material.name(),
                    new ObjMaterial(renderMaterial, blockSprite.apply(diffuseTexture), color));
            }

            QuadEmitter root = builder.emitter();

            Matrix4f transform = new Matrix4f();
            transform.mul(state.getRotation().getMatrix());
            metadata.transformMatrix(transform);

            try (PooledQuadEmitter emitter = root.withTransformQuad(
                new MatrixQuadTransform.Options(transform, metadata.transformGranularity()),
                MatrixQuadTransform.getInstance())) {
                for (ObjFace face : file.faces()) {
                    int vertCount = Math.min(face.vertices().length, 4);
                    for (int i = 0; i < vertCount; i++) {
                        ObjFaceVertex vertex = face.vertices()[i];
                        emitVertex(emitter, vertex, i);
                    }

                    if (vertCount < 4) {
                        emitVertex(emitter, face.vertices()[2], 3);
                    }

                    ObjMaterial material = materials.getOrDefault(face.materialName(), defau);
                    emitter.setQuadColor(material.color(), material.color(), material.color(), material.color());
                    emitter.spriteBake(material.sprite(), QuadEmitter.BAKE_ROTATE_NONE);
                    emitter.setMaterial(material.material());

                    emitter.emit();
                }
            }

            return KRenderer.getDefault().bakedModelFactory()
                .wrap(new SimpleModelCore(builder.build(), particle, metadata.useAmbientOcclusion(), metadata.gui3d()));
        } catch (Exception e) {
            KObjLog.LOG.error("Error loading model '{}'", name, e);
            return baker.bake(ModelBakery.MISSING_MODEL_LOCATION, state);
        }
    }

    private void emitVertex(PooledQuadEmitter emitter, ObjFaceVertex vertex, int i) {
        ObjVertexPosition position = file.positions().get(vertex.position() - 1);
        emitter.setPos(i, position.x(), position.y(), position.z());

        if (vertex.texture() != -1) {
            ObjVertexTexture texture = file.textures().get(vertex.texture() - 1);
            emitter.setUv(i, texture.u(), 1f - texture.v());
        }

        if (vertex.normal() != -1) {
            ObjVertexNormal normal = file.normals().get(vertex.normal() - 1);
            emitter.setNormal(i, normal.x(), normal.y(), normal.z());
        }
    }

    private record ObjMaterial(RenderMaterial material, TextureAtlasSprite sprite, int color) {}
}
