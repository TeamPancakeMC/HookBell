package com.glyceryl6.hook_bell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class HookBellRenderer implements BlockEntityRenderer<HookBellBlockEntity> {

    private static final ResourceLocation BELL_RESOURCE_LOCATION = new ResourceLocation(Main.MOD_ID, "textures/entity/hook_bell_body.png");
    private final ModelPart bellBody;

    public HookBellRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelpart = context.bakeLayer(Main.HOOK_BELL_LAYER);
        this.bellBody = modelpart.getChild("bell_body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        PartDefinition partDefinition1 = partDefinition.addOrReplaceChild("bell_body", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-3.0F, -6.0F, -3.0F, 6.0F, 7.0F, 6.0F), PartPose.offset(8.0F, 12.0F, 8.0F));
        partDefinition1.addOrReplaceChild("bell_base", CubeListBuilder.create().texOffs(0, 13)
                .addBox(4.0F, 4.0F, 4.0F, 8.0F, 2.0F, 8.0F), PartPose.offset(-8.0F, -12.0F, -8.0F));
        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    public void render(HookBellBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        float f = (float)blockEntity.ticks + partialTick;
        float f1 = 0.0F;
        float f2 = 0.0F;
        if (blockEntity.shaking) {
            float f3 = Mth.sin(f / (float)Math.PI) / (4.0F + f / 3.0F);
            if (blockEntity.clickDirection == Direction.NORTH) {
                f1 = -f3;
            } else if (blockEntity.clickDirection == Direction.SOUTH) {
                f1 = f3;
            } else if (blockEntity.clickDirection == Direction.EAST) {
                f2 = -f3;
            } else if (blockEntity.clickDirection == Direction.WEST) {
                f2 = f3;
            }
        }

        this.bellBody.xRot = f1;
        this.bellBody.zRot = f2;
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entitySolid(BELL_RESOURCE_LOCATION));
        this.bellBody.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }

}