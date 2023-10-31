package com.glyceryl6.hook_bell.api;

public interface ExtendedEntityType {
    boolean hookBell$isBlacklisted();
    void hookBell$setIsBlacklisted(boolean isBlacklisted);
}
