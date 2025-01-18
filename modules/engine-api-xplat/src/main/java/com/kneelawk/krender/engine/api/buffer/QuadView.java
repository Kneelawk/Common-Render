package com.kneelawk.krender.engine.api.buffer;

import org.jetbrains.annotations.Nullable;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import com.kneelawk.krender.engine.api.RendererDependent;
import com.kneelawk.krender.engine.api.material.RenderMaterial;

/**
 * A view of a buffered quad.
 */
public interface QuadView extends RendererDependent {
    /**
     * The number of 32-bit integers in a vanilla vertex.
     */
    int VANILLA_VERTEX_STRIDE = DefaultVertexFormat.BLOCK.getVertexSize() / 4;

    /**
     * The number of 32-bit integers in a vanilla quad.
     */
    int VANILLA_QUAD_STRIDE = VANILLA_VERTEX_STRIDE * 4;

    /**
     * Entirely copies this view's current quad to the given quad-emitter instance.
     * <p>
     * Note: this does not call {@link QuadEmitter#emit()} for you. You must do that yourself when you are ready to
     * actually emit the quad.
     *
     * @param target the quad-emitter to copy quad data to.
     */
    void copyTo(QuadEmitter target);

    /**
     * Gets the x coordinate of the given vertex.
     *
     * @param vertexIndex the index of the vertex.
     * @return the x coordinate.
     */
    float getX(int vertexIndex);

    /**
     * Gets the y coordinate of the given vertex.
     *
     * @param vertexIndex the index of the vertex.
     * @return the y coordinate.
     */
    float getY(int vertexIndex);

    /**
     * Gets the z coordinate of the given vertex.
     *
     * @param vertexIndex the index of the vertex.
     * @return the z coordinate.
     */
    float getZ(int vertexIndex);

    /**
     * Gets the vertex position vector component specified.
     *
     * @param vertexIndex     the index of the vertex.
     * @param coordinateIndex the index of the vector component.
     * @return the position vector component.
     */
    float getPosByIndex(int vertexIndex, int coordinateIndex);

    /**
     * Copies the specified vertex position to a vector.
     * <p>
     * If {@code target} is not {@code null}, then the position is copied to it and it is returned.
     * Otherwise, the position is copied to a new vector and the new vector is returned.
     *
     * @param vertexIndex the index of the vertex.
     * @param target      the optional vector to copy the position to.
     * @return the vector that the position was copied to.
     */
    Vector3f copyPos(int vertexIndex, @Nullable Vector3f target);

    /**
     * Gets the color of the specified vertex in ARGB format.
     *
     * @param vertexIndex the index of the vertex.
     * @return the color of the specified vertex.
     */
    int getColor(int vertexIndex);

    /**
     * Gets the horizontal texture coordinate of the given vertex.
     *
     * @param vertexIndex the index of the vertex.
     * @return the horizontal texture coordinate.
     */
    float getU(int vertexIndex);

    /**
     * Gets the vertical texture coordinate of the given vertex.
     *
     * @param vertexIndex the index of the vertex.
     * @return the vertical texture coordinate.
     */
    float getV(int vertexIndex);

    /**
     * Gets the vertex texture coordinate vector component specified.
     *
     * @param vertexIndex     the index of the vertex.
     * @param coordinateIndex the index of the vector component.
     * @return the texture coordinate vector component.
     */
    float getUvByIndex(int vertexIndex, int coordinateIndex);

    /**
     * Copies the specified texture coordinate to a vector.
     * <p>
     * If {@code target} is not {@code null}, then the texture coordinates are coped to it and it is returned.
     * Otherwise, the texture coordinates are copied to a new vector and the new vector is returned.
     *
     * @param vertexIndex the index of the vertex.
     * @param target      the optional vector to copy the texture coordinates to.
     * @return the vector that the texture coordinates were copied to.
     */
    Vector2f copyUv(int vertexIndex, @Nullable Vector2f target);

    /**
     * Gets the vertex's minimum block brightness.
     *
     * @param vertexIndex the index of the vertex.
     * @return the minimum block brightness.
     */
    int getLightmap(int vertexIndex);

    /**
     * Gets whether the given vertex has a normal associated with it.
     *
     * @param vertexIndex the index of the vertex.
     * @return whether the given vertex has a normal.
     */
    boolean hasNormal(int vertexIndex);

    /**
     * Gets the normal x component of the given vertex.
     *
     * @param vertexIndex the index of the vertex.
     * @return the normal x component.
     */
    float getNormalX(int vertexIndex);

    /**
     * Gets the normal x component of the given vertex.
     *
     * @param vertexIndex the index of the vertex.
     * @return the normal x component.
     */
    float getNormalY(int vertexIndex);

    /**
     * Gets the normal x component of the given vertex.
     *
     * @param vertexIndex the index of the vertex.
     * @return the normal x component.
     */
    float getNormalZ(int vertexIndex);

    /**
     * Gets the vertex normal vector component specified.
     *
     * @param vertexIndex     the index of the vertex.
     * @param coordinateIndex the index of the vector component.
     * @return the normal vector component.
     */
    float getNormalByIndex(int vertexIndex, int coordinateIndex);

    /**
     * Copies the specified normal to a vector.
     * <p>
     * If {@code target} is not {@code null}, then the normal is copied to it and it is returned.
     * Otherwise, the normal is copied to a new vector and the new vector is returned. However, this only applies
     * if this vertex actually has a normal. If this vertex does not have a normal, then the {@code target} vertex
     * is not modified and {@code null} is returned.
     *
     * @param vertexIndex the index of the vertex.
     * @param target      the optional vector to copy the normal to.
     * @return the vector that the normal was copied to or {@code null} if the specified vertex has no normal.
     */
    @Nullable Vector3f copyNormal(int vertexIndex, @Nullable Vector3f target);

    /**
     * {@return the current quad's cull face}
     */
    @Nullable Direction getCullFace();

    /**
     * Gets the quad's lighting face.
     * <p>
     * A quad's lighting face is the direction the quad is primarily facing in terms of vanilla lighting calculations.
     *
     * @return the quad's lighting face.
     */
    Direction getLightFace();

    /**
     * {@return the current quad's nominal face}
     */
    @Nullable Direction getNominalFace();

    /**
     * {@return the lazily-computed face normal based on geometry}
     */
    Vector3f getFaceNormal();

    /**
     * {@return this quad's material}
     */
    RenderMaterial getMaterial();

    /**
     * {@return the quad tint index serialized with the quad}
     */
    int getTintIndex();

    /**
     * {@return the user-defined tag associated with this quad}
     */
    int getTag();

    /**
     * Copies the vanilla properties of this quad into the given array in vanilla format.
     *
     * @param target      the array to copy vanilla properties into.
     * @param targetIndex the index within the array to start copying to.
     */
    void toVanilla(int[] target, int targetIndex);

    /**
     * Creates a vanilla representation of this quad, using the given sprite.
     *
     * @param sprite the sprite to use in the baked quad.
     * @return the vanilla representation of this quad.
     * @see com.kneelawk.krender.engine.api.texture.SpriteFinder
     */
    BakedQuad toBakedQuad(TextureAtlasSprite sprite);
}
