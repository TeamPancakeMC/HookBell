package com.glyceryl6.hook_bell.mixin;

import com.glyceryl6.hook_bell.api.ExtendedEntityType;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityType.class)
public class EntityTypeMixin implements ExtendedEntityType {
    @Unique private boolean hookBell$isBlacklisted;

    @Override
    public boolean hookBell$isBlacklisted() {
        return this.hookBell$isBlacklisted;
    }

    @Override
    public void hookBell$setIsBlacklisted(boolean isBlacklisted) {
        this.hookBell$isBlacklisted = isBlacklisted;
    }
}
