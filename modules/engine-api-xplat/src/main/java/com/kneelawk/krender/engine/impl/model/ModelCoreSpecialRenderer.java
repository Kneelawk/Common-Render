package com.kneelawk.krender.engine.impl.model;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import com.kneelawk.krender.engine.api.KRenderer;
import com.kneelawk.krender.engine.api.buffer.PooledQuadEmitter;
import com.kneelawk.krender.engine.api.buffer.QuadEmitter;
import com.kneelawk.krender.engine.api.material.RenderMaterial;
import com.kneelawk.krender.engine.api.model.BakedModelCore;
import com.kneelawk.krender.engine.api.model.ModelItemContext;
import com.kneelawk.krender.engine.api.util.transform.LightingQuadTransform;
import com.kneelawk.krender.engine.api.util.transform.PoseQuadTransform;

/**
 * A {@link SpecialModelRenderer} designed for rendering {@link BakedModelCore}s.
 */
public class ModelCoreSpecialRenderer implements SpecialModelRenderer<ModelCoreSpecialRenderer.Input> {
    public static final ModelCoreSpecialRenderer INSTANCE = new ModelCoreSpecialRenderer();

    private final RandomSource random = RandomSource.create();

    @Override
    public void render(@Nullable ModelCoreSpecialRenderer.Input input, ItemDisplayContext displayContext,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay,
                       boolean hasFoilType) {
        if (input == null) return;

        KRenderer renderer = KRenderer.tryGetDefault();
        if (renderer == null) return;

        QuadEmitter emitter =
            renderer.converter().fromVertexConsumerProvider(mat -> bufferSource.getBuffer(getItemRenderType(mat)));

        try (PooledQuadEmitter pooled = emitter.withTransformQuad(new PoseQuadTransform.Options(poseStack.last()),
            PoseQuadTransform.getInstance());
             PooledQuadEmitter pooled2 = pooled.withTransformQuad(new LightingQuadTransform.Options(packedLight),
                 LightingQuadTransform.getInstance())) {
            input.core().renderItem(pooled2, new ModelItemContext(input.stack(), () -> {
                random.setSeed(42);
                return random;
            }));
        }
    }

    @Override
    public @Nullable Input extractArgument(ItemStack stack) {
        return null;
    }

    public record Input(BakedModelCore<?> core, ItemStack stack) {}

    public static RenderType getItemRenderType(RenderMaterial material) {
        return switch (material.getBlendMode()) {
            case DEFAULT, TRANSLUCENT -> Sheets.translucentItemSheet();
            case SOLID -> Sheets.solidBlockSheet();
            case CUTOUT_MIPPED, CUTOUT -> Sheets.cutoutBlockSheet();
        };
    }
}
