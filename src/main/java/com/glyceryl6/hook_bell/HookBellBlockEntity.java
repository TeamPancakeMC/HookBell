package com.glyceryl6.hook_bell;

import com.glyceryl6.hook_bell.api.ExtendedEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class HookBellBlockEntity extends BlockEntity {

    public int ticks;
    public boolean shaking;
    private boolean resonating;
    private int resonationTicks;
    private long lastRingTimestamp;
    public Direction clickDirection;
    private List<LivingEntity> nearbyEntities;
    private static final double MAX_RANGE = 128.0D;

    public HookBellBlockEntity(BlockPos pos, BlockState state) {
        super(Main.HOOK_BELL_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            this.updateEntities();
            this.resonationTicks = 0;
            this.clickDirection = Direction.from3DDataValue(type);
            this.ticks = 0;
            this.shaking = true;
            return true;
        } else {
            return super.triggerEvent(id, type);
        }
    }

    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, HookBellBlockEntity blockEntity) {
        if (blockEntity.shaking) {
            ++blockEntity.ticks;
            if (blockEntity.level instanceof ServerLevel serverWorld) {
                for (int i = 0; i < 2; ++i) {
                    double x = blockEntity.getBlockPos().getX() + 0.5D;
                    double y = blockEntity.getBlockPos().getY() + 0.5D;
                    double z = blockEntity.getBlockPos().getZ() + 0.5D;
                    double xOffset = (blockEntity.level.random.nextDouble() - 0.5D) * 2.0D;
                    double yOffset = -blockEntity.level.random.nextDouble();
                    double zOffset = (blockEntity.level.random.nextDouble() - 0.5D) * 2.0D;
                    serverWorld.sendParticles(ParticleTypes.PORTAL, x, y, z, 0, xOffset, yOffset, zOffset, 0.5F);
                }
            }
        }

        if (blockEntity.ticks >= 50) {
            blockEntity.shaking = false;
            blockEntity.ticks = 0;
        }

        if (blockEntity.ticks >= 5 && blockEntity.resonationTicks == 0 && blockEntity.areRaidersNearby()) {
            blockEntity.resonating = true;
            blockEntity.playResonateSound();
        }

        if (blockEntity.resonating) {
            if (blockEntity.resonationTicks < 40) {
                ++blockEntity.resonationTicks;
            } else {
                blockEntity.teleportRaiders();
                blockEntity.resonating = false;
            }
        }
    }

    private void playResonateSound() {
        if (this.level != null) {
            this.level.playSound(null, this.getBlockPos(), SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 1.0F, 0.5F);
        }
    }

    public void onHit(Direction direction) {
        BlockPos blockpos = this.getBlockPos();
        this.clickDirection = direction;
        if (this.shaking) {
            this.ticks = 0;
        } else {
            this.shaking = true;
        }

        if (this.level != null) {
            this.level.blockEvent(blockpos, this.getBlockState().getBlock(), 1, direction.get3DDataValue());
        }
    }

    private void updateEntities() {
        if (this.level != null) {
            BlockPos blockPos = this.getBlockPos();
            if (this.level.getGameTime() > this.lastRingTimestamp + 60L || this.nearbyEntities == null) {
                this.lastRingTimestamp = this.level.getGameTime();
                AABB aabb = (new AABB(blockPos)).inflate(MAX_RANGE);
                this.nearbyEntities = this.level.getEntitiesOfClass(LivingEntity.class, aabb);
            }

            if (!this.level.isClientSide) {
                for (LivingEntity livingEntity : this.nearbyEntities) {
                    if (livingEntity.isAlive() && !livingEntity.isRemoved() && blockPos.closerToCenterThan(livingEntity.position(), MAX_RANGE)) {
                        livingEntity.getBrain().setMemory(MemoryModuleType.HEARD_BELL_TIME, this.level.getGameTime());
                    }
                }
            }
        }
    }

    private boolean areRaidersNearby() {
        for (LivingEntity livingEntity : this.nearbyEntities) {
            if (this.isRaiderWithinRange(livingEntity)) {
                return true;
            }
        }

        return false;
    }

    private boolean areRaidersClose(LivingEntity living) {
        return this.worldPosition.closerToCenterThan(living.position(), MAX_RANGE) && !this.worldPosition.closerToCenterThan(living.position(), 16.0D);
    }

    private void teleportRaiders() {
        this.nearbyEntities.stream().filter(this::isRaiderWithinRange).forEach(this::teleport);
    }

    private boolean isRaiderWithinRange(LivingEntity livingEntity) {
        return livingEntity.isAlive() && !livingEntity.isRemoved() &&
                this.areRaidersClose(livingEntity) && livingEntity.getType().is(EntityTypeTags.RAIDERS) &&
                !hasEntityTypesConfig(livingEntity.getType());
    }

    @SuppressWarnings("resource")
    private void teleport(LivingEntity livingEntity) {
        for (int i = 0; i < 128; ++i) {
            BlockPos blockPos = this.worldPosition;
            double d3 = blockPos.getX() + (livingEntity.getRandom().nextDouble() - 0.5D) * 8;
            double d4 = blockPos.getY() + (double) (livingEntity.getRandom().nextInt(16) - 8);
            double d5 = blockPos.getZ() + (livingEntity.getRandom().nextDouble() - 0.5D) * 8;
            if (livingEntity.randomTeleport(d3, d4, d5, true)) {
                livingEntity.level().playSound(null, livingEntity.xo, livingEntity.yo, livingEntity.zo, SoundEvents.ENDERMAN_TELEPORT, livingEntity.getSoundSource(), 1.0F, 1.0F);
                livingEntity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                break;
            }
        }
    }

    private static boolean hasEntityTypesConfig(EntityType<?> entityType){
        return !MainConfig.HookBellBlackList.get().isEmpty() && ((ExtendedEntityType)entityType).hookBell$isBlacklisted();
    }

}