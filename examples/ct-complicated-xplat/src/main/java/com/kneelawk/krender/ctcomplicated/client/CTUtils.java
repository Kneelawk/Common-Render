package com.kneelawk.krender.ctcomplicated.client;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.model.ModelBlockContext;

import static com.kneelawk.krender.ctcomplicated.client.TexDirectionUtils.texDown;
import static com.kneelawk.krender.ctcomplicated.client.TexDirectionUtils.texLeft;
import static com.kneelawk.krender.ctcomplicated.client.TexDirectionUtils.texRight;
import static com.kneelawk.krender.ctcomplicated.client.TexDirectionUtils.texUp;
import static java.lang.Math.abs;

public class CTUtils {
    static final Direction[] DIRECTIONS = Direction.values();
    static final FacePos[] FACES = {
        new FacePos(0.0f + 0f, 0.0f + 0f, 0.5f, 0.5f, 0f),
        new FacePos(0.5f, 0.0f + 0f, 1.0f - 0f, 0.5f, 0f),
        new FacePos(0.0f + 0f, 0.5f, 0.5f, 1.0f - 0f, 0f),
        new FacePos(0.5f, 0.5f, 1.0f - 0f, 1.0f - 0f, 0f)
    };

    static @NotNull Data getData(boolean doCorners, boolean interiorBorder, ModelBlockContext ctx) {
        int[] indices = new int[6];
        for (int i = 0; i < 6; i++) {
            indices[i] = getIndices(doCorners, interiorBorder, ctx.level(), ctx.state(), ctx.pos(), DIRECTIONS[i]);
        }

        return new Data(indices);
    }

    static void render(RenderMaterial material, TextureAtlasSprite[] sprites, QuadEmitter renderTo,
                       Data blockKey) {
        int[] indices = blockKey.indices();
        for (int i = 0; i < 6; i++) {
            int index = indices[i];
            Direction side = DIRECTIONS[i];

            for (int corner = 0; corner < 4; corner++) {
                TextureAtlasSprite sprite = sprites[(index >>> (corner * 3)) & 0x7];
                if (sprite == null) continue;

                FACES[corner].emit(renderTo, side);
                renderTo.spriteBake(sprite, QuadEmitter.BAKE_ROTATE_NONE);
                renderTo.setQuadColor(-1, -1, -1, -1);
                renderTo.setTintIndex(-1);
                renderTo.setMaterial(material);
                renderTo.emit();
            }
        }
    }

    private static int getIndices(boolean doCorners, boolean interiorBorder, BlockAndTintGetter view, BlockState state,
                                  BlockPos pos, Direction normal) {
        int horizontals = getHorizontals(interiorBorder, view, state, pos, normal);
        int verticals = getVerticals(interiorBorder, view, state, pos, normal);
        int corners;
        if (doCorners) corners = getCorners(interiorBorder, view, state, pos, normal) & horizontals & verticals;
        else corners = 0;

        return (corners << 2) | (horizontals ^ corners) | ((verticals ^ corners) << 1);
    }

    private static int getHorizontals(boolean interiorBorder, BlockAndTintGetter view, BlockState state, BlockPos pos,
                                      Direction normal) {
        boolean right = canConnect(interiorBorder, view, state, pos, normal, pos.relative(texRight(normal)));
        boolean left = canConnect(interiorBorder, view, state, pos, normal, pos.relative(texLeft(normal)));

        return (left ? 0x41 : 0) | (right ? 0x208 : 0);
    }

    private static int getVerticals(boolean interiorBorder, BlockAndTintGetter view, BlockState state, BlockPos pos,
                                    Direction normal) {
        boolean up = canConnect(interiorBorder, view, state, pos, normal, pos.relative(texUp(normal)));
        boolean down = canConnect(interiorBorder, view, state, pos, normal, pos.relative(texDown(normal)));

        return (down ? 0x9 : 0) | (up ? 0x240 : 0);
    }

    private static int getCorners(boolean interiorBorder, BlockAndTintGetter view, BlockState state, BlockPos pos,
                                  Direction normal) {
        boolean bl = canConnect(interiorBorder, view, state, pos, normal, pos.relative(texDown(normal)).relative(texLeft(normal)));
        boolean br = canConnect(interiorBorder, view, state, pos, normal, pos.relative(texDown(normal)).relative(texRight(normal)));
        boolean tl = canConnect(interiorBorder, view, state, pos, normal, pos.relative(texUp(normal)).relative(texLeft(normal)));
        boolean tr = canConnect(interiorBorder, view, state, pos, normal, pos.relative(texUp(normal)).relative(texRight(normal)));

        return (bl ? 0x1 : 0) | (br ? 0x8 : 0) | (tl ? 0x40 : 0) | (tr ? 0x200 : 0);
    }

    private static boolean canConnect(boolean interiorBorder, BlockAndTintGetter view, BlockState state, BlockPos pos,
                                      Direction normal,
                                      BlockPos offsetPos) {
        BlockPos outPos = offsetPos.relative(normal);
        BlockState offsetState = view.getBlockState(offsetPos);
        BlockState outState = view.getBlockState(outPos);
        return state.equals(offsetState) &&
            (!interiorBorder || !state.equals(outState));
    }

    public record Data(int[] indices) {
        @Override
        public String toString() {
            return "Data{" +
                "indices=" + Arrays.toString(indices) +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Data data = (Data) o;
            return Arrays.equals(indices, data.indices);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(indices);
        }
    }

    public record FacePos(float left, float bottom, float right, float top, float depth) {
        public void emit(QuadEmitter emitter, Direction face) {
            emitter.square(face, left, bottom, right, top, depth);
            emitter.setUv(0, left, 1f - top);
            emitter.setUv(1, left, 1f - bottom);
            emitter.setUv(2, right, 1f - bottom);
            emitter.setUv(3, right, 1f - top);
        }
    }
}
