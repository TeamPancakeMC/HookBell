package com.glyceryl6.hook_bell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class HookBellBlock extends BellBlock {

    public HookBellBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean attemptToRing(@Nullable Entity entity, Level level, BlockPos pos, @Nullable Direction direction) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof HookBellBlockEntity blockEntity) {
            if (direction != null) {
                direction = level.getBlockState(pos).getValue(FACING);
            }

            blockEntity.onHit(direction);
            level.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0F, 1.0F);
            level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
            return true;
        } else {
            return false;
        }
    }

    @Nullable @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HookBellBlockEntity(pos, state);
    }

    @Nullable @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, Main.HOOK_BELL_BLOCK_ENTITY.get(), HookBellBlockEntity::tick);
    }

}