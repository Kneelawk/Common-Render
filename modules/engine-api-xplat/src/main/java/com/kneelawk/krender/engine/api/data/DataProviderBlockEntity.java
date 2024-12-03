package com.kneelawk.krender.engine.api.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.kneelawk.krender.engine.api.model.ModelBlockContext;

/**
 * Abstract class extended by {@link BlockEntity}s that provide data to
 * {@link net.minecraft.client.resources.model.BakedModel}s.
 * <p>
 * Extending this class provides a platform independent way to provide data to block models. Otherwise, you will need
 * to mixin into your own base classes and implement {@code RenderDataBlockEntity.getRenderData()} on Fabric, and
 * {@code IBlockEntityExtension.getModelData()} on NeoForge.
 * <p>
 * On Fabric, you can simply return your {@link DataHolder} from {@code RenderDataBlockEntity.getRenderData()}.
 * <p>
 * On NeoForge, you must return a {@code ModelData} containing your {@link DataHolder} under the
 * {@code ModelDataProperties.DATA_HOLDER_MODEL_PROPERTY} property.
 * <p>
 * {@link DataProviderBlockEntity} is an abstract class instead of an interface because it needs to extend
 * {@link BlockEntity} in order to be able to override the correct method on Fabric backends.
 *
 * @see ModelBlockContext#renderDataHolder()
 */
public abstract class DataProviderBlockEntity extends BlockEntity {
    /**
     * {@link DataProviderBlockEntity} constructor.
     *
     * @param type       the type of block entity.
     * @param pos        the position of the block entity.
     * @param blockState the block state of the block where the block entity is.
     */
    public DataProviderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    /**
     * {@return the custom data from this block entity to be passed to its associated baked model}
     *
     * @see ModelBlockContext#renderDataHolder()
     */
    public abstract DataHolder getRenderDataHolder();
}
