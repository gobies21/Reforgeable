package net.gobies.reforgeable.init;

import net.gobies.reforgeable.client.renderer.ReforgingStationRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class RFRenders {

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(RFBlockEntities.REFORGING_STATION.get(), ReforgingStationRenderer::new);
    }
}
