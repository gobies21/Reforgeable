package net.gobies.reforgeable;

import com.mojang.logging.LogUtils;
import net.gobies.reforgeable.client.ReforgingScreen;
import net.gobies.reforgeable.compat.curios.CuriosCompat;
import net.gobies.reforgeable.compat.curios.CuriosEvents;
import net.gobies.reforgeable.config.ClientConfig;
import net.gobies.reforgeable.config.CommonConfig;
import net.gobies.reforgeable.config.QualityConfig;
import net.gobies.reforgeable.events.QualityEvents;
import net.gobies.reforgeable.init.*;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

@Mod(Reforgeable.MOD_ID)
public class Reforgeable {
    public static final String MOD_ID = "reforgeable";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Reforgeable(IEventBus modBus, ModContainer container) {
        RFBlocks.register(modBus);
        RFBlockEntities.register(modBus);
        RFMenus.register(modBus);
        RFDataComponents.register(modBus);
        QualityEvents.register();
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::registerScreens);
        modBus.addListener(this::registerRenders);
        modBus.addListener(this::addCreativeTab);
        container.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "reforgeable/common.toml");
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, "reforgeable/client.toml");
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(QualityConfig::loadJsonConfig);
        if (CuriosCompat.isLoaded()) {
            CuriosEvents.loadCompat();
        }
    }

    public void registerScreens(RegisterMenuScreensEvent event) {
        event.register(RFMenus.REFORGING_STATION.get(), ReforgingScreen::new);
    }

    private void registerRenders(final EntityRenderersEvent.RegisterRenderers event) {
        RFRenders.registerRenderers(event);
    }

    private void addCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(RFBlocks.ReforgingStationItem.get());
        }
    }
}
