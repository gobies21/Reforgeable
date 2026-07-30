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
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ReforgingStationRenderer implements BlockEntityRenderer<ReforgingStationBlockEntity> {

    public ReforgingStationRenderer(BlockEntityRendererProvider.Context ignoreContext) {}

    @Override
    public void render(ReforgingStationBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        Container inventory = blockEntity.getInventory();
        ItemStack gearStack = inventory.getItem(0);
        ItemStack materialStack = inventory.getItem(1);
        if (gearStack.isEmpty() && materialStack.isEmpty()) return;

        BlockState state = blockEntity.getBlockState();
        if (!state.hasProperty(HorizontalDirectionalBlock.FACING)) return;
        Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        int ambientLight = LevelRenderer.getLightColor(Objects.requireNonNull(blockEntity.getLevel()), blockEntity.getBlockPos().above());

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot() + 90.0F));
        poseStack.translate(-0.5D, 0.0D, -0.5D);

        if (!gearStack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.30D, 0.92D, 0.55D);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(300.0F));
            poseStack.scale(0.50F, 0.50F, 0.50F);
            itemRenderer.renderStatic(gearStack, ItemDisplayContext.FIXED, ambientLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
            poseStack.popPose();
        }

        if (!materialStack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.75D, 0.92D, 0.25D);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(250.0F));
            poseStack.scale(0.45F, 0.45F, 0.45F);
            itemRenderer.renderStatic(materialStack, ItemDisplayContext.FIXED, ambientLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}