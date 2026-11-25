package net.gobies.reforgeable.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.gobies.reforgeable.Reforgeable;
import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.helper.QualityHelper;
import net.gobies.reforgeable.init.RFBlocks;
import net.gobies.reforgeable.util.Quality;
import net.gobies.reforgeable.util.QualityUtil;
import net.gobies.reforgeable.util.ReforgeUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReforgeableJeiPlugin implements IRecipeCategory<List<ItemStack>> {

    private static final ResourceLocation UID = new ResourceLocation(Reforgeable.MOD_ID, "reforging");
    private static final RecipeType<List<ItemStack>> TYPE = new RecipeType<>(UID, (Class<List<ItemStack>>) (Class<?>) List.class);
    private static final ResourceLocation HAMMER_TEXTURE = new ResourceLocation(Reforgeable.MOD_ID, "textures/gui/single_hammer_button.png");

    private IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;

    public void setupCategory(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        this.background = helper.createBlankDrawable(160, 180);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, getStationBlock());
        this.slotDrawable = helper.getSlotDrawable();

        registration.addRecipeCategories(this);
    }

    public void loadRecipes(IRecipeRegistration registration) {
        List<List<ItemStack>> recipes = new ArrayList<>();
        List<ItemStack> gearStack = new ArrayList<>();
        List<ItemStack> materialStack = new ArrayList<>();

        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            ItemStack stack = new ItemStack(item);
            if (stack.isEmpty()) continue;

            if (QualityUtil.isValidQualityItem(stack) && !QualityUtil.isBlacklisted(stack)) {
                gearStack.add(stack);
            } else {
                materialStack.add(stack);
            }
        }

        for (ItemStack gear : gearStack) {
            List<ItemStack> recipeElements = new ArrayList<>();
            recipeElements.add(gear);

            for (ItemStack material : materialStack) {
                if (ReforgeUtil.getReforgeMaterial(gear, material)) {
                    recipeElements.add(material);
                }
            }

            if (recipeElements.size() > 1) {
                recipes.add(recipeElements);
            }
        }

        registration.addRecipes(TYPE, recipes);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, List<ItemStack> recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 72, 6).addItemStack(recipe.get(0));

        List<ItemStack> materialsOnly = recipe.subList(1, recipe.size());
        builder.addSlot(RecipeIngredientRole.INPUT, 72, 48).addIngredients(VanillaTypes.ITEM_STACK, materialsOnly);
    }

    @Override
    public void draw(@NotNull List<ItemStack> recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.slotDrawable.draw(guiGraphics, 71, 5);
        guiGraphics.blit(HAMMER_TEXTURE, 72, 27, 0, 0, 16, 16, 16, 16);
        this.slotDrawable.draw(guiGraphics, 71, 47);

        Font font = Minecraft.getInstance().font;
        List<Quality> qualities = QualityUtil.getQualities(recipe.get(0)).stream().filter(q -> q.weight() >= 0).toList();
        List<List<Quality>> rows = splitRows(qualities, font);
        int currentY = 70;

        for (List<Quality> row : rows) {
            int currentX = (160 - getRowWidth(row, font)) / 2;

            for (Quality quality : row) {
                Component text = getQualityLabel(quality);
                guiGraphics.drawString(font, text, currentX, currentY, 0xFFFFFF, false);
                currentX += font.width(text) + 5;
            }

            currentY += 10;
        }
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(@NotNull List<ItemStack> recipe, @NotNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> tooltips = new ArrayList<>();
        if (mouseX >= 72 && mouseX <= 88 && mouseY >= 27 && mouseY <= 43) {
            tooltips.add(Component.literal("Reforge"));
            return tooltips;
        }

        ItemStack gearStack = recipe.get(0);
        Font font = Minecraft.getInstance().font;
        List<Quality> qualities = QualityUtil.getQualities(gearStack).stream().filter(q -> q.weight() >= 0).toList();
        List<List<Quality>> rows = splitRows(qualities, font);
        int currentY = 70;

        for (List<Quality> row : rows) {
            int currentX = (160 - getRowWidth(row, font)) / 2;

            for (Quality quality : row) {
                int textWidth = font.width(getQualityLabel(quality));

                if (mouseX >= currentX && mouseX <= currentX + textWidth && mouseY >= currentY && mouseY <= currentY + 9) {
                    tooltips.addAll(QualityUtil.getQualityTooltips(quality, gearStack));
                    if (quality.weight() <= 0) {
                        addTreasureTooltips(tooltips, qualities);
                    } else {
                        addChanceTooltips(tooltips, quality, qualities);
                    }
                    return tooltips;
                }

                currentX += textWidth + 5;
            }

            currentY += 10;
        }

        return tooltips;
    }

    private void addChanceTooltips(List<Component> tooltips, Quality target, List<Quality> allQualities) {
        int maxRareWeight = CommonConfig.MAX_WEIGHT.get();
        int totalWeight = 0;
        int rareWeight = 0;

        for (Quality quality : allQualities) {
            if (quality.weight() > 0) {
                totalWeight += quality.weight();
                if (quality.weight() <= maxRareWeight) {
                    rareWeight += quality.weight();
                }
            }
        }

        tooltips.add(Component.empty());

        double baseChance = (target.weight() * 100.0) / totalWeight;
        tooltips.add(Component.literal(String.format("Chance: %.2f%%", baseChance)).withStyle(ChatFormatting.GRAY));

        double luckScale = CommonConfig.LUCK_SCALE.get();
        if (luckScale <= 0) return;

        double luckFactor = 1.0 + luckScale;
        double difference = QualityHelper.getLuckDifference(baseChance, target.weight(), maxRareWeight, rareWeight, luckFactor);
        ChatFormatting color = difference > 0 ? ChatFormatting.GREEN : difference < 0 ? ChatFormatting.RED : ChatFormatting.GRAY;
        tooltips.add(Component.literal(String.format("Luck Factor: %+.2f%%", difference)).withStyle(color));
    }

    private void addTreasureTooltips(List<Component> tooltips, List<Quality> allQualities) {
        long treasureCount = allQualities.stream().filter(q -> q.weight() <= 0).count();
        if (treasureCount <= 0) return;

        double baseChance = (CommonConfig.TREASURE_CHANCE.get() * 100.0) / treasureCount;

        tooltips.add(Component.empty());
        tooltips.add(Component.literal("Only found in loot").withStyle(ChatFormatting.GOLD));
        tooltips.add(Component.literal(String.format("Chance: %.2f%%", baseChance)).withStyle(ChatFormatting.GRAY));

        double treasureLuck = CommonConfig.TREASURE_LUCK_FACTOR.get();
        if (treasureLuck <= 0) return;

        double luckIncrease = (treasureLuck * 100.0) / treasureCount;
        tooltips.add(Component.literal(String.format("Luck Factor: %+.2f%%", luckIncrease)).withStyle(ChatFormatting.GREEN));
    }

    private List<List<Quality>> splitRows(List<Quality> qualities, Font font) {
        List<List<Quality>> rows = new ArrayList<>();
        List<Quality> currentRow = new ArrayList<>();

        for (Quality quality : qualities) {
            currentRow.add(quality);
            if (currentRow.size() > 1 && getRowWidth(currentRow, font) > 156) {
                currentRow.remove(currentRow.size() - 1);
                rows.add(currentRow);
                currentRow = new ArrayList<>(List.of(quality));
            }
        }

        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }
        return rows;
    }

    private int getRowWidth(List<Quality> row, Font font) {
        int total = (row.size() - 1) * 5;
        for (Quality quality : row) {
            total += font.width(getQualityLabel(quality));
        }
        return total;
    }

    private Component getQualityLabel(Quality quality) {
        return Component.translatable("item.quality." + quality.name().toLowerCase()).withStyle(quality.color());
    }

    @Override
    public @NotNull RecipeType<List<ItemStack>> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("container.reforgeable.reforging_station");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    public static RecipeType<List<ItemStack>> getRecipeTypeInstance() {
        return TYPE;
    }

    public ResourceLocation getPluginUid() {
        return UID;
    }

    public static ItemStack getStationBlock() {
        return RFBlocks.ReforgingStationItem.get().asItem().getDefaultInstance();
    }
}