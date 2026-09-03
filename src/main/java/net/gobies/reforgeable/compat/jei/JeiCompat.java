package net.gobies.reforgeable.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.gobies.reforgeable.config.CommonConfig;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
@SuppressWarnings("unused")
public class JeiCompat implements IModPlugin {

    private final ReforgeableJeiPlugin plugin = new ReforgeableJeiPlugin();

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return plugin.getPluginUid();
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        if (!CommonConfig.JEI_COMPAT.get()) return;
        plugin.setupCategory(registration);
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        if (!CommonConfig.JEI_COMPAT.get()) return;
        registration.addRecipeCatalyst(ReforgeableJeiPlugin.getStationBlock(), ReforgeableJeiPlugin.getRecipeTypeInstance());
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        if (!CommonConfig.JEI_COMPAT.get()) return;
        plugin.loadRecipes(registration);
    }
}