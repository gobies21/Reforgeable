package net.gobies.reforgeable.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.helper.QualityHelper;
import net.gobies.reforgeable.network.PacketHandler;
import net.gobies.reforgeable.network.ReforgeMessage;
import net.gobies.reforgeable.util.QualityUtil;
import net.gobies.reforgeable.util.ReforgeUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;


public class ReforgingScreen extends AbstractContainerScreen<ReforgingMenu> {

    private static final ResourceLocation SCREEN_TEXTURE = new ResourceLocation(Reforgeable.MOD_ID, "textures/gui/reforging_station.png");
    private static final ResourceLocation HAMMER_TEXTURE = new ResourceLocation(Reforgeable.MOD_ID, "textures/gui/hammer_button.png");

    private int pressAnimationTicks = 0;

    public ReforgingScreen(ReforgingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 0;
        this.titleLabelY = 0;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 73;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.pressAnimationTicks > 0) {
            this.pressAnimationTicks--;
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        int titleWidth = this.font.width(this.title);
        int centeredTitleX = (this.imageWidth - titleWidth) / 2;
        graphics.drawString(this.font, this.title, centeredTitleX, 6, 4210752, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        graphics.blit(SCREEN_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        ItemStack gear = this.menu.getSlot(0).getItem();
        ItemStack material = this.menu.getSlot(1).getItem();
        boolean isRecipeValid = ReforgeUtil.getReforgeMaterial(gear, material);

        int buttonX = x + 80;
        int buttonY = y + 39;
        int size = 16;

        if (!gear.isEmpty() && material.isEmpty()) {
            List<Item> hints = ReforgeUtil.getHintItems(gear);
            if (!hints.isEmpty() && java.util.Objects.requireNonNull(this.minecraft).player != null) {
                Item activeHint = hints.get((this.minecraft.player.tickCount / 20) % hints.size());
                if (activeHint != null) {
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();

                    ItemStack stack = new ItemStack(activeHint);
                    var renderer = this.minecraft.getItemRenderer();
                    var model = renderer.getModel(stack, this.minecraft.level, this.minecraft.player, 0);

                    graphics.setColor(1.0F, 1.0F, 1.0F, 0.2F);
                    graphics.pose().pushPose();

                    graphics.pose().translate(x + 80, y + 59, 100.0F);
                    graphics.pose().translate(8.0F, 8.0F, 0.0F);
                    graphics.pose().scale(16.0F, -16.0F, 16.0F);

                    model.applyTransform(ItemDisplayContext.GUI, graphics.pose(), false);
                    graphics.pose().translate(-0.5F, -0.5F, -0.5F);

                    renderer.renderModelLists(model, stack, 15728880, OverlayTexture.NO_OVERLAY, graphics.pose(), graphics.bufferSource().getBuffer(RenderType.itemEntityTranslucentCull(InventoryMenu.BLOCK_ATLAS)));

                    graphics.bufferSource().endBatch();
                    graphics.pose().popPose();
                    graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.disableBlend();
                }
            }
        }

        if (isRecipeValid) {
            graphics.blit(HAMMER_TEXTURE, buttonX, buttonY, 0, this.pressAnimationTicks > 0 ? size : 0, size, size, 32, 32);
            if (this.pressAnimationTicks == 0 && mouseX >= buttonX && mouseX < buttonX + size && mouseY >= buttonY && mouseY < buttonY + size) {
                RenderSystem.enableBlend();
                graphics.fill(buttonX, buttonY, buttonX + size, buttonY + size, 0x40FFFFFF);
                RenderSystem.disableBlend();
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int buttonX = x + 80;
        int buttonY = y + 39;
        int size = 16;

        ItemStack gearStack = this.menu.getSlot(0).getItem();
        ItemStack materialStack = this.menu.getSlot(1).getItem();

        if (ReforgeUtil.getReforgeMaterial(gearStack, materialStack) && this.pressAnimationTicks == 0) {
            if (mouseX >= buttonX && mouseX < buttonX + size && mouseY >= buttonY && mouseY < buttonY + size) {
                String qualityName = QualityUtil.getQuality(gearStack);

                if (!qualityName.isEmpty() && !qualityName.equalsIgnoreCase("none")) {
                    QualityHelper.Quality quality = QualityUtil.getQualityForStack(gearStack, qualityName);

                    if (quality != null) {
                        List<Component> tooltipLines = QualityUtil.getQualityTooltips(quality);

                        if (!tooltipLines.isEmpty()) {
                            graphics.renderComponentTooltip(this.font, tooltipLines, mouseX, mouseY);
                        }
                    }
                }
            }
        }

        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        int buttonX = x + 80;
        int buttonY = y + 39;
        int size = 16;

        if (mouseX >= buttonX && mouseX < buttonX + size && mouseY >= buttonY && mouseY < buttonY + size) {
            ItemStack gearStack = this.menu.getSlot(0).getItem();
            ItemStack materialStack = this.menu.getSlot(1).getItem();

            if (!ReforgeUtil.getReforgeMaterial(gearStack, materialStack) || this.pressAnimationTicks > 0) {
                return false;
            }

            Objects.requireNonNull(Objects.requireNonNull(this.minecraft).player).playSound(net.minecraft.sounds.SoundEvents.ANVIL_USE, 1.0F, 1.0F);
            this.pressAnimationTicks = 2;

            PacketHandler.INSTANCE.sendToServer(new ReforgeMessage());
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}