package com.glyceryl6.hook_bell;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;
import java.util.List;

public class MainConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> HookBellBlackList;

    static {
        BUILDER.push("Blocks");
        HookBellBlackList = BUILDER.comment("Add mobs that Hook Bells don't work on. " +
                        "To do so, enter the namespace ID of the mob, like 'minecraft:zombie, minecraft:skeleton'")
                .defineList("hookBellBlackList", Lists.newArrayList(), (itemRaw) -> itemRaw instanceof String);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path))
                .sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }

}