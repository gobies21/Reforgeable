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
import net.gobies.reforgeable.init.RFBlocks;
import net.gobies.reforgeable.util.QualityUtil;
import net.gobies.reforgeable.util.ReforgeUtil;
import net.minecraft.ChatFormatting;
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

    public static RecipeType<List<ItemStack>> getRecipeTypeInstance() {
        return TYPE;
    }

    public ResourceLocation getPluginUid() {
        return UID;
    }

    public void setupCategory(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        this.background = helper.createBlankDrawable(60, 64);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, getStationBlock());
        this.slotDrawable = helper.getSlotDrawable();

        registration.addRecipeCategories(this);
    }

    @Override
    public void draw(@NotNull List<ItemStack> recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.slotDrawable.draw(guiGraphics, 21, 2);
        this.slotDrawable.draw(guiGraphics, 21, 44);

        guiGraphics.blit(HAMMER_TEXTURE, 22, 24, 0, 0, 16, 16, 16, 16);
    }

    public void loadRecipes(IRecipeRegistration registration) {
        List<List<ItemStack>> recipes = new ArrayList<>();
        List<Item> items = ForgeRegistries.ITEMS.getValues().stream().toList();

        List<ItemStack> gearStack = new ArrayList<>();
        List<ItemStack> materialStack = new ArrayList<>();

        for (Item item : items) {
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
    public @NotNull List<Component> getTooltipStrings(@NotNull List<ItemStack> recipe, @NotNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> tooltips = new ArrayList<>();
        if (mouseX >= 22 && mouseX <= 38 && mouseY >= 24 && mouseY <= 40) {
            tooltips.add(Component.literal("Reforge").withStyle(ChatFormatting.GRAY));
        }
        return tooltips;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, List<ItemStack> recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 22, 3).addItemStack(recipe.get(0));

        List<ItemStack> materialsOnly = recipe.subList(1, recipe.size());
        builder.addSlot(RecipeIngredientRole.INPUT, 22, 45).addIngredients(VanillaTypes.ITEM_STACK, materialsOnly);
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

    public static ItemStack getStationBlock() {
        return RFBlocks.ReforgingStationItem.get().asItem().getDefaultInstance();
    }
}