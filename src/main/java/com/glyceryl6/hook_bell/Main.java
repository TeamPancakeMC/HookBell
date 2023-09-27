package com.glyceryl6.hook_bell;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.Set;

@Mod(Main.MOD_ID)
@SuppressWarnings("ConstantConditions")
public class Main {

    public static final String MOD_ID = "hook_bell";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<Block> HOOK_BELL_BLOCK = BLOCKS.register("hook_bell", () -> new HookBellBlock(BlockBehaviour.Properties.copy(Blocks.BELL)));
    public static final RegistryObject<Item> HOOK_BELL_ITEM = ITEMS.register("hook_bell", () -> new ItemNameBlockItem(HOOK_BELL_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<HookBellBlockEntity>> HOOK_BELL_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("hook_bell",
            () -> BlockEntityType.Builder.of(HookBellBlockEntity::new, HOOK_BELL_BLOCK.get()).build(null));

    public static final ModelLayerLocation HOOK_BELL_LAYER = new ModelLayerLocation(HOOK_BELL_ITEM.getId(), "main");

    public static final Set<EntityType<?>> ENTITY_TYPES_IN_CONFIG = new HashSet<>();

    public Main() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MainConfig.SPEC, "hook_bell.toml");
        MainConfig.loadConfig(MainConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve("hook_bell.toml").toString());
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(this::buildContents);
        modEventBus.addListener(this::registerRenderers);
        modEventBus.addListener(this::registerLayers);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        for (String id : MainConfig.HookBellBlackList.get()) {
            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(id));
            if (entityType != null) ENTITY_TYPES_IN_CONFIG.add(entityType);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(HOOK_BELL_ITEM);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(HOOK_BELL_BLOCK_ENTITY.get(), HookBellRenderer::new);
    }

    @OnlyIn(Dist.CLIENT)
    public void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HOOK_BELL_LAYER, HookBellRenderer::createBodyLayer);
    }

}