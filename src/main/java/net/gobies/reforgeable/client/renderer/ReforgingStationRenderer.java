package net.gobies.reforgeable.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.gobies.reforgeable.block.ReforgingStationBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ReforgingStationRenderer implements BlockEntityRenderer<ReforgingStationBlockEntity> {

    public ReforgingStationRenderer(BlockEntityRendererProvider.Context ignoreContext) {}

    @Override
    public void render(ReforgingStationBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        Container inventory = blockEntity.getInventory();
        ItemStack gearStack = inventory.getItem(0);
        ItemStack materialStack = inventory.getItem(1);

        if (gearStack.isEmpty() && materialStack.isEmpty()) {
            return;
        }

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        int ambientLight = LevelRenderer.getLightColor(Objects.requireNonNull(blockEntity.getLevel()), blockEntity.getBlockPos().above());

        if (!gearStack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.45D, 1.01D, 0.35D);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(20.0F));
            poseStack.scale(0.65F, 0.65F, 0.65F);
            itemRenderer.renderStatic(gearStack, ItemDisplayContext.FIXED, ambientLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
            poseStack.popPose();
        }

        if (!materialStack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.60D, 1.01D, 0.75D);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(100.0F));
            poseStack.scale(0.4F, 0.4F, 0.4F);
            itemRenderer.renderStatic(materialStack, ItemDisplayContext.FIXED, ambientLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
            poseStack.popPose();
        }
    }
}