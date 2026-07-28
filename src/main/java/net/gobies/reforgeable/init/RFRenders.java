package net.gobies.reforgeable.init;

import net.gobies.reforgeable.client.ReforgingScreen;
import net.gobies.reforgeable.client.renderer.ReforgingStationRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class RFRenders {

    public static void registerScreen(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(RFMenus.REFORGING_STATION.get(), ReforgingScreen::new));
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(RFBlockEntities.REFORGING_STATION.get(), ReforgingStationRenderer::new);
    }
}
